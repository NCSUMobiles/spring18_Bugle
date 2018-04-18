var app = angular.module('root', ['LocalStorageModule', 'readMore']);

app.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
        .setPrefix('bugleApp')
        .setStorageType('sessionStorage')
        .setNotify(true, true);
});


app.service('UserService', function () {
    var loggedInUser = '';
    var currentEvents = '';
    var organizations = '';
    var organization = '';
    var currentEvent = '';
    var currentVolunteers = '';
    var prevPage = '';

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
        getOrganization: function () {
            return organization;
        },
        setOrganizations: function (org) {
            organization = org;
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
        },
        getPrevPage: function () {
            return prevPage;
        },
        setPrevPage: function (p) {
            prevPage = p;
        }
    }
});


app.controller('index', ['$scope', '$http', '$window', 'UserService', 'localStorageService', function ($scope, $http, $window, UserService, localStorageService) {

    $scope.title = 'Bugle Beta App';

    // Actual User data will be fetched from the session - remove this hardcoding after session is implemented.
    UserService.loggedInUser = {
        "uId": 4, "uName": "Default User", "email": "usr1@vol.com", "mobile": "1232233421", "dob": "21.08.93", "password": "pwd1", "type": "vol"
    };
    // userID = 1-3 for organizations and userID 4-7 for volunteers.    

    $scope.prevPage = {};

    $scope.user = UserService.loggedInUser;

    // The actual list will come from the Database via the API
    $scope.organizations = [];

    // The actual list will come from the Database via the API
    $scope.events = [];

    // The actual list will come from the Database via the API
    $scope.event = {};

    $scope.organization = {};

    $scope.eventSelected = false;

    // The actual list will com from the Database via the API
    $scope.volunteers = [];

    $scope.fetchSession = function () {
        $scope.user = localStorageService.get('sessionUser');
        $scope.organizations = localStorageService.get('organizations');
        $scope.organization = localStorageService.get('organization');
        $scope.events = localStorageService.get('events');
        $scope.event = localStorageService.get('event');
        $scope.volunteers = localStorageService.get('volunteers');
        $scope.prevPage = localStorageService.get('prevPage');

        var validUnauthPage = $window.location.href.includes('/login.html') || $window.location.href.includes('/organisationSignup.html') || $window.location.href.includes('/volunteerSignup.html');

        if (!validUnauthPage && !$scope.user) {
            $window.location.href = '/login.html';
        }

        if (validUnauthPage && $scope.user) {
            if ($scope.user.type == 'vol') {
                $window.location.href = '/volunteer.html';
            } else {
                $window.location.href = '/organization.html';
            }
        }
    }

    // Login function Begin.
    $scope.login = function () {
        $scope.dataLoading = true;
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
            console.log('updating session user to: ' + JSON.stringify(user));
            localStorageService.set('sessionUser', null);
            localStorageService.set('sessionUser', user);
            if (user.type === 'vol') {
                $window.location.href = '/volunteer.html';
            } else {
                $window.location.href = '/organization.html';
            }
        }, function failLogin(response) {
            console.log('ERROR: ' + JSON.stringify(response));
        }).finally(function () {
            $scope.dataLoading = false;
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
            console.log('updating session user to: ' + JSON.stringify(user));
            localStorageService.set('sessionUser', null);
            localStorageService.set('sessionUser', user);
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
    $scope.getOrganizations = function () {
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
            console.log('updating session organizations to: ' + JSON.stringify(orgs));
            localStorageService.set('organizations', null);
            localStorageService.set('organizations', orgs);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get organizations function end

    // Get volunteer events function start
    $scope.getVolunteerEvents = function (uId) {
        //TODO: remove hardcoded value later.
        // uId = 1;
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
            console.log('updating session events to: ' + JSON.stringify(events));
            localStorageService.set('events', null);
            localStorageService.set('events', events);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get volunteer events function end

    // Get organization events function start
    $scope.getOrganizationEvents = function (uId) {
        //uId = 2;//TODO: Remove this
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
            console.log('updating session events to: ' + JSON.stringify(events));
            localStorageService.set('events', null);
            localStorageService.set('events', events);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get organization events function end

    // org selected function start
    $scope.getEventVolunteers = function () {
        console.log('Fetching Events Volunteers for the selected Event: ' + JSON.stringify($scope.eventSelected));
        updateScopeEvent($scope.eventSelected);
        console.log('updating session event to: ' + JSON.stringify($scope.eventSelected));
        localStorageService.set('event', null);
        localStorageService.set('event', $scope.eventSelected);
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
            console.log('updating session volunteers to: ' + JSON.stringify(volunteers));
            localStorageService.set('volunteers', null);
            localStorageService.set('volunteers', volunteers);
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

    // Update Scope PrevPage function Start
    var updateScopePrevPage = function(p) {
        console.log('updating previous page to: ' + p);
        UserService.prevPage = p;
        $scope.prevPage = p;
    }
    // Update Scope PrevPage function Start

    // Update Scope Organization function Start
    var updateScopeOrganization = function (org) {
        console.log('updating Service organizations to: ' + org);
        UserService.organization = org;
        $scope.organization = org;
    }
    // Update Scope Organization function Start

    // open org events function start
    $scope.openOrgEvents = function (org) {
        console.log('updating session organization to: ' + JSON.stringify(org));
        localStorageService.set('organization', null);
        localStorageService.set('organization', org);
        updateScopeOrganization(org);
        $window.location.href = '/orgEvents.html';
    }
    // open org events function end

    // logout function start
    $scope.logout = function () {
        localStorageService.set('sessionUser', null);
        UserService.loggedInUser = null;
        $scope.user = null;
        $window.location.href = '/login.html';
    }
    // logout function end

    // view event details function end
    $scope.viewEventDetails = function (event) {
        console.log('displaying event details for event: ' + JSON.stringify(event));
        console.log('Current scope organization is: ' + JSON.stringify($scope.organization));
        updateScopeEvent(event);
        console.log('updating session event to: ' + JSON.stringify(event));
        localStorageService.set('event', null);
        localStorageService.set('event', event);
        updateScopePrevPage($window.location.href);
        localStorageService.set('prevPage', null);
        localStorageService.set('prevPage', $window.location.href);
        $window.location.href = '/eventDetails.html';
    }
    // view event details function end


    //--Start: Content from Event Details page controller
    $scope.shortMessage = false;
    $scope.longMessage = "long";

    fullDetails = function () {
        if (confirm("This will display the full details {{event.descriptionFull}}")) {
            shortMessage = false;
            longMessage = "long";
        }
        //           	    	$scope.longMessage = "long";
        //           	    	$scope.shortMessage = false;
    }

    $scope.shortOnly = function () {
        if (confirm("This will display the short details again")) {
            shortMessage = "short";
            longMessage = false;
        }
        //           			$scope.shortMessage = "short";
        //           			$scope.longMessage = false;
    }

    $scope.shortMessageDir = false;
    $scope.longMessageDir = "long";

    $scope.fullDirections = function () {
        if (confirm("This will display the full directions {{event.fullDirections}}")) {
            shortMessageDir = false;
            longMessageDir = "long";
        }
        //           	    	$scope.longMessage = "long";
        //           	    	$scope.shortMessage = false;
    }
    //--END: Content from Event Details page controller

    $scope.updateField = false;

    // function to enable users to modify Profile details
    $scope.modify = function(){
        $scope.updateField = true;
    };

    // update user Profile details function
    $scope.update = function(user){

        $scope.dataLoading = true;
        console.log('update user called for user ' + user.uName);

        var updateUserURL = 'https://bugle-pl-srv.herokuapp.com/edit-user'; //edit-user
        var updateUserInfo = {
            'USERS_UID': $scope.user.uId,
            'USERS_UNAME': $scope.user.uName,
            'USERS_EMAIL': $scope.user.email,
            'USERS_TYPE': $scope.user.type,
            'USERS_MOBILE': $scope.user.mobile,
            'USERS_DOB': $scope.user.dob,
            'USERS_PASSWORD': $scope.user.password,
            'USERS_DESCRIPTION': $scope.user.description,
            'USERS_WEBSITE': $scope.user.website,
            'USERS_LOCATION': $scope.user.location
        };

        $http({
            url: updateUserURL,
            method: 'POST',
            data: updateUserInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            console.log('SUCCESS: ' + JSON.stringify(response));
            //var user = response.config.data;
            var user = JSON.parse(response.data.user);
            //var user = response.data.user;
            updateScopeUser(user);
            console.log('updated records for user: ' + user);
            localStorageService.set('sessionUser', null);
            localStorageService.set('sessionUser', user);
            $scope.updateField = false;
            $window.location.href = '/profile.html';
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        }).finally(function () {
            $scope.dataLoading = false;
        });
    };

    // cancel Profile update function
    $scope.cancelUpdate = function(){
        $scope.updateField = false;
        $window.location.href = '/profile.html';
    };

}]);