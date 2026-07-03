//PROTECCIÓN DE RUTA 
const doctorSesion = JSON.parse(sessionStorage.getItem('sesion_usuario'));
if (!doctorSesion) {
    window.location.href = 'login.html';
}

const profesionalId = doctorSesion.id
        || (doctorSesion.persona ? doctorSesion.persona.id : null);

//  CAMBIAR SECCIONES
function mostrarSeccion(seccion) {
    document.querySelectorAll('.seccion').forEach(function (s) {
        s.style.display = 'none';
    });
    const sec = document.getElementById('seccion-' + seccion);
    if (sec) sec.style.display = 'block';

    document.querySelectorAll('.menu-item').forEach(function (item) {
        item.classList.remove('active');
    });

    if (seccion === 'citas-pendientes') renderCitasPendientes();
    if (seccion === 'todas-citas') renderTodasCitas();
    if (seccion === 'pacientes') renderHistorialPacientes();
}

function obtenerCitasDelProfesional() {
    if (!profesionalId) {
        return Promise.resolve([]);
    }
    return fetch('CitaController?action=listarPorProfesional&profesionalId=' + profesionalId)
        .then(function (res) { return res.json(); })
        .then(function (data) { return data.data || []; })
        .catch(function () {
            console.error('No se pudieron cargar las citas del profesional');
            return [];
        });
}

function renderCitasPendientes() {
    const contenedor = document.getElementById('citas-pendientes');
    if (!contenedor) return;
    contenedor.innerHTML = '<p>Cargando...</p>';

    obtenerCitasDelProfesional().then(function (citas) {
        const pendientes = citas.filter(function (c) {
            return c.estado === 'CONFIRMADA';
        });

        if (pendientes.length === 0) {
            contenedor.innerHTML = '<p class="text-success">¡No tienes citas pendientes!</p>';
            return;
        }

        let html = '';
        pendientes.forEach(function (cita) {
            const fecha = cita.fechaHora ? cita.fechaHora.substring(0, 10) : '-';
            const hora  = cita.fechaHora ? cita.fechaHora.substring(11, 16) : '-';
            const paciente = cita.paciente
                    ? cita.paciente.nombre + ' ' + (cita.paciente.apellido || '')
                    : '-';

            html += '<div class="cita-card">'
                  + '<div style="display:flex;justify-content:space-between;align-items:center;">'
                  + '<div><strong>' + paciente + '</strong><br>'
                  + '<span>' + (cita.motivo || 'Consulta') + '</span></div>'
                  + '<div style="text-align:right;">'
                  + '<div><strong>' + fecha + '</strong> - ' + hora + '</div>'
                  + '<span class="badge-pendiente">Pendiente</span></div>'
                  + '</div>'
                  + '<button onclick="atenderCita(' + cita.id + ')" '
                  + 'style="margin-top:10px;background:#27ae60;color:white;'
                  + 'border:none;padding:8px 15px;border-radius:6px;cursor:pointer;">'
                  + 'Marcar como Atendida</button>'
                  + '</div>';
        });

        contenedor.innerHTML = html;
    });
}


function renderTodasCitas() {
    const contenedor = document.getElementById('todas-las-citas');
    if (!contenedor) return;
    contenedor.innerHTML = '<p>Cargando...</p>';

    obtenerCitasDelProfesional().then(function (citas) {
        if (citas.length === 0) {
            contenedor.innerHTML = '<p class="text-muted">No hay citas registradas.</p>';
            return;
        }

        let html = '';
        citas.forEach(function (cita) {
            const fecha = cita.fechaHora ? cita.fechaHora.substring(0, 10) : '-';
            const hora  = cita.fechaHora ? cita.fechaHora.substring(11, 16) : '-';
            const paciente = cita.paciente
                    ? cita.paciente.nombre + ' ' + (cita.paciente.apellido || '')
                    : '-';
            const badgeClass = cita.estado === 'CONFIRMADA' ? 'badge-confirmada' : 'badge-pendiente';

            html += '<div class="cita-card">'
                  + '<div style="display:flex;justify-content:space-between;">'
                  + '<div><strong>' + paciente + '</strong><br>' + (cita.motivo || 'Consulta') + '</div>'
                  + '<div style="text-align:right;">'
                  + '<div><strong>' + fecha + '</strong> — ' + hora + '</div>'
                  + '<span class="' + badgeClass + '">' + cita.estado + '</span></div>'
                  + '</div></div>';
        });

        contenedor.innerHTML = html;
    });
}

// HISTORIAL DE PACIENTES ATENDIDOS 
function renderHistorialPacientes() {
    const contenedor = document.getElementById('historial-pacientes');
    if (!contenedor) return;
    contenedor.innerHTML = '<p>Cargando...</p>';

    obtenerCitasDelProfesional().then(function (citas) {
        const atendidas = citas.filter(function (c) {
            return c.estado === 'ATENDIDA';
        });

        if (atendidas.length === 0) {
            contenedor.innerHTML = '<p>No hay historial disponible</p>';
            return;
        }

        let html = '';
        atendidas.forEach(function (cita) {
            const fecha = cita.fechaHora ? cita.fechaHora.substring(0, 10) : '-';
            const hora  = cita.fechaHora ? cita.fechaHora.substring(11, 16) : '-';
            const paciente = cita.paciente
                    ? cita.paciente.nombre + ' ' + (cita.paciente.apellido || '')
                    : '-';

            html += '<div class="cita-card">'
                  + '<strong>Paciente:</strong> ' + paciente + '<br>'
                  + '<strong>Motivo:</strong> ' + (cita.motivo || 'Consulta') + '<br>'
                  + '<strong>Fecha:</strong> ' + fecha + ' - ' + hora + '<br>'
                  + '<span style="color:green;">Atendido</span>'
                  + '</div>';
        });

        contenedor.innerHTML = html;
    });
}

// MARCAR CITA COMO ATENDIDA 
function atenderCita(idCita) {
    if (!confirm('¿Marcar esta cita como atendida?')) return;

    fetch('CitaController?action=atender&idCita=' + idCita, {
        method: 'POST'
    })
    .then(function (res) { return res.json(); })
    .then(function (data) {
        if (data.success) {
            alert('Cita marcada como atendida.');
            renderCitasPendientes();
            renderTodasCitas();
        } else {
            alert(data.message || 'No se pudo actualizar la cita.');
        }
    })
    .catch(function () {
        alert('No se pudo conectar con el servidor.');
    });
}

// CERRAR SESIÓN 
document.getElementById('logoutBtn').addEventListener('click', function () {
    if (confirm('¿Cerrar sesión como doctor?')) {
        fetch('AuthController?action=salir', { method: 'POST' })
            .finally(function () {
                sessionStorage.removeItem('sesion_usuario');
                window.location.href = 'login.html';
            });
    }
});

// INICIALIZAR
document.addEventListener('DOMContentLoaded', function () {
    mostrarSeccion('citas-pendientes');
});