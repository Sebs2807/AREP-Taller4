// Funcion que saluda al usuario al hacer clic en el botón.
function saludar() {
    let name = document.getElementById("name").value;
    fetch("/hello?name=" + name)
        .then(resp => resp.json())
        .then(data => {
            document.getElementById("respuesta").innerText = data.mensaje;
        });
}
