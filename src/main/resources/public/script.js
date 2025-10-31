document.addEventListener('DOMContentLoaded', () => {
    // Mantener la funcionalidad existente de la tabla
    const itemListTable = document.querySelector('#item-list-table');
    if (itemListTable) {
        itemListTable.addEventListener('click', function(event) {
            if (event.target.matches('a.item-name-link')) {
                event.preventDefault();
                const itemId = event.target.getAttribute('href').split('/')[2];
                window.location.href = '/items/' + itemId;
            }
        });
    }

    // Validación del formulario de ofertas
    const bidForm = document.querySelector('.bid-form');
    if (bidForm) {
        const bidAmount = document.getElementById('bidAmount');
        const bidderName = document.getElementById('bidderName');

        // Helper para mostrar errores
        const showError = (element, message) => {
            const formGroup = element.closest('.form-group');
            formGroup.classList.add('error');
            
            let errorMessage = formGroup.querySelector('.form-error-message');
            if (!errorMessage) {
                errorMessage = document.createElement('div');
                errorMessage.className = 'form-error-message';
                formGroup.appendChild(errorMessage);
            }
            errorMessage.textContent = message;
            element.setAttribute('aria-invalid', 'true');
        };

        // Helper para limpiar errores
        const clearError = (element) => {
            const formGroup = element.closest('.form-group');
            formGroup.classList.remove('error');
            const errorMessage = formGroup.querySelector('.form-error-message');
            if (errorMessage) {
                errorMessage.remove();
            }
            element.removeAttribute('aria-invalid');
        };

        // Validación en tiempo real
        bidderName.addEventListener('input', () => {
            if (bidderName.value.trim()) {
                clearError(bidderName);
            }
        });

        bidAmount.addEventListener('input', () => {
            const minBid = parseFloat(bidAmount.min);
            const amount = parseFloat(bidAmount.value);
            
            if (!isNaN(amount) && amount > minBid) {
                clearError(bidAmount);
            }
        });

        // Validación al enviar
        bidForm.addEventListener('submit', (e) => {
            let isValid = true;

            // Validar nombre del ofertante
            if (!bidderName.value.trim()) {
                showError(bidderName, 'Por favor, ingresa tu nombre');
                isValid = false;
            }

            // Validar monto de la oferta
            const amount = parseFloat(bidAmount.value);
            const minBid = parseFloat(bidAmount.min);
            
            if (isNaN(amount)) {
                showError(bidAmount, 'Por favor, ingresa un número válido');
                isValid = false;
            } else if (amount <= minBid) {
                showError(bidAmount, `La oferta debe ser mayor a $${minBid.toLocaleString('es')}`);
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    // Formateo de precios
    document.querySelectorAll('.price, .current-price').forEach(el => {
        const price = parseFloat(el.textContent.replace(/[^0-9.]/g, ''));
        if (!isNaN(price)) {
            el.textContent = el.textContent.replace(
                price.toString(),
                price.toLocaleString('es', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                })
            );
        }
    });
});
