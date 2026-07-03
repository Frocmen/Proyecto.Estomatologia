/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// PROTECCIÓN DE RUTA 
const usuarioSesion = JSON.parse(sessionStorage.getItem('sesion_usuario'));
if (!usuarioSesion) {
    window.location.href = 'login.html';
}

//ESTADO DEL FLUJO
let pacienteActual = null;

// INICIALIZAR 
document.addEventListener('DOMContentLoaded', function () {
    const fechaInput = document.getElementById('fecha');
    if (fechaInput) {
        fechaInput.min = new Date().toISOString().split('T')[0];
    }

    cargarProfesionales();
    cargarEspecialidades();
    mostrarSeccion('nueva-cita');
});

//CAMBIAR SECCIONES
function mostrarSeccion(seccion) {
    document.querySelectorAll('.seccion').forEach(function (s) {
        s.style.display = 'none';
    });
    const sec = document.getElementById('seccion-' + seccion);
    if (sec) sec.style.display = 'block';

    document.querySelectorAll('.menu-item').forEach(function (item) {
        item.classList.remove('active');
    });

    if (seccion === 'todas-citas') cargarTodasCitas();
}

// BUSCAR PACIENTE POR DNI
function buscarPaciente() {
    const dni = document.getElementById('dniBusqueda').value.trim();

    if (!dni) {
        Swal.fire({ icon: 'warning', title: 'Falta el DNI', text: 'Ingresa un DNI para buscar' });
        return;
    }

    fetch('PacienteController?action=buscar&dni=' + encodeURIComponent(dni))
        .then(function (res) { return res.json(); })
        .then(function (data) {
            document.getElementById('paso-paciente-encontrado').style.display = 'none';
            document.getElementById('paso-paciente-nuevo').style.display = 'none';
            document.getElementById('paso-datos-cita').style.display = 'none';

            if (data.success && data.data) {
                // ── Paciente existente ──
                const p = data.data;
                pacienteActual = { id: p.id, nombre: p.nombre, apellido: p.apellido };

                document.getElementById('infoPacienteEncontrado').innerHTML =
                    '<strong>' + p.nombre + ' ' + (p.apellido || '') + '</strong><br>'
                    + 'DNI: ' + (p.dni || '-') + ' | Tel: ' + (p.telefono || '-')
                    + ' | Email: ' + (p.email || '-');

                document.getElementById('paso-paciente-encontrado').style.display = 'block';
            } else {
                // ── No existe: mostrar formulario de alta rápida ──
                document.getElementById('np-dni').value = dni;
                document.getElementById('paso-paciente-nuevo').style.display = 'block';
            }
        })
        .catch(function () {
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No se pudo conectar con el servidor' });
        });
}

// CONTINUAR CON PACIENTE EXISTENTE 
function continuarConPaciente() {
    if (!pacienteActual) return;
    mostrarPasoCita();
}

//  REGISTRAR PACIENTE NUEVO
document.getElementById('formNuevoPaciente').addEventListener('submit', function (e) {
    e.preventDefault();

    const nombre = document.getElementById('np-nombre').value.trim();
    const apellido = document.getElementById('np-apellido').value.trim();
    const dni = document.getElementById('np-dni').value.trim();
    const telefono = document.getElementById('np-telefono').value.trim();
    const email = document.getElementById('np-email').value.trim();

    if (!nombre || !dni) {
        Swal.fire({ icon: 'warning', title: 'Campos incompletos', text: 'Nombre y DNI son obligatorios' });
        return;
    }

    const datos = new URLSearchParams();
    datos.append('nombre', nombre);
    datos.append('apellido', apellido);
    datos.append('dni', dni);
    datos.append('telefono', telefono);
    datos.append('email', email);

    fetch('PacienteController?action=guardar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: datos.toString()
    })
        .then(function (res) { return res.json(); })
        .then(function (data) {
            if (data.success && data.id) {
                pacienteActual = { id: data.id, nombre: nombre, apellido: apellido };

                Swal.fire({
                    icon: 'success',
                    title: 'Paciente registrado',
                    timer: 1500,
                    showConfirmButton: false
                });

                document.getElementById('paso-paciente-nuevo').style.display = 'none';
                mostrarPasoCita();
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'No se pudo registrar el paciente' });
            }
        })
        .catch(function () {
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No se pudo conectar con el servidor' });
        });
});

// (DATOS DE LA CITA)
function mostrarPasoCita() {
    document.getElementById('pacienteSeleccionadoInfo').textContent =
        'Paciente: ' + pacienteActual.nombre + ' ' + (pacienteActual.apellido || '') + ' (ID: ' + pacienteActual.id + ')';
    document.getElementById('paso-datos-cita').style.display = 'block';
}

// REINICIAR TODO EL FLUJO
function reiniciarBusqueda() {
    pacienteActual = null;
    document.getElementById('dniBusqueda').value = '';
    document.getElementById('formNuevoPaciente').reset();
    document.getElementById('formCitaRecepcion').reset();
    document.getElementById('resultadoBusqueda').innerHTML = '';
    document.getElementById('paso-paciente-encontrado').style.display = 'none';
    document.getElementById('paso-paciente-nuevo').style.display = 'none';
    document.getElementById('paso-datos-cita').style.display = 'none';
}

// CARGAR PROFESIONALES
function cargarProfesionales() {
    fetch('ProfesionalController?action=listar')
        .then(function (res) { return res.json(); })
        .then(function (data) {
            const select = document.getElementById('profesionalId');
            if (!select) return;
            select.innerHTML = '<option value="">Seleccione un profesional...</option>';
            (data.data || []).forEach(function (p) {
                select.innerHTML += '<option value="' + p.id + '">' + p.nombre + ' ' + p.apellido + '</option>';
            });
        })
        .catch(function () {
            console.error('No se pudo cargar la lista de profesionales');
        });
}

// CARGAR ESPECIALIDADES
function cargarEspecialidades() {
    fetch('EspecialidadController?action=listar')
        .then(function (res) { return res.json(); })
        .then(function (data) {
            const select = document.getElementById('especialidadId');
            if (!select) return;
            select.innerHTML = '<option value="">Seleccione una especialidad...</option>';
            (data.data || []).forEach(function (e) {
                select.innerHTML += '<option value="' + e.id + '">' + e.nombre + '</option>';
            });
        })
        .catch(function () {
            console.error('No se pudo cargar la lista de especialidades');
        });
}

//REGISTRAR LA CITA 
document.getElementById('formCitaRecepcion').addEventListener('submit', function (e) {
    e.preventDefault();

    if (!pacienteActual) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No hay paciente seleccionado' });
        return;
    }

    const profesionalId = document.getElementById('profesionalId').value;
    const especialidadId = document.getElementById('especialidadId').value;
    const fecha = document.getElementById('fecha').value;
    const hora = document.getElementById('hora').value;
    const motivo = document.getElementById('motivo').value;

    if (!profesionalId || !especialidadId || !fecha || !hora) {
        Swal.fire({ icon: 'warning', title: 'Campos incompletos', text: 'Completa profesional, especialidad, fecha y hora' });
        return;
    }

    const fechaHora = fecha + 'T' + hora + ':00';

    const datos = new URLSearchParams();
    datos.append('pacienteId', pacienteActual.id);
    datos.append('profesionalId', profesionalId);
    datos.append('especialidadId', especialidadId);
    datos.append('fechaHora', fechaHora);
    datos.append('motivo', motivo || 'Consulta');

    fetch('CitaController?action=registrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: datos.toString()
    })
        .then(function (res) { return res.json(); })
        .then(function (data) {
            if (data.success) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Cita registrada!',
                    text: 'La cita fue agendada correctamente para ' + pacienteActual.nombre,
                    timer: 2200,
                    showConfirmButton: false
                });
                reiniciarBusqueda();
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'No se pudo registrar la cita' });
            }
        })
        .catch(function () {
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No se pudo conectar con el servidor' });
        });
});

// LISTAR TODAS LAS CITAS (vista general de recepción)
function cargarTodasCitas() {
    const contenedor = document.getElementById('lista-todas-citas');
    if (!contenedor) return;

    contenedor.innerHTML = '<p>Cargando...</p>';

    fetch('CitaController?action=listarTodas')
        .then(function (res) { return res.json(); })
        .then(function (data) {
            const citas = data.data || [];

            if (citas.length === 0) {
                contenedor.innerHTML = '<p class="text-muted">No hay citas registradas.</p>';
                return;
            }

            let html = '';
            citas.forEach(function (cita) {
                const fecha = cita.fechaHora ? cita.fechaHora.substring(0, 10) : '-';
                const hora = cita.fechaHora ? cita.fechaHora.substring(11, 16) : '-';
                const color = cita.estado === 'CONFIRMADA' ? 'green' :
                    cita.estado === 'CANCELADA' ? 'red' :
                    cita.estado === 'ATENDIDA' ? 'blue' : 'orange';

                html += '<div class="cita-card">'
                    + '<p><strong>Paciente:</strong> ' + (cita.paciente ? cita.paciente.nombre + ' ' + cita.paciente.apellido : '-') + '</p>'
                    + '<p><strong>Profesional:</strong> ' + (cita.profesional ? cita.profesional.nombre + ' ' + cita.profesional.apellido : '-') + '</p>'
                    + '<p><strong>Fecha:</strong> ' + fecha + ' | <strong>Hora:</strong> ' + hora + '</p>'
                    + '<p><strong>Estado:</strong> <span style="color:' + color + ';">' + cita.estado + '</span></p>'
                    + '</div>';
            });

            contenedor.innerHTML = html;
        })
        .catch(function () {
            contenedor.innerHTML = '<p>Error al cargar las citas.</p>';
        });
}

//CERRAR SESIÓN 
document.getElementById('logoutBtn').addEventListener('click', function () {
    if (confirm('¿Estás seguro de cerrar sesión?')) {
        fetch('AuthController?action=salir', { method: 'POST' })
            .finally(function () {
                sessionStorage.removeItem('sesion_usuario');
                window.location.href = 'login.html';
            });
    }
});
