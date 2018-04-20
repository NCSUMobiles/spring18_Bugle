//Make Connection
var socket = io.connect();

//Query DOM
var message = document.getElementById('message');
var handle = document.getElementById('handle');
var btn = document.getElementById('send');

var output = document.getElementById('output');

//Emit Events
btn.addEventListener('click', function () {
    if (message.value) {
        socket.emit('chat', {
            message: message.value,
            handle: handle.value
        });
    }
    message.value = null;
});

//Listen for Events
socket.on('chat', function (data) {
    output.innerHTML += '<p><strong>' + data.handle + ': </strong>' + data.message + '</p>';
});