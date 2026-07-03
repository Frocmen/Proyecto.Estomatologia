// PROTECCIÓN DE RUTA
const usuarioSesion = JSON.parse(sessionStorage.getItem('sesion_usuario'));
if (!usuarioSesion) {
    window.location.href = 'login.html';
}

// INICIALIZAR 
document.addEventListener('DOMContentLoaded', function () {

    // Fecha mínima = hoy
    const fechaInput = document.getElementById('fecha');
    if (fechaInput) {
        fechaInput.min = new Date().toISOString().split('T')[0];
    }

    // Cargar profesionales en el select
    cargarProfesionales();

    cargarEspecialidades();

    // Mostrar sección inicial
    mostrarSeccion('agendar');
});

// CAMBIAR SECCIONES 
function mostrarSeccion(seccion) {
    document.querySelectorAll('.seccion').forEach(function(s) {
        s.style.display = 'none';
    });

    const sec = document.getElementById('seccion-' + seccion);
    if (sec) sec.style.display = 'block';

    document.querySelectorAll('.menu-item').forEach(function(item) {
        item.classList.remove('active');
    });

    if (seccion === 'mis-citas') cargarCitas();
    if (seccion === 'historial') cargarHistorial();
}

// CARGAR PROFESIONALES
function cargarProfesionales() {
    fetch('ProfesionalController?action=listar')
    .then(function(res) { return res.json(); })
    .then(function(data) {
        const select = document.getElementById('profesionalId');
        if (!select) return;
        select.innerHTML = '<option value="">Seleccione un profesional</option>';
        const lista = data.data || [];
        lista.forEach(function(p) {
            select.innerHTML += '<option value="' + p.id + '">'
                    + p.nombre + ' ' + p.apellido + '</option>';
        });
    })
    .catch(function() {
        const select = document.getElementById('profesionalId');
        if (select) {
            select.innerHTML = '<option value="1">Dr. por defecto</option>';
        }
    });
}

// CARGAR ESPECIALIDADES 

function cargarEspecialidades() {
    fetch('EspecialidadController?action=listar')
    .then(function(res) { return res.json(); })
    .then(function(data) {
        const select = document.getElementById('servicio');
        if (!select) return;
        select.innerHTML = '<option value="">Seleccione un servicio</option>';
        const lista = data.data || [];
        lista.forEach(function(e) {
            select.innerHTML += '<option value="' + e.id + '">' + e.nombre + '</option>';
        });
    })
    .catch(function() {
        console.error('No se pudo cargar la lista de especialidades');
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudieron cargar los servicios disponibles. Recarga la página.'
        });
    });
}

//AGENDAR CITA
document.getElementById('formCita').addEventListener('submit', function(e) {
    e.preventDefault();

    const servicio       = document.getElementById('servicio').value; // ahora es un ID numérico
    const profesionalId  = document.getElementById('profesionalId').value;
    const fecha           = document.getElementById('fecha').value;
    const hora             = document.getElementById('hora').value;
    const comentarios    = document.getElementById('comentarios').value;

    if (!servicio || !fecha || !hora) {
        Swal.fire({
            icon: 'warning',
            title: 'Campos incompletos',
            text: 'Selecciona servicio, fecha y hora'
        });
        return;
    }

    if (!profesionalId) {
        Swal.fire({
            icon: 'warning',
            title: 'Falta el profesional',
            text: 'Selecciona un profesional'
        });
        return;
    }

    const fechaHora  = fecha + 'T' + hora + ':00';
    const pacienteId = usuarioSesion.id
            || (usuarioSesion.persona ? usuarioSesion.persona.id : 0);

    if (!pacienteId) {
        Swal.fire({
            icon: 'error',
            title: 'Error de sesión',
            text: 'No se pudo identificar al paciente. Vuelve a iniciar sesión.'
        });
        return;
    }

    const datos = new URLSearchParams();
    datos.append('pacienteId',    pacienteId);
    datos.append('profesionalId', profesionalId);
    datos.append('especialidadId', servicio); // ahora sí es un ID numérico válido
    datos.append('fechaHora',     fechaHora);
    datos.append('motivo',        comentarios || 'Consulta');
    datos.append('estado',        'CONFIRMADA');

    fetch('CitaController?action=registrar', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: datos.toString()
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        console.log('Cita respuesta:', data);
        if (data.success) {
            Swal.fire({
                icon: 'success',
                title: '¡Cita registrada!',
                text: 'Tu cita fue agendada correctamente.',
                timer: 2000,
                showConfirmButton: false
            });
            document.getElementById('formCita').reset();
            setTimeout(function() {
                mostrarSeccion('mis-citas');
            }, 2000);
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: data.message || 'No se pudo registrar la cita'
            });
        }
    })
    .catch(function(err) {
        console.error('Error:', err);
        Swal.fire({
            icon: 'error',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor'
        });
    });
});

// ── CARGAR MIS CITAS ──────────────────────────────────────────
function cargarCitas() {
    const contenedor = document.getElementById('lista-citas');
    if (!contenedor) return;

    contenedor.innerHTML = '<p>Cargando citas...</p>';

    const pacienteId = usuarioSesion.id
            || (usuarioSesion.persona ? usuarioSesion.persona.id : 0);

    if (!pacienteId) {
        contenedor.innerHTML = '<p>Error: no se pudo identificar al paciente.</p>';
        return;
    }

    fetch('CitaController?action=listarPorPaciente&pacienteId=' + pacienteId)
    .then(function(res) { return res.json(); })
    .then(function(data) {
        const citas = data.data || [];

        if (citas.length === 0) {
            contenedor.innerHTML =
                    '<p class="text-muted">Aún no tienes citas programadas.</p>';
            return;
        }

        let html = '';
        citas.forEach(function(cita) {
            const fecha = cita.fechaHora
                    ? cita.fechaHora.substring(0, 10) : '-';
            const hora  = cita.fechaHora
                    ? cita.fechaHora.substring(11, 16) : '-';
            const color = cita.estado === 'CONFIRMADA' ? 'green'  :
                          cita.estado === 'CANCELADA'  ? 'red'    :
                          cita.estado === 'ATENDIDA'   ? 'blue'   : 'orange';

            html += '<div class="cita-card">'
                  + '<h4>' + (cita.motivo || 'Consulta') + '</h4>'
                  + '<p><strong>Fecha:</strong> ' + fecha
                  + ' | <strong>Hora:</strong> ' + hora + '</p>'
                  + '<p><strong>Médico:</strong> '
                  + (cita.profesional ? cita.profesional.nombre + ' '
                  + cita.profesional.apellido : '-') + '</p>'
                  + '<p><strong>Estado:</strong> '
                  + '<span style="color:' + color + ';">'
                  + cita.estado + '</span></p>';

            if (cita.estado === 'CONFIRMADA') {
                html += '<button onclick="cancelarCita(' + cita.id + ')" '
                      + 'style="background:#e74c3c;color:white;border:none;'
                      + 'padding:6px 14px;border-radius:6px;cursor:pointer;'
                      + 'margin-top:8px;">Cancelar Cita</button>';
            }

            html += '</div>';
        });

        contenedor.innerHTML = html;
    })
    .catch(function(err) {
        console.error('Error:', err);
        contenedor.innerHTML = '<p>Error al cargar las citas.</p>';
    });
}

// ── CANCELAR CITA — RN-05 ─────────────────────────────────────
function cancelarCita(idCita) {
    Swal.fire({
        title: '¿Cancelar cita?',
        text: 'Solo puedes cancelar con más de 8 horas de anticipación.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, cancelar',
        cancelButtonText: 'No'
    }).then(function(result) {
        if (!result.isConfirmed) return;

        fetch('CitaController?action=cancelar&idCita=' + idCita, {
            method: 'POST'
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                Swal.fire('Cancelada', 'La cita fue cancelada.', 'success');
                cargarCitas();
            } else {
                Swal.fire('No se puede cancelar',
                        data.message || 'Faltan menos de 8 horas.', 'error');
            }
        })
        .catch(function() {
            Swal.fire('Error', 'No se pudo conectar.', 'error');
        });
    });
}

// ── CARGAR HISTORIAL ──────────────────────────────────────────
function cargarHistorial() {
    const contenedor = document.getElementById('lista-historial');
    if (!contenedor) return;

    contenedor.innerHTML = '<p>Cargando historial...</p>';

    const pacienteId = usuarioSesion.id
            || (usuarioSesion.persona ? usuarioSesion.persona.id : 0);

    fetch('CitaController?action=listarPorPaciente&pacienteId=' + pacienteId)
    .then(function(res) { return res.json(); })
    .then(function(data) {
        const atendidas = (data.data || []).filter(function(c) {
            return c.estado === 'ATENDIDA';
        });

        if (atendidas.length === 0) {
            contenedor.innerHTML =
                    '<p class="text-muted">No tienes atenciones registradas aún.</p>';
            return;
        }

        let html = '';
        atendidas.forEach(function(cita) {
            const fecha = cita.fechaHora
                    ? cita.fechaHora.substring(0, 10) : '-';
            html += '<div class="cita-card">'
                  + '<h4>' + (cita.motivo || 'Atención') + '</h4>'
                  + '<p><strong>Fecha:</strong> ' + fecha + '</p>'
                  + '<p><strong>Médico:</strong> '
                  + (cita.profesional ? cita.profesional.nombre
                  + ' ' + cita.profesional.apellido : '-') + '</p>'
                  + '<span style="color:blue;font-weight:bold;">Atendido ✓</span>'
                  + '</div>';
        });

        contenedor.innerHTML = html;
    })
    .catch(function() {
        contenedor.innerHTML = '<p>Error al cargar historial.</p>';
    });
}

// ── CERRAR SESIÓN ─────────────────────────────────────────────
document.getElementById('logoutBtn').addEventListener('click', function() {
    if (confirm('¿Estás seguro de cerrar sesión?')) {
        fetch('AuthController?action=salir', {method: 'POST'})
        .finally(function() {
            sessionStorage.removeItem('sesion_usuario');
            window.location.href = 'login.html';
        });
    }
});