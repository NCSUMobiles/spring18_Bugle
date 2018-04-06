var app = angular.module('root', []);

app.controller('index', ['$scope', '$http', '$window', function ($scope, $http, $window) {

    $scope.title = 'Bugle Beta App';

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
        { 'id': '3', 'name': 'Event 3', 'location': 'Morrisville', 'date': '05.11.18' }
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
        }).then(function (response) {
            console.log('SUCCESS: ' + JSON.stringify(response));
            $scope.greeting = response.data.status;
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Login function End.


    // Register Vol function Begin
    $scope.register = function (type) {
        console.log('registerVol called');

        var signupURL = 'https://bugle-pl-srv.herokuapp.com/signup';
        var signupInfo = {
            'u_name': $scope.u_name,
            'email': $scope.email,
            'mobile': $scope.mobile,
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
            $scope.greeting = response.data.status;
            $window.location.href = '/volunteer.html';
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    //Register Vol function end.

}]);