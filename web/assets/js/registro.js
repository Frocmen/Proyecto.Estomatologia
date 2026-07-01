document.addEventListener('DOMContentLoaded', function () {

    const form = document.getElementById('registro-form');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const nombre   = document.getElementById('nombre').value.trim();
        const apellido = document.getElementById('apellido').value.trim();
        const dni      = document.getElementById('dni').value.trim();
        const telefono = document.getElementById('telefono').value.trim();
        const email    = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!nombre || !email || !password) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Nombre, email y contraseña son obligatorios'
            });
            return;
        }

        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Registrando...';

        const datos = new URLSearchParams();
        datos.append('nombre',   nombre);
        datos.append('apellido', apellido);
        datos.append('dni',      dni);
        datos.append('telefono', telefono);
        datos.append('email',    email);
        datos.append('password', password);

        fetch('AuthController?action=register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: datos.toString()
        })
        .then(function(res) {
            return res.json();
        })
        .then(function(data) {
            console.log('Respuesta servidor:', data);

            if (data.success) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Registro exitoso!',
                    text: 'Tu cuenta fue creada. Ahora inicia sesión.',
                    confirmButtonText: 'Ir al Login'
                }).then(function() {
                    window.location.href = 'login.html';
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: data.message || 'No se pudo registrar'
                });
                btn.disabled = false;
                btn.textContent = 'Registrarse';
            }
        })
        .catch(function(err) {
            console.error('Error de conexión:', err);
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'Verifica que Tomcat esté corriendo'
            });
            btn.disabled = false;
            btn.textContent = 'Registrarse';
        });
    });
});