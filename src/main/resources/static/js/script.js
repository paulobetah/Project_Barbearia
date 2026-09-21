(function() {
    'use setup';

    function alternarTelas(tela) {
        const sectionLogin = document.getElementById('section-login');
        const sectionCadastro = document.getElementById('section-cadastro');
        const sectionRecuperar = document.getElementById('section-recuperar');

        if (sectionLogin) sectionLogin.classList.remove('active');
        if (sectionCadastro) sectionCadastro.classList.remove('active');
        if (sectionRecuperar) sectionRecuperar.classList.remove('active');

        const alvo = document.getElementById('section-' + tela);
        if (alvo) alvo.classList.add('active');
    }

    // Expose globally
    window.alternarTelas = alternarTelas;

    document.addEventListener("DOMContentLoaded", function() {
        // Handle login screen toggle
        const loginBody = document.getElementById('pagina-login');
        if (loginBody && loginBody.getAttribute('data-abrir-cadastro') === 'true') {
            alternarTelas('cadastro');
        }

        // Phone mask: (99) 99999-9999
        const phoneInputs = document.querySelectorAll('input[type="tel"], input[name="telefone"]');
        phoneInputs.forEach(input => {
            input.addEventListener('input', function(e) {
                let x = e.target.value.replace(/\D/g, '').match(/(\d{0,2})(\d{0,5})(\d{0,4})/);
                e.target.value = !x[2] ? x[1] : '(' + x[1] + ') ' + x[2] + (x[3] ? '-' + x[3] : '');
            });
        });

        // Auto-dismiss alerts after 5 seconds
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(alert => {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        });
    });
})();
