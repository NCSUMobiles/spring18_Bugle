//Make Connection
var socket = io.connect();
//Query DOM
var message = document.getElementById('message');
var handle = document.getElementById('handle');
var btn = document.getElementById('send');

var output = document.getElementById('output');
var getbtn = document.getElementById('getdb');

var chatID = document.getElementById('chatID');
var eventID = document.getElementById('eventID');
var mID = document.getElementById('mID');

var url;

//Emit Events
btn.addEventListener('click', function () {
    if (message.value) {
        var dt = new Date();
        var utcDate = dt.toLocaleDateString() + ' ' + dt.toLocaleTimeString();
        socket.emit('chat', {
            message: message.value,
            handle: handle.value,
            //send chat id decide whether or not to send message!
            chatID: chatID.value,
            eventID: eventID.value,
            timestamp: utcDate
        });
        console.log('Sending to play server');
        url = 'https://bugle-pl-srv.herokuapp.com/msgs';
        var parameter = output.innerHTML + '<md-card><p style="word-break: break-all !important; padding:5% !important;"><strong><font size = "-2" color = "blue">' + handle.value + '</font></strong><br>' + message.value + '<br><font size = "-2">' + utcDate + '</font></p></md-card>';
        // console.log("I am sending .. "+parameter);
        saveToPlayServer(url, parameter);
        /*send to play server :
        1. output.innerHTML += '<p><strong>' + message.value + ': </strong>' + handle.value + '</p>';
        2. chat id
        3. event id
        */
    }
    console.log("The chat id: -------------- " + chatID.value);
    console.log("The event id: -------------- " + eventID.value);
    message.value = null;
});

//Listen for Events
socket.on('chat', function (data) {
    /*
         fetch chat id from data
         fetch messages from play server using chat id
         display output from content retrieved by the play server
    */
   
   var shouldScroll = output.scrollTop + output.clientHeight === output.scrollHeight;
    output.innerHTML += '<md-card><p style="word-break: break-all !important; padding:5% !important;"><strong><font size = "-2" color = "blue">' + data.handle + '</font></strong><br>' + data.message + '<br><font size = "-2">' + data.timestamp + '</font></p></md-card>';
    if(!shouldScroll){
        scrollToBottom();
    }
});

function scrollToBottom() {
    output.scrollTop = output.scrollHeight;
  }
  
scrollToBottom();

function initialize() {
    console.log("The message id in initialize: -------------- " + mID.value);
    /* Call play server. We have the chat id, retrieve all chats and display */
    url = 'https://bugle-pl-srv.herokuapp.com/msgs' + "/" + mID.value
    retrieveFromPlayServer(url);
};

function saveToPlayServer(url, parameter) {
    console.log('In the function of POST');
    var postMessage = new XMLHttpRequest();
    postMessage.onreadystatechange = function() {
        if (postMessage.readyState == XMLHttpRequest.DONE) {
            console.log("Got Server response for POST ... ");
            // console.log(postMessage.responseText);
        }
    }
    postMessage.open('POST', url);
    postMessage.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    postMessage.send(JSON.stringify(
        {
            "cId": chatID.value + '',
            "eId": eventID.value + '',
            "message": parameter + ''
        }
    ));
};

function retrieveFromPlayServer(url) {
    console.log('In the function of GET');
    var getMessage = new XMLHttpRequest();
    getMessage.onreadystatechange = function() {
        if (getMessage.readyState == XMLHttpRequest.DONE) {
            console.log("Got Server response for GET ... ");
            // console.log(getMessage.responseText);
            var responseData = getMessage.responseText;
            var jsonResponse = JSON.parse(responseData);
            // console.log(jsonResponse.message);
            var innerJSON = JSON.parse(jsonResponse.message);
            // console.log(innerJSON.msg);
            output.innerHTML = innerJSON.msg;
        }
    }
    getMessage.open('GET', url);
    getMessage.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    getMessage.send();
    console.log('Get call ended.');
};