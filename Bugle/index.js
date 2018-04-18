var express = require('express');
var socket = require('socket.io');

var app = express();
app.use(express.static(__dirname + '/www')).use('/node_modules', express.static('node_modules'));

var port = process.env.PORT || 5000;
var server = app.listen(port, function() {
    console.log('server listening on port ' + port);
});

var io = socket(server);
//Handling socket connections
io.on('connection',function(socket){
    console.log('made socket connection',socket.id);
    
    socket.on('chat',function(data){
        io.sockets.emit('chat',data);
    });
});