// Datos de ejemplo (en la vida real vendrían de una base de datos)
        let todasLasCitas = JSON.parse(localStorage.getItem('todasCitas')) || [
            {
                id: 1,
                paciente: "Jhonatan López",
                servicio: "Limpieza Dental Profunda",
                fecha: "2026-04-20",
                hora: "10:00",
                estado: "Pendiente"
            },
            {
                id: 2,
                paciente: "María García",
                servicio: "Blanqueamiento Dental",
                fecha: "2026-04-21",
                hora: "14:30",
                estado: "Pendiente"
            },
            {
                id: 3,
                paciente: "Carlos Ramírez",
                servicio: "Consulta General",
                fecha: "2026-04-18",
                hora: "09:00",
                estado: "Confirmada"
            }
        ];

        // Guardar en localStorage (para simular persistencia)
        function guardarCitas() {
            localStorage.setItem('todasCitas', JSON.stringify(todasLasCitas));
        }

        // Mostrar secciones
        function mostrarSeccion(seccion) {
            document.querySelectorAll('.seccion').forEach(s => s.style.display = 'none');
            document.getElementById('seccion-' + seccion).style.display = 'block';

            document.querySelectorAll('.menu-item').forEach(item => item.classList.remove('active'));
        }

        // Renderizar citas pendientes
        function renderCitasPendientes() {
            const contenedor = document.getElementById('citas-pendientes');
            const pendientes = todasLasCitas.filter(c => c.estado === "Pendiente");

            if (pendientes.length === 0) {
                contenedor.innerHTML = `<p class="text-success">¡No tienes citas pendientes!</p>`;
                return;
            }

            let html = '';
            pendientes.forEach(cita => {
                html += `
                    <div class="cita-card">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <strong>${cita.paciente}</strong><br>
                                <span>${cita.servicio}</span>
                            </div>
                            <div style="text-align: right;">
                                <div><strong>${cita.fecha}</strong> - ${cita.hora}</div>
                                <span class="badge-pendiente">Pendiente</span>
                            </div>
                        </div>
                        <button onclick="confirmarCita(${cita.id})" style="margin-top: 10px; background: #27ae60; color: white; border: none; padding: 8px 15px; border-radius: 6px;">
                            Confirmar Cita
                        </button>
                    </div>`;
            });

            contenedor.innerHTML = html;
        }

        // Renderizar todas las citas
        function renderTodasCitas() {
            const contenedor = document.getElementById('todas-las-citas');
            let html = '';

            todasLasCitas.forEach(cita => {
                const badgeClass = cita.estado === "Confirmada" ? "badge-confirmada" : "badge-pendiente";
                html += `
                    <div class="cita-card">
                        <div style="display: flex; justify-content: space-between;">
                            <div>
                                <strong>${cita.paciente}</strong><br>
                                ${cita.servicio}
                            </div>
                            <div style="text-align: right;">
                                <div><strong>${cita.fecha}</strong> — ${cita.hora}</div>
                                <span class="${badgeClass}">${cita.estado}</span>
                            </div>
                        </div>
                    </div>`;
            });

            contenedor.innerHTML = html;
        }

        function renderHistorialPacientes() {
            const contenedor = document.getElementById('historial-pacientes');
            const citas = JSON.parse(localStorage.getItem('todasCitas')) || [];

            if (citas.length === 0) {
                contenedor.innerHTML = "<p>No hay historial disponible</p>";
                return;
            }

            let html = '';

            citas.forEach(cita => {
                if (cita.estado === "Confirmada") {
                    html += `
                <div class="cita-card">
                    <strong>Paciente:</strong> ${cita.paciente}<br>
                    <strong>Servicio:</strong> ${cita.servicio}<br>
                    <strong>Fecha:</strong> ${cita.fecha} - ${cita.hora}<br>
                    <span style="color: green;">Atendido</span>
                </div>
            `;
                }
            });

            contenedor.innerHTML = html;
        }
        // Confirmar cita
        function confirmarCita(id) {
            if (confirm("¿Confirmar esta cita?")) {
                todasLasCitas = todasLasCitas.map(cita => {
                    if (cita.id === id) {
                        cita.estado = "Confirmada";
                    }
                    return cita;
                });
                guardarCitas();
                renderCitasPendientes();
                renderTodasCitas();
                alert("Cita confirmada exitosamente.");
            }
        }

        // Cerrar sesión
        document.getElementById("logoutBtn").addEventListener("click", function () {
            if (confirm("¿Cerrar sesión como doctor?")) {
                window.location.href = "login.html";
            }
        });

        // Inicializar
        document.addEventListener('DOMContentLoaded', function() {
    mostrarSeccion('citas-pendientes');
    renderCitasPendientes();
    renderTodasCitas();
    renderHistorialPacientes(); 
});