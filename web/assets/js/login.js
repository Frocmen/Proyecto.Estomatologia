document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('login');
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const email    = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();
        if (!email || !password) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Ingresa tu email y contraseña'
            });
            return;
        }
        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Ingresando...';
        const datos = new URLSearchParams();
        datos.append('usuario',  email);
        datos.append('password', password);
        fetch('AuthController?action=validar', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: datos.toString()
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            console.log('Login respuesta:', data);
            if (data.success) {
                sessionStorage.setItem('sesion_usuario',
                        JSON.stringify(data.userData));
                const nombre = data.userData.persona
                        ? data.userData.persona.nombre
                        : data.userData.usuario;
                Swal.fire({
                    icon: 'success',
                    title: '¡Bienvenido ' + nombre + '!',
                    timer: 1500,
                    showConfirmButton: false
                });
                setTimeout(function() {
                    const rol = data.userData.rol;
                    if (rol === 'RECEPCIONISTA') {
                        window.location.href = 'VistaRecepcion.html';
                    } else if (rol === 'ADMIN' ||
                               rol === 'MEDICO' ||
                               rol === 'JEFE_ADMIN') {
                        window.location.href = 'VistaDoctor.html';
                    } else {
                        window.location.href = 'VistaUsuario.html';
                    }
                }, 1600);
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: data.message || 'Email o contraseña incorrectos'
                });
                btn.disabled = false;
                btn.textContent = 'Entrar';
            }
        })
        .catch(function(err) {
            console.error('Error:', err);
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No se pudo conectar con el servidor'
            });
            btn.disabled = false;
            btn.textContent = 'Entrar';
        });
    });
});