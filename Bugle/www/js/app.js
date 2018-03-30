var app = angular.module('root', []);

app.controller('index', ['$scope', '$http', function ($scope, $http) {

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
        { 'id': '3', 'name': 'Event 3', 'location': 'MOrrisville', 'date': '05.11.18' }
    ];

}
]);