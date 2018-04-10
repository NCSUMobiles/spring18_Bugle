var app = angular.module('root', []);

app.service('UserService', function () {
    var loggedInUser = '';
    var currentEvents = '';
    var organizations = '';

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
    $scope.event = {
        'id': '1', 'name': 'Event 1', 'location': 'Raleigh', 'date': '04.14.18'
    };

    // The actual list will com from the Database via the API
    $scope.volunteers = [
        { 'id': '1', 'name': 'Lin' },
        { 'id': '2', 'name': 'Tom' },
        { 'id': '3', 'name': 'Jack' },
        { 'id': '4', 'name': 'John' }
    ];

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
    $scope.getVolunteerEvents = function (vId) {
        //TODO: remove hardcoded value later.
        vId = 1;
        console.log('fetching events for volunteer: ' + vId);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/volunteer-events/' + vId;
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

    // Update Scope User function Start
    var updateScopeUser = function (usr) {
        console.log('updating Service user');
        UserService.loggedInUser = usr;
        $scope.user = usr;
    }
    // Update Scope User function end

    // Update Scope User function Start
    var updateScopeOrganizations = function (orgs) {
        console.log('updating Service organizations');
        UserService.organizations = orgs;
        $scope.organizations = orgs;
    }
    // Update Scope User function end

    // Update Scope Events function Start
    var updateScopeEvents = function (ev) {
        console.log('updating Service events')
        UserService.currentEvents = ev;
        $scope.events = ev;
    }
    // Update Scope Events function end

}]);