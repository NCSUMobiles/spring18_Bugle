var express = require('express');
 
var server = express();
server.use(express.static(__dirname + '/www')).use('/node_modules', express.static('node_modules'));

var port = process.env.PORT || 5000;
server.listen(port, function() {
    console.log('server listening on port ' + port);
});