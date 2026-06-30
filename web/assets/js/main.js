/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// ====================== MAIN.JS - DENTAL HEALTH ======================

// Variables globales
let usuarioActual = null;

// Cargar componentes reutilizables (header y footer)
async function cargarComponentes() {
    try {
        const headerRes = await fetch('header.html');
        document.getElementById('header-placeholder').innerHTML = await headerRes.text();

        const footerRes = await fetch('footer.html');
        document.getElementById('footer-placeholder').innerHTML = await footerRes.text();
    } catch (e) {
        console.error("Error cargando componentes", e);
    }
}

// Verificar si hay sesión activa
function verificarSesion() {
    const usuarioStr = localStorage.getItem('usuario');
    if (usuarioStr) {
        usuarioActual = JSON.parse(usuarioStr);
        return true;
    }
    return false;
}

// Login
document.addEventListener('DOMContentLoaded', function () {

    const form = document.getElementById('login');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const email    = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!email || !password) {
            mostrarAlerta("Completa todos los campos", "warning");
            return;
        }

        const datos = new URLSearchParams({
            usuario: email,
            password: password
        });

        fetch('AuthController?action=validar', {
            method: 'POST',
            body: datos
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // Guardamos el objeto usuario completo devuelto por el servidor
                sessionStorage.setItem("sesion_usuario", JSON.stringify(data.userData));

                const nombre = data.userData.persona?.nombre || data.userData.usuario || 'Usuario';
                mostrarAlerta("Bienvenido " + nombre, "success");

                setTimeout(() => {
                    const rol = data.userData.rol;
                    if (rol === "ADMIN" || rol === "RECEPCIONISTA" || rol === "JEFE_ADMIN") {
                        window.location.href = "VistaDoctor.html";
                    } else {
                        window.location.href = "VistaUsuario.html";
                    }
                }, 1500);

            } else {
                mostrarAlerta(data.message || "Credenciales incorrectas", "error");
            }
        })
        .catch(() => {
            mostrarAlerta("Error de conexión con el servidor", "error");
        });
    });
});

function mostrarAlerta(mensaje, tipo) {
    Swal.fire({
        icon: tipo,
        title: mensaje,
        timer: 2000,
        showConfirmButton: false
    });
}


// Registro de paciente
document.getElementById('registro-form').addEventListener('submit', function (e) {
    e.preventDefault();

    const nombre   = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const dni      = document.getElementById('dni').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!nombre || !apellido || !dni || !email || !password) {
        Swal.fire({
            icon: 'warning',
            title: 'Campos incompletos',
            text: 'Por favor completa todos los campos obligatorios'
        });
        return;
    }

    const datos = new URLSearchParams({
        nombre:   nombre,
        apellido: apellido,
        dni:      dni,
        telefono: telefono,
        email:    email,
        password: password
    });

    fetch('AuthController?action=register', {
        method: 'POST',
        body: datos
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            Swal.fire({
                icon: 'success',
                title: '¡Registro exitoso!',
                text: 'Tu cuenta ha sido creada. Ahora puedes iniciar sesión.',
                timer: 2500,
                showConfirmButton: false
            });
            this.reset();
            setTimeout(() => {
                window.location.href = "login.html";
            }, 2500);
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: data.message || 'No se pudo registrar la cuenta'
            });
        }
    })
    .catch(() => {
        Swal.fire({
            icon: 'error',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor'
        });
    });
});


// Agendar Cita
document.addEventListener('DOMContentLoaded', function () {

    // Fecha mínima = hoy
    const fechaInput = document.getElementById('fecha');
    if (fechaInput) {
        fechaInput.min = new Date().toISOString().split('T')[0];
    }

    // Enviar formulario
    document.getElementById('formCita').addEventListener('submit', function (e) {
        e.preventDefault();

        // Verificar sesión
        const usuario = JSON.parse(sessionStorage.getItem('sesion_usuario'));
        if (!usuario) {
            Swal.fire({
                title: 'Debes iniciar sesión',
                text: '¿Quieres ir a la página de login?',
                icon: 'info',
                showCancelButton: true,
                confirmButtonText: 'Ir al Login',
                cancelButtonText: 'Cancelar'
            }).then(result => {
                if (result.isConfirmed) window.location.href = "login.html";
            });
            return;
        }

        const servicio  = document.getElementById('servicio').value;
        const fecha     = document.getElementById('fecha').value;
        const hora      = document.getElementById('hora').value;
        const comentarios = document.getElementById('comentarios')?.value || '';

        if (!servicio || !fecha || !hora) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Completa servicio, fecha y hora'
            });
            return;
        }

        const fechaHora = fecha + 'T' + hora + ':00';
        const pacienteId = usuario.id || usuario.persona?.id || '';

        const datos = new URLSearchParams({
            pacienteId:    pacienteId,
            profesionalId: 1,
            especialidadId: servicio,
            fechaHora:     fechaHora,
            motivo:        comentarios || 'Consulta general',
            estado:        'CONFIRMADA'
        });

        fetch('CitaController?action=registrar', {
            method: 'POST',
            body: datos
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Cita solicitada!',
                    text: 'Tu cita ha sido registrada correctamente.',
                    timer: 2000,
                    showConfirmButton: false
                });
                this.reset();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: data.message || 'No se pudo registrar la cita'
                });
            }
        })
        .catch(() => {
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No se pudo conectar con el servidor'
            });
        });
    });
});


// Cargar mis citas (para paciente)
async function cargarMisCitas() {
    if (!usuarioActual) return;
    const container = document.getElementById('misCitas');
    if (!container) return;

    try {
        const res = await fetch(`CitaController?action=listarPorPaciente&idPaciente=${usuarioActual.id_usuario}`);
        const data = await res.json();
        // Renderizar citas...
        container.innerHTML = '<p>Próximamente verás tus citas aquí.</p>';
    } catch (e) {
        console.error(e);
    }
}

// Inicialización general
document.addEventListener('DOMContentLoaded', () => {
    cargarComponentes();
    
    // Verificar sesión en páginas protegidas
    if (window.location.pathname.includes('Vista')) {
        if (!verificarSesion()) {
            window.location.href = 'login.html';
        }
    }

    // Ejemplo: si estás en login.html
    const formLogin = document.getElementById('formLogin');
    if (formLogin) {
        formLogin.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            login(email, password);
        });
    }

    // Ejemplo: si estás en registrarse.html
    const formRegistro = document.getElementById('formRegistro');
    if (formRegistro) {
        formRegistro.addEventListener('submit', (e) => {
            e.preventDefault();
            const formData = new FormData(formRegistro);
            registrarPaciente(formData);
        });
    }

    // Cargar datos en VistaUsuario
    if (document.getElementById('nombreUsuario')) {
        const user = JSON.parse(localStorage.getItem('usuario'));
        if (user) document.getElementById('nombreUsuario').textContent = user.persona.nombre;
        cargarMisCitas();
    }
});