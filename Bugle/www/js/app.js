var app = angular.module('root', ['LocalStorageModule', 'readMore', 'ngMaterial', 'ngMessages']);

app.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
        .setPrefix('bugleApp')
        .setStorageType('sessionStorage')
        .setNotify(true, true);
});

app.config(function ($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('green')
        .accentPalette('indigo');
});


app.service('UserService', function () {
    var loggedInUser = '';
    var currentEvents = '';
    var organizations = '';
    var organization = '';
    var currentEvent = '';
    var currentVolunteers = '';
    var prevPage = '';
    var chats = '';
    var chat = '';

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
        },
        getChats: function () {
            return chats;
        },
        setChats: function (ch) {
            chats = ch;
        },
        getChat: function () {
            return chat;
        },
        setChat: function (c) {
            chat = c;
        }
    }
});


app.controller('index', ['$scope', '$http', '$window', '$mdToast', 'UserService', 'localStorageService', function ($scope, $http, $window, $mdToast, UserService, localStorageService) {

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

    // The actual list will come from the Database via the API
    $scope.volunteers = [];

    // boolean flag set to true if the current event is applied to by the user.
    $scope.appliedStatus = null;

    // chats
    $scope.chats = [];

    // chat
    $scope.chat = {};

    $scope.fetchSession = function () {
        $scope.user = localStorageService.get('sessionUser');
        $scope.organizations = localStorageService.get('organizations');
        $scope.organization = localStorageService.get('organization');
        $scope.events = localStorageService.get('events');
        $scope.event = localStorageService.get('event');
        $scope.volunteers = localStorageService.get('volunteers');
        $scope.prevPage = localStorageService.get('prevPage');
        $scope.chats = localStorageService.get('chats');
        $scope.chat = localStorageService.get('chat');

        // do this only on the event details page for volunteers.
        if ($scope.user && $scope.user.type=='vol' && $window.location.href.includes('/eventDetails.html')) {
            console.log('checking for event details');
            //fetch org name and check if event is joined.
            //userID of the organizer: $scope.event.uId - fetch the name of this organizer from the API.
            //check applied status also from the API - pass $scope.user.uId, eId and if applicant table has entry for $scope.user.uId and eId then isEventApplied is true.;
            var data = {
                "orgID": $scope.event.uId,
                "userID": $scope.user.uId,
                "eventID":  $scope.event.eId
            }

            $http({
                url: 'https://bugle-pl-srv.herokuapp.com/details-val',
                method: 'POST',
                data: data,
                headers: { 'Content-Type': 'application/json' }
            }).then(function (response) {
                if (response.data.status != 'error') {
                    console.log('SUCCESS: ' + JSON.stringify(response));
                    $scope.detailOrgName = response.data.orgName;
                    $scope.appliedStatus = response.data.appliedStatus;
                } else {
                    console.log(response.data.message);
                    showToast('Could not validate Event applied status!');
                }
            }, function (response) {
                console.log('ERROR: ' + JSON.stringify(response));
            }).finally(function () {
                $scope.dataLoading = false;
            });
        }

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

        //TODO: check how to implement this logic
        $scope.isEventApplied = false;

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
            if (response.data.status != 'error') {
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
            } else {
                console.log(response.data.message);
                showToast(response.data.message);
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
            if (response.data.status != 'error') {
                console.log('SUCCESS: ' + JSON.stringify(response));
                $window.location.href = '/login.html';
                showToast('Successfully Signed up, you can Login now.');
            } else {
                console.log(response.data.message);
                showToast('Sorry Could not sign up! Try again later.');
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
            if (response.data.status != 'error') {
                console.log('SUCCESS: ' + JSON.stringify(response));
                var orgs = JSON.parse(response.data.organizations);
                updateScopeOrganizations(orgs);
                console.log('updating session organizations to: ' + JSON.stringify(orgs));
                localStorageService.set('organizations', null);
                localStorageService.set('organizations', orgs);
            } else {
                console.log(response.data.message);
                showToast('Could not load organizations');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get organizations function end

    // Get volunteer events function start
    $scope.getVolunteerEvents = function (uId) {
        console.log('fetching events for volunteer ID: ' + uId);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/volunteer-events/' + uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            if (response.data.status != 'error') {
                console.log('SUCCESS: ' + JSON.stringify(response));
                var events = JSON.parse(response.data.events);
                updateScopeEvents(events);
                console.log('updating session events to: ' + JSON.stringify(events));
                localStorageService.set('events', null);
                localStorageService.set('events', events);
            } else {
                console(response.data.message);
                showToast('Could not load events.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get volunteer events function end

    // Get organization events function start
    $scope.getOrganizationEvents = function (uId) {
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
    var updateScopePrevPage = function (p) {
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
        localStorageService.clearAll();
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

    // function to enable users to modify Profile details start
    $scope.modify = function () {
        $scope.updateField = true;
    };
    // function to enable users to modify Profile details end

    // function for a volunteer to apply for a event start.
    $scope.applyEvent = function (event) {
        console.log('applying For event: ' + JSON.stringify(event) + ', by volunteer: ' + JSON.stringify($scope.user));

        var eventApplication = {
            'e_Id': event.eId,
            'u_Id': $scope.user.uId
        };

        console.log('application: ' + JSON.stringify(eventApplication));

        $http({
            method: 'POST',
            url: 'https://bugle-pl-srv.herokuapp.com/apply-event',
            data: eventApplication,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            console.log('response: ' + JSON.stringify(response));
            if (response.status != 'error') {
                var message = JSON.stringify(response.data.message);
                console.log('SUCCESS: ' + JSON.stringify(message));
                showToast('Applied to Event!');
                $scope.appliedStatus = 'applied';
            } else {
                console.log('ERROR: ' + JSON.stringify(response.data.message));
                showToast('Sorry, Could not apply for event!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a volunteer to apply for a event end.

     // function for a volunteer to leave an event start.
     $scope.leaveEvent = function (event) {
        console.log('applying For event: ' + JSON.stringify(event) + ', by volunteer: ' + JSON.stringify($scope.user));

        var Leavingevent = {
            'e_Id': event.eId,
            'u_Id': $scope.user.uId
        };

        console.log('Leaving event: ' + JSON.stringify(Leavingevent));

        $http({
            method: 'POST',
            url: 'https://bugle-pl-srv.herokuapp.com/leave-event',
            data: Leavingevent,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            console.log('response: ' + JSON.stringify(response));
            if (response.status != 'error') {
                var message = JSON.stringify(response.data.message);
                console.log('SUCCESS: ' + JSON.stringify(message));
                showToast('Left Event!');
                $scope.appliedStatus = 'left';
            } else {
                console.log('ERROR: ' + JSON.stringify(response.data.message));
                showToast('Sorry, Could not leave event!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a volunteer to leaven an event end.

    var showToast = function (message) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(message)
                .hideDelay(3000)
        );
    }

    // update user Profile details function
    $scope.update = function (user) {

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
    $scope.cancelUpdate = function () {
        $scope.updateField = false;
        $window.location.href = '/profile.html';
    };

    //function to redirect to a page on button click
    $scope.gotoPage = function (page) {
        $window.location.href = '/' + page + '.html';
    };

    $scope.createEvent = function () {
        $scope.dataLoading = true;
        console.log('creating event.');

        var eventInfo = {
            "e_name": $scope.eName,
            "location": $scope.location,
            "datetime": $scope.datetime,
            "description": $scope.description,
            "members": $scope.members,
            "u_id": $scope.user.uId,
            "status": "active"
        };

        $http({
            url: 'https://bugle-pl-srv.herokuapp.com/event',
            method: 'POST',
            data: eventInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function successLogin(response) {
            if (response.data.status != 'error') {
                console.log('SUCCESS: ' + JSON.stringify(response));
                $window.location.href = '/organization.html';
                showToast(response.data.message);
            } else {
                console.log(response.data.message);
                showToast(response.data.message);
            }
        }, function failLogin(response) {
            console.log('ERROR: ' + JSON.stringify(response));
        }).finally(function () {
            $scope.dataLoading = false;
        });
    }

    $scope.getChats = function () {
        console.log('getting chats for user: ' + $scope.user.uId);
        var srvURL = 'https://bugle-pl-srv.herokuapp.com/chats/' + $scope.user.uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            //TODO: check if status in response is 'success'
            console.log('SUCCESS: ' + JSON.stringify(response));
            var chats = JSON.parse(response.data.chats);
            updateScopeChats(chats);
            console.log('updating session chats to: ' + JSON.stringify(chats));
            localStorageService.set('chats', null);
            localStorageService.set('chats', chats);
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }

    // Update Scope Chats function Start
    var updateScopeChats = function (ch) {
        console.log('updating Service chats to: ' + ch);
        UserService.chats = ch;
        $scope.chats = ch;
    }
    // Update Scope Chats function end

    // Update Scope Chat function Start
    var updateScopeChat = function (c) {
        console.log('updating Service chat to: ' + c);
        UserService.chat = c;
        $scope.chat = c;
    }
    // Update Scope Chat function end

    $scope.openChat = function (ch) {
        console.log('opening chat for: ' + JSON.stringify(ch));
        updateScopeChat(ch);
        console.log('updating session chat to: ' + JSON.stringify(ch));
        localStorageService.set('chat', null);
        localStorageService.set('chat', ch);
        $window.location.href = '/chat.html';
    };

    $scope.showDetails = function(org) {
        console.log('updating session organization to: ' + JSON.stringify(org));
        localStorageService.set('organization', null);
        localStorageService.set('organization', org);
        updateScopeOrganization(org);
        $window.location.href = '/organizationDetails.html';
    };

    $scope.navigate = function(event) {
        console.log('navigating...');
    }

}]);