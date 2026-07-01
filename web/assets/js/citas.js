const fechaInput = document.getElementById('fecha');
        const hoy = new Date().toISOString().split('T')[0];
        fechaInput.setAttribute('min', hoy);

        // Verificar si el usuario está registrado (sesión activa)
        function estaRegistrado() {
            return localStorage.getItem('sesion_usuario') !== null;}

        // Enviar formulario
        document.getElementById('formCita').addEventListener('submit', function(e) {
            e.preventDefault();

            // Verificamos si hay sesión activa
            if (!estaRegistrado()) {
                const confirmar = confirm("Debes estar registrado para solicitar una cita.\n\n¿Quieres ir a la página de registro ahora?");
                
                if (confirmar) {
                    window.location.href = "Registrarse.html";
                }
                return; // No continúa con la cita
            }
// Si está registrado, procedemos normalmente
            const nombre = document.getElementById('nombre').value;
            const telefono = document.getElementById('telefono').value;
            const servicio = document.getElementById('servicio').value;
            const fecha = document.getElementById('fecha').value;
            const hora = document.getElementById('hora').value;

            if (nombre && telefono && servicio && fecha && hora) {
                alert(` ¡Cita solicitada correctamente!\n\n` +
                      `Paciente: ${nombre}\n` +
                      `Servicio: ${servicio}\n` +
                      `Fecha: ${fecha}\n` +
                      `Hora: ${hora}\n\n` +
                      `Te contactaremos pronto para confirmar.`);

                this.reset();
            } else {
                alert("Por favor completa todos los campos obligatorios.");
            }
        });