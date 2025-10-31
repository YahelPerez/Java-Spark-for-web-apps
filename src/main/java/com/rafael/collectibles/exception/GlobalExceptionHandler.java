package com.rafael.collectibles.exception;

import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler for the application.
 * Provides consistent error handling and response formatting.
 */
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

    public static void configure() {
        spark.Spark.exception(ResourceNotFoundException.class, GlobalExceptionHandler::handleNotFound);
        spark.Spark.exception(ValidationException.class, GlobalExceptionHandler::handleValidation);
        spark.Spark.exception(Exception.class, GlobalExceptionHandler::handleInternalError);
    }

    private static void handleNotFound(Exception exception, Request req, Response res) {
        logger.warn("Resource not found: {}", exception.getMessage());
        res.status(404);
        
        Map<String, Object> model = new HashMap<>();
        model.put("errorTitle", "Not Found");
        model.put("errorMessage", exception.getMessage());
        
        Map<String, Object> layout = new HashMap<>();
        layout.put("pageTitle", "Not Found");
        layout.put("content", templateEngine.render(new ModelAndView(model, "error.mustache")));
        res.body(templateEngine.render(new ModelAndView(layout, "layout.mustache")));
    }

    private static void handleValidation(ValidationException exception, Request req, Response res) {
        logger.warn("Validation error: {} - {}", exception.getField(), exception.getMessage());
        res.status(400);
        
        Map<String, Object> model = new HashMap<>();
        model.put("errorTitle", "Invalid Input");
        model.put("errorMessage", exception.getMessage());
        model.put("field", exception.getField());
        
        Map<String, Object> layout = new HashMap<>();
        layout.put("pageTitle", "Invalid Input");
        layout.put("content", templateEngine.render(new ModelAndView(model, "error.mustache")));
        res.body(templateEngine.render(new ModelAndView(layout, "layout.mustache")));
    }

    private static void handleInternalError(Exception exception, Request req, Response res) {
        logger.error("Internal server error", exception);
        res.status(500);
        
        Map<String, Object> model = new HashMap<>();
        model.put("errorTitle", "Internal Server Error");
        model.put("errorMessage", "An unexpected error occurred. Please try again later.");
        
        Map<String, Object> layout = new HashMap<>();
        layout.put("pageTitle", "Error");
        layout.put("content", templateEngine.render(new ModelAndView(model, "error.mustache")));
        res.body(templateEngine.render(new ModelAndView(layout, "layout.mustache")));
    }
}