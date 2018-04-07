var app = angular.module('root', []);

app.controller('index', ['$scope', '$http', '$window', function ($scope, $http, $window) {

    $scope.title = 'Bugle Beta App';
    $scope.loggedInUser = '-';

    // Actual User data will be fetched from the session
    $scope.user = {
        'id': '4',
        'name': 'John',
        'email': 'john@digg.com',
        'type': 'vol'
    };

    // The actual list will com from the Database via the API
    $scope.organizations = [
        { 'id': '1', 'name': 'Organization 1', 'location': 'Raleigh' },
        { 'id': '2', 'name': 'Organization 2', 'location': 'Cary' },
        { 'id': '3', 'name': 'Organization 3', 'location': 'MOrrisville' }
    ];

    // The actual list will com from the Database via the API
    $scope.events = [
        { 'id': '1', 'name': 'Event 1', 'location': 'Raleigh', 'date': '04.14.18' },
        { 'id': '2', 'name': 'Event 2', 'location': 'Cary', 'date': '03.30.18' },
        { 'id': '3', 'name': 'Event 3', 'location': 'Morrisville', 'date': '05.11.18' },
        { 'id': '4', 'name': 'Event 4', 'location': 'Morrisville', 'date': '05.11.18' },
        { 'id': '5', 'name': 'Event 5', 'location': 'Morrisville', 'date': '05.11.18' }
    ];
    // The actual list will com from the Database via the API
    $scope.event = { 
        'id': '1', 'name': 'Event 1', 'location': 'Raleigh', 'date': '04.14.18' 
    };
    
    // The actual list will com from the Database via the API
    $scope.volunteers = [
        {'id': '1', 'name': 'Lin'},
        {'id': '2', 'name': 'Tom'},
        {'id': '3', 'name': 'Jack'},
        {'id': '4', 'name': 'John'}
          
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
            console.log('SUCCESS: ' + JSON.stringify(response));
            var user = JSON.parse(response.data.user);
            $scope.greeting = response.data.status + '. Hello ' + user.uName;
            if (user.type === 'vol') {
                updateScopeUser(user);
                $window.location.href = '/volunteer.html';
            } else {
                updateScopeUser(user);
                $window.location.href = '/organization.html';
            }
           
        }, function failLogin(response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Login function End.


    // Register Vol function Begin
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
            console.log('SUCCESS: ' + JSON.stringify(response));
            var user = JSON.parse(response.data.user);
            $scope.greeting = response.data.status + '. Hello ' + user.uName;          
            if (type === 'vol') {
                updateScopeUser();
                $window.location.href = '/volunteer.html';
            } else {
                updateScopeUser();
                $window.location.href = '/organization.html';
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        }).finally(function() {
            $scope.dataLoading = false;
        });
    }
    //Register Vol function end.


    var updateScopeUser = function(user) {
        console.log('updating scope');
        $scope.loggedInUser = user;
    }
}]);