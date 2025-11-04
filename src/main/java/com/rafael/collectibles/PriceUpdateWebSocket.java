package com.rafael.collectibles;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebSocket
public class PriceUpdateWebSocket {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final Gson gson = new Gson();
    
    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
    }
    
    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }
    
    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
    
    public static void broadcastPriceUpdate(String itemId, double newPrice) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "priceUpdate");
        message.addProperty("itemId", itemId);
        message.addProperty("price", newPrice);
        
        String jsonMessage = gson.toJson(message);
        
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(jsonMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}