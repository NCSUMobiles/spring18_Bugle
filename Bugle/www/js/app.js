var app = angular.module('root', ['chat']);

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

app.service('UserService', function () {
    var loggedInUser = '';
    var currentEvents = '';
    var organizations = '';
    var currentEvent = '';
    var currentVolunteers = '';

    return {
        getLoggedInUser: function () {
            return loggedInUser;
        },
        setLoggedInUser: function (usr) {
            loggedInUser = usr;
        },
        getCurrentEvents: function () {
            return currentEvents;
        },
        setCurrentEvents: function (ev) {
            currentEvents = ev;
        },
        getOrganizations: function () {
            return organizations;
        },
        setOrganizations: function (orgs) {
            organizations = orgs;
        },
        getCurrentEvent: function () {
            return currentEvent;
        },
        setCurrentEvent: function (e) {
            currentEvent = e;
        },
        getCurrentVolunteers: function () {
            return currentVolunteers;
        },
        setCurrentVolunteers: function (v) {
            currentVolunteers = v;
        }
    }
});


app.controller('index', ['$scope', '$http', '$window', 'UserService', function ($scope, $http, $window, UserService) {

    $scope.title = 'Bugle Beta App';
   
    // Actual User data will be fetched from the session
    UserService.loggedInUser = {
        "uId": 4, "uName": "Default User", "email": "usr1@vol.com", "mobile": "1232233421", "dob": "21.08.93", "password": "pwd1", "type": "vol"
    };

    $scope.user = UserService.loggedInUser;

    // The actual list will come from the Database via the API
    $scope.organizations = [];
    // $scope.organizations = [
    //     { 'id': '1', 'name': 'Organization 1', 'location': 'Raleigh' },
    //     { 'id': '2', 'name': 'Organization 2', 'location': 'Cary' },
    //     { 'id': '3', 'name': 'Organization 3', 'location': 'MOrrisville' }
    // ];

    // The actual list will come from the Database via the API
    $scope.events = [];
    // $scope.events = [
    //     { 'id': '1', 'name': 'Event 1', 'location': 'Raleigh', 'date': '04.14.18' },
    //     { 'id': '2', 'name': 'Event 2', 'location': 'Cary', 'date': '03.30.18' },
    //     { 'id': '3', 'name': 'Event 3', 'location': 'Morrisville', 'date': '05.11.18' },
    //     { 'id': '4', 'name': 'Event 4', 'location': 'Morrisville', 'date': '05.11.18' },
    //     { 'id': '5', 'name': 'Event 5', 'location': 'Morrisville', 'date': '05.11.18' }
    // ];

    // The actual list will come from the Database via the API
    $scope.event = {};
    // $scope.event = {
    //     'eId': '1', 'eName': 'Event 1', 'location': 'Raleigh', 'datetime': '04.14.18 11:00AM', 'description': 'description of a volunteering event!', 'members': '12', 'uId': 1, 'status': 'active'
    // };

    $scope.eventSelected = false;

    // The actual list will com from the Database via the API
    $scope.volunteers = [];
    // $scope.volunteers = [
    //     { 'uId': '1', 'uName': 'Lin' },
    //     { 'uId': '2', 'uName': 'Tom' },
    //     { 'uId': '3', 'uName': 'Jack' },
    //     { 'uId': '4', 'uName': 'John' }
    // ];

    // Login function Begin.
    $scope.login = function () {
        console.log('login called');

        var loginURL = 'https://bugle-pl-srv.herokuapp.com/login';
        // var loginURL = 'localhost:9000/login';
        var loginInfo = {
            'email': $scope.email,
            'password': $scope.password
        };

        $http({
            url: loginURL,
            method: 'POST',
            data: loginInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function successLogin(response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var user = JSON.parse(response.data.user);
            $scope.greeting = response.data.status + '. Hello ' + user.uName;
            updateScopeUser(user);
            if (user.type === 'vol') {
                $window.location.href = '/volunteer.html';
            } else {
                $window.location.href = '/organization.html';
            }
        }, function failLogin(response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Login function End.


    // Register function Begin
    $scope.register = function (type) {
        $scope.dataLoading = true;
        console.log('register called for ' + type);

        var signupURL = 'https://bugle-pl-srv.herokuapp.com/signup';
        var signupInfo = {
            'u_name': $scope.u_name,
            'email': $scope.email,
            'dob': $scope.dob,
            'password': $scope.password,
            'type': type,
        };

        $http({
            url: signupURL,
            method: 'POST',
            data: signupInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            // var user = JSON.parse(response.data.user);
            var user = response.data.user;
            $scope.greeting = response.data.status + '. Hello ' + user.uName;
            updateScopeUser(user);
            if (type === 'vol') {
                $window.location.href = '/volunteer.html';
            } else {
                $window.location.href = '/organization.html';
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        }).finally(function () {
            $scope.dataLoading = false;
        });
    }
    //Register function end.

    // Get organizations function start
    $scope.getOrganizations = function() {
        console.log('fetching organizations from server');
        $http({
            method: 'GET',
            url: 'https://bugle-pl-srv.herokuapp.com/organizations',
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var orgs = JSON.parse(response.data.organizations);
            updateScopeOrganizations(orgs);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get organizations function end

    // Get volunteer events function start
    $scope.getVolunteerEvents = function (uId) {
        //TODO: remove hardcoded value later.
        uId = 1;
        console.log('fetching events for volunteer ID: ' + uId);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/volunteer-events/' + uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var events = JSON.parse(response.data.events);
            updateScopeEvents(events);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get volunteer events function end

     // Get organization events function start
     $scope.getOrganizationEvents = function (uId) {
        //TODO: remove hardcoded value later.
        uId = 2;
        console.log('fetching events for organization ID: ' + uId);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/organizations/' + uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var events = JSON.parse(response.data.events);
            updateScopeEvents(events);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get organization events function end

    // org selected function start
    $scope.getEventVolunteers = function() {
        console.log('Fetching Events Volunteers for the selected Event: ' + JSON.stringify($scope.eventSelected));
        updateScopeEvent($scope.eventSelected);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/event-volunteers/' + $scope.eventSelected.eId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var volunteers = JSON.parse(response.data.volunteers);
            updateScopeVolunteers(volunteers);
            // console.log('Scope volunteers length: ' + $scope.volunteers.length + ' =>  value is: ' + ($scope.volunteers.length==0));
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // org selected function end

    // Update Scope User function Start
    var updateScopeUser = function (usr) {
        console.log('updating Service user to: ' + usr);
        UserService.loggedInUser = usr;
        $scope.user = usr;
    }
    // Update Scope User function end

    // Update Scope User function Start
    var updateScopeOrganizations = function (orgs) {
        console.log('updating Service organizations to: ' + orgs);
        UserService.organizations = orgs;
        $scope.organizations = orgs;
    }
    // Update Scope User function end

    // Update Scope Events function Start
    var updateScopeEvents = function (ev) {
        console.log('updating Service events to: ' + ev)
        UserService.currentEvents = ev;
        $scope.events = ev;
    }
    // Update Scope Events function end

    // Update Scope Event function Start
    var updateScopeEvent = function (e) {
        console.log('updating Service event to: ' + e)
        UserService.currentEvent = e;
        $scope.event = e;
    }
    // Update Scope Event function end

    // Update Scope Volunteers function Start
    var updateScopeVolunteers = function (v) {
        console.log('updating Service volunteers to: ' + v)
        UserService.currentVolunteers = v;
        $scope.volunteers = v;
    }
    // Update Scope Volunteers function end

}]);

// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
// Chat App Controller
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
app.controller( 'BasicController', ['$scope', 'Messages', function($scope, Messages) {

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