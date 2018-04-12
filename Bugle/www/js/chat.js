'use strict';

// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
// Chat App Module
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
var basicChat = angular.module( 'BasicChat', ['chat'] );

angular.module('chat').constant( 'config', {
    rltm: {
        service: 'pubnub', 
        config: {
            publishKey: 'pub-c-e576563c-1012-43eb-854b-f347ae67ebee',
            subscribeKey: 'sub-c-8bdbbb38-3e09-11e8-afae-2a65d00afee8'
        }
    }
});

// or use socket.io
// make sure to run socket.io-server from rltm.js
// angular.module('chat').constant( 'config', {
//     rltm: {
//         service: 'socketio', 
//         config: {
//             endpoint: 'http://localhost:5000'
//         }
//     }
// });


// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
// Chat App Controller
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
basicChat.controller( 'BasicController', ['$scope', 'Messages', function($scope, Messages) {

    // Sent Indicator
    $scope.status = "";

    // Keep an Array of Messages
    $scope.messages = [];

    var sillyname = function() {
        // Basic Random
        function rnd(n) { return Math.floor(Math.random()*n) }
    
        // First Name
        return ["Runny","Buttercup","Dinky","Princess","Crusty",
        "Greasy","Gidget","Cheesypoof","Lumpy","Wacky","Tiny","Flunky",
        "Fluffy","Zippy","Doofus","Gobsmacked","Slimy","Grimy","Salamander",
        "Dr","Burrito","Bumpy","Loopy",
        "Snotty","Irving","Egbert"][rnd(25)] +
    
        // Last Name
        ["Waffer","Lilly","Bubblegum","Sand","Fuzzy","Kitty",
        "Puppy","Snuggles","SpacePrincess","Stinky","Lulu",
        "Lala","Sparkle","Glitter",
        "Silver","Golden","Rainbow","Cloud",
        "Rain","Stormy","Wink","Sugar",
        "Twinkle","Star","Halo","Angel"][rnd(25)];
    };
    
    $scope.me = {name: sillyname()};

    // Set User Data
    Messages.user($scope.me);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Get Received Messages and Add it to Messages Array.
    // This will automatically update the view.
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    var chatmessages = document.querySelector(".chat-messages");

    Messages.receive(function(msg) {
        
        $scope.messages.push(msg);
    
        setTimeout(function() {
            chatmessages.scrollTop = chatmessages.scrollHeight;
        }, 10);

    });

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Send Messages
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    $scope.send = function() {

        Messages.send({data: $scope.textbox});
        
        $scope.status = "sending";
        $scope.textbox = "";

        setTimeout(function() { 
            $scope.status = "" 
        }, 1200 );

    };

} ] );