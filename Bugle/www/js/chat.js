//Make Connection
var socket = io.connect();

//Query DOM
var message = document.getElementById('message');
var handle = document.getElementById('handle');
var btn = document.getElementById('send');
console.log("Button .... "+btn);
var output = document.getElementById('output');

//Emit Events
btn.addEventListener('click',function(){
    socket.emit('chat', {
        message: message.value,
        handle: handle.value
    });
});

//Listen for Events
socket.on('chat',function(data){
    output.innerHTML += '<p><strong>' + data.handle + ':</strong>' + data.message + '</p>';
});