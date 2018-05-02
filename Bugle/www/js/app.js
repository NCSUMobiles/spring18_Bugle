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
    var tmpGUser = '';
    var participants = '';

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
        },
        getTmpGUser: function () {
            return tmpGUser;
        },
        setTmpGUser: function (tgu) {
            tmpGUser = tgu;
        },
        getParticipants: function () {
            return participants;
        },
        setParticipants: function (p) {
            participants = p;
        }
    }
});


app.controller('index', ['$scope', '$http', '$window', '$mdToast', 'UserService', 'localStorageService', '$mdDialog', function ($scope, $http, $window, $mdToast, UserService, localStorageService, $mdDialog) {

    var serviceURL = 'https://bugle-pl-srv.herokuapp.com';

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

    $scope.tempGUser = {};

    $scope.isGoogleUser = false;

    $scope.participants = [];

    //Loading Google API
    function onLoad() {
        gapi.load('auth2', function () {
            console.log('gapi initialized');
            gapi.auth2.init();
        });
    }

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
        $scope.tempGUser = localStorageService.get('tempGUser');
        $scope.participants = localStorageService.get('participants');

        //only do this for profile page.
        if ($window.location.href.includes('/profile.html')) {
            console.log('enabling gapi for logout support.');
            if ($scope.user && $scope.user.gprofid === $scope.user.password) {
                gapi.load('auth2', function () {
                    gapi.auth2.init();
                });
                $scope.isGoogleUser = true;
            } else {
                $scope.isGoogleUser = false;
            }
        }

        // do this only on the event details page for volunteers.
        if ($scope.user && $scope.user.type == 'vol' && $window.location.href.includes('/eventDetails.html')) {
            console.log('checking for event details');
            //fetch org name and check if event is joined.
            //userID of the organizer: $scope.event.uId - fetch the name of this organizer from the API.
            //check applied status also from the API - pass $scope.user.uId, eId and if applicant table has entry for $scope.user.uId and eId then isEventApplied is true.;
            var data = {
                "orgID": $scope.event.uId,
                "userID": $scope.user.uId,
                "eventID": $scope.event.eId
            }

            $http({
                url: serviceURL + '/details-val',
                method: 'POST',
                data: data,
                headers: { 'Content-Type': 'application/json' }
            }).then(function (response) {
                // console.log('API response: ' + JSON.stringify(response));
                if (response.data.status != 'error') {
                    console.log('response status: ' + response.data.status);
                    $scope.detailOrgName = response.data.orgName;
                    $scope.appliedStatus = response.data.appliedStatus;
                } else {
                    console.log('Error: ' + response.data.message);
                    showToast('Could not validate Event applied status!');
                }
            }, function (response) {
                console.log('ERROR: ' + JSON.stringify(response));
            }).finally(function () {
                $scope.dataLoading = false;
            });
        }

        var tempPage = $window.location.href.includes('/tempGLogin.html');

        var validUnauthPage = $window.location.href.includes('/login.html') || $window.location.href.includes('/organisationSignup.html') || $window.location.href.includes('/volunteerSignup.html');

        if (!tempPage && !validUnauthPage && !$scope.user) {
            $window.location.href = '/login.html';
        } else if (!tempPage && validUnauthPage && $scope.user) {
            if ($scope.user.type == 'vol') {
                $window.location.href = '/volunteer.html';
            } else {
                $window.location.href = '/organization.html';
            }
        } else {
            var orgPage = $window.location.href.includes('/organization.html') || $window.location.href.includes('/createEvent.html') || $window.location.href.includes('/eventVolunteers.html');
            if (orgPage && $scope.user.type == 'vol') {
                console.log('redirecting lost volunteer to Volunteer Home page.');
                $window.location.href = '/volunteer.html';
            }

            var volPage = $window.location.href.includes('/volunteerEvents.html') || $window.location.href.includes('/volunteer.html') || $window.location.href.includes('/orgEvents.html');
            if (volPage && $scope.user.type == 'org') {
                console.log('redirecting lost organization to Organization Home page.');
                $window.location.href = '/organization.html';
            }
        }

    }

    // Login function Begin.
    $scope.login = function () {
        $scope.dataLoading = true;
        console.log('login called');

        var loginURL = serviceURL + '/login';
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
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var user = JSON.parse(response.data.user);
                updateScopeUser(user);
                console.log('updating session user.');
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

        var signupURL = serviceURL + '/signup';
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
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                $window.location.href = '/login.html';
                showToast('Successfully Signed up, you can Login now.');
            } else {
                console.log('Error: ' + response.data.message);
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
            url: serviceURL + '/organizations',
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var orgs = JSON.parse(response.data.organizations);
                updateScopeOrganizations(orgs);
                console.log('updating session organizations.');
                localStorageService.set('organizations', null);
                localStorageService.set('organizations', orgs);
            } else {
                console.log('Error: ' + response.data.message);
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
        var srvURL = serviceURL + '/volunteer-events/' + uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var events = JSON.parse(response.data.events);
                updateScopeEvents(events);
                console.log('updating session events.');
                localStorageService.set('events', null);
                localStorageService.set('events', events);
            } else {
                console('Error: ' + response.data.message);
                showToast('Could not load volunteer events.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // Get volunteer events function end

    // Get organization events function start
    $scope.getOrganizationEvents = function (uId) {
        console.log('fetching events for organization ID: ' + uId);
        var srvURL = serviceURL + '/organizations/' + uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var events = JSON.parse(response.data.events);
                updateScopeEvents(events);
                console.log('updating session events.');
                localStorageService.set('events', null);
            } else {
                console('Error: ' + response.data.message);
                showToast('Could not load organization events.');
            }
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
        console.log('updating session event.');
        localStorageService.set('event', null);
        localStorageService.set('event', $scope.eventSelected);
        var srvURL = serviceURL + '/event-volunteers/' + $scope.eventSelected.eId;
        console.log('API URL: ' + srvURL);
        //reset the selected checkboxes.
        $scope.selected = [];
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var volunteers = JSON.parse(response.data.volunteers);
                updateScopeVolunteers(volunteers);
                console.log('updating session volunteers.');
                localStorageService.set('volunteers', null);
                localStorageService.set('volunteers', volunteers);
            } else {
                console.log('Error: ' + response.data.message);
                showToast('Could not load volunteers for event.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }
    // org selected function end

    // Update Scope User function Start
    var updateScopeUser = function (usr) {
        console.log('updating Service user.');
        UserService.loggedInUser = usr;
        $scope.user = usr;
    }
    // Update Scope User function end

    // Update Scope User function Start
    var updateScopeOrganizations = function (orgs) {
        console.log('updating Service organizations.');
        UserService.organizations = orgs;
        $scope.organizations = orgs;
    }
    // Update Scope User function end

    // Update Scope Events function Start
    var updateScopeEvents = function (ev) {
        console.log('updating Service events.');
        UserService.currentEvents = ev;
        $scope.events = ev;
    }
    // Update Scope Events function end

    // Update Scope Event function Start
    var updateScopeEvent = function (e) {
        console.log('updating Service event.');
        UserService.currentEvent = e;
        $scope.event = e;
    }
    // Update Scope Event function end

    // Update Scope Volunteers function Start
    var updateScopeVolunteers = function (v) {
        console.log('updating Service volunteers.');
        UserService.currentVolunteers = v;
        $scope.volunteers = v;
    }
    // Update Scope Volunteers function end

    // Update Scope PrevPage function Start
    var updateScopePrevPage = function (p) {
        console.log('updating Service previous page.');
        UserService.prevPage = p;
        $scope.prevPage = p;
    }
    // Update Scope PrevPage function Start

    // Update Scope Organization function Start
    var updateScopeOrganization = function (org) {
        console.log('updating Service organizations.');
        UserService.organization = org;
        $scope.organization = org;
    }
    // Update Scope Organization function Start

    // open org events function start
    $scope.openOrgEvents = function (org) {
        console.log('updating session organization.');
        localStorageService.set('organization', null);
        localStorageService.set('organization', org);
        updateScopeOrganization(org);
        $window.location.href = '/orgEvents.html';
    }
    // open org events function end

    // logout function start
    $scope.logout = function () {
        console.log('normal logout called');
        localStorageService.set('sessionUser', null);
        UserService.loggedInUser = null;
        $scope.user = null;
        localStorageService.clearAll();
        $window.location.href = '/login.html';
    }
    // logout function end

    // logout google user function start
    $scope.logoutGUser = function () {
        console.log('logoutGUser called');
        UserService.loggedInUser = null;
        $scope.user = null;
        localStorageService.clearAll();
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut().then(function () {
            console.log('Google User signed out.');
        });
        $window.location.href = '/login.html';
    }
    // logout google user function start

    // view event details function end
    $scope.viewEventDetails = function (event) {
        console.log('displaying event details for event: ' + JSON.stringify(event));
        console.log('Current scope organization is: ' + JSON.stringify($scope.organization));
        updateScopeEvent(event);
        console.log('updating session event.');
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

    $scope.dobError = false;
    $scope.mobileError = false;

    // function to enable users to modify Profile details start
    $scope.modify = function () {
        $scope.updateField = true;
    };
    // function to enable users to modify Profile details end

    // function for a volunteer to apply for a event start.
    $scope.applyEvent = function (event) {
        var eventApplication = {
            'e_Id': event.eId,
            'u_Id': $scope.user.uId
        };

        console.log('Applying for Event: ' + JSON.stringify(eventApplication));

        $http({
            method: 'POST',
            url: serviceURL + '/apply-event',
            data: eventApplication,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response message: ' + response.data.message);
                showToast('Applied to Event!');
                $scope.appliedStatus = 'applied';
            } else {
                console.log('ERROR: ' + response.data.message);
                showToast('Sorry, Could not apply for event!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a volunteer to apply for a event end.

    // function for a volunteer to leave an event start.
    $scope.leaveEvent = function (event) {
        var Leavingevent = {
            'e_Id': event.eId,
            'u_Id': $scope.user.uId
        };

        console.log('Leaving event: ' + JSON.stringify(Leavingevent));

        $http({
            method: 'POST',
            url: serviceURL + '/leave-event',
            data: Leavingevent,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response message: ' + response.data.message);
                showToast('Left Event!');
                $scope.appliedStatus = 'left';
            } else {
                console.log('ERROR: ' + response.data.message);
                showToast('Sorry, Could not leave event!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a volunteer to leave an event end.

    $scope.selected = [];
    // toggle selected checkbox start
    $scope.toggle = function (uId, selected) {
        if (selected.includes(uId)) {
            for (var i = 0; i < selected.length; i++) {
                if (selected[i] == uId) {
                    selected.splice(i, 1);
                    break;
                }
            }
        } else {
            selected.push(uId);
        }
    }
    // toggle selected checkbox end


    // function for a org to approve volunteer start.
    $scope.approveVol = function (event) {
        var eventApplication = {
            'e_Id': event.eId,
            'u_Ids': '' + $scope.selected + ''
        };

        console.log('Approving Volunteers: ' + JSON.stringify(eventApplication));

        $http({
            method: 'POST',
            url: serviceURL + '/approve-volunteers',
            data: eventApplication,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response message: ' + response.data.message);
                $scope.getEventVolunteers();
                showToast('Volunteer(s) approved for Event!');
            } else {
                console.log('ERROR: ' + response.data.message);
                showToast('Sorry, approval failed!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a org to approve volunteer end.

    // function for a org to reject volunteer start.
    $scope.rejectVol = function (event) {
        var eventApplication = {
            'e_Id': event.eId,
            'u_Ids': '' + $scope.selected + ''
        };

        console.log('Rejecting Volunteers: ' + JSON.stringify(eventApplication));

        $http({
            method: 'POST',
            url: serviceURL + '/reject-volunteers',
            data: eventApplication,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != "error") {
                console.log('response message: ' + response.data.message);
                showToast('Volunteer(s) succesfully rejected for event.');
                $scope.getEventVolunteers();
            } else {
                console.log('ERROR: ' + response.data.message);
                showToast('Sorry, rejection failed!');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    };
    // function for a org to reject volunteer end.

    // Function to show toast message start
    var showToast = function (message) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(message)
                .hideDelay(3000)
        );
    }
    // Function to show toast message end

    function testingDateStr(str) {
        if (str) {
            console.log('validating date of birth.');
            var t = str.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
            if (t === null)
                return false;
            var m = +t[1], d = +t[2], y = +t[3];

            // Below should be a more acurate algorithm
            if (m >= 1 && m <= 12 && d >= 1 && d <= 31) {
                return true;
            }

            return false;
        } else {
            console.log('no date of birth to validate.');
            return true;
        }
    }

    function testingPhoneStr(str) {
        if (str) {
            console.log('validating Phone number.');
            var t = str.match(/^(\d{10})$/);
            if (t === null)
                return false;

            return true;
        } else {
            console.log('no phone number to validate.');
            return true;
        }
    }

    // update user Profile details function
    $scope.update = function (user) {
        console.log('update user called for user ' + user.uName);
        var updateUserURL = serviceURL + '/edit-user';
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

        //validating input
        //check for correct format of dob
        if (!testingDateStr(user.dob)) {
            $scope.dobError = true;
        } else {
            $scope.dobError = false;
        }

        //check for correct format of dob
        if (!testingPhoneStr(user.mobile)) {
            $scope.mobileError = true;
        } else {
            $scope.mobileError = false;
        }

        //calling the API only if there are no error on forms
        if (!$scope.dobError && !$scope.mobileError) {
            $scope.dataLoading = true;
            $http({
                url: updateUserURL,
                method: 'POST',
                data: updateUserInfo,
                headers: { 'Content-Type': 'application/json' }
            }).then(function (response) {
                // console.log('API response: ' + JSON.stringify(response));
                if (response.data.status != 'error') {
                    var user = JSON.parse(response.data.user);
                    updateScopeUser(user);
                    console.log('updated records for user.');
                    localStorageService.set('sessionUser', null);
                    localStorageService.set('sessionUser', user);
                    $scope.updateField = false;
                    $window.location.href = '/profile.html';
                    $scope.dobError = false;
                    $scope.mobileError = false;
                } else {
                    console.log('ERROR: ' + response.data.message);
                    showToast('Something went wrong while updating Profile. Please try again later.');
                }
            }, function (response) {
                console.log('ERROR: ' + JSON.stringify(response));
                showToast('Something went wrong while updating Profile. Please try again later.');
            }).finally(function () {
                $scope.dataLoading = false;
            });
        } else {
            if ($scope.dobError) {
                showToast('Error: Please Check Date of Birth.');
            }
            if ($scope.mobileError) {
                showToast('Error: Please Check Mobile number.');
            }
        }
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
            url: serviceURL + '/event',
            method: 'POST',
            data: eventInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function successLogin(response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response message: ' + response.data.message);
                $window.location.href = '/organization.html';
                showToast(response.data.message);
            } else {
                console.log(response.data.message);
                showToast(response.data.message);
            }
        }, function failLogin(response) {
            console.log('ERROR: ' + JSON.stringify(response));
            showToast('Something went wrong while creating Event. Please try again later.');
        }).finally(function () {
            $scope.dataLoading = false;
        });
    }

    $scope.getChats = function () {
        console.log('getting chats for user: ' + $scope.user.uId);
        var srvURL = serviceURL + '/chats/' + $scope.user.uId;
        console.log('API URL: ' + srvURL)
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var chats = JSON.parse(response.data.chats);
                updateScopeChats(chats);
                console.log('updating session chats.');
                localStorageService.set('chats', null);
                localStorageService.set('chats', chats);
            } else {
                console.log('Error: ' + response.data.message);
                showToast('Error while fetching chats for user.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }

    // Update Scope Chats function Start
    var updateScopeChats = function (ch) {
        console.log('updating Service chats.');
        UserService.chats = ch;
        $scope.chats = ch;
    }
    // Update Scope Chats function end

    // Update Scope Chat function Start
    var updateScopeChat = function (c) {
        console.log('updating Service chat.');
        UserService.chat = c;
        $scope.chat = c;
    }
    // Update Scope Chat function end

    $scope.openChat = function (ch) {
        console.log('opening chat.');
        updateScopeChat(ch);
        console.log('updating session chat.');
        localStorageService.set('chat', null);
        localStorageService.set('chat', ch);
        $window.location.href = '/chat.html';
    };

    $scope.showDetails = function (org) {
        console.log('updating session organization.');
        localStorageService.set('organization', null);
        localStorageService.set('organization', org);
        updateScopeOrganization(org);
        $window.location.href = '/organizationDetails.html';
    };

    //For Google Maps Navigation
    $scope.navigate = function (event) {
        console.log('navigating...');
    }

    //For Google Sign In
    function onSignIn(googleUser) {
        console.log('sign in called');
        var profile = googleUser.getBasicProfile();
        // console.log('ID: ' + profile.getId());
        // console.log('Name: ' + profile.getName());
        // console.log('Image URL: ' + profile.getImageUrl());
        // console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.

        var gUser = {
            "gprofid": profile.getId(),
            "uName": profile.getName(),
            "email": profile.getEmail(),
            "type": $scope.gUserType
        };

        var userEmail = profile.getEmail();
        // console.log('The user email is: ' + userEmail);
        if (userEmail == null) {
            console.log('Google Profile does not have an Email!');
            showToast('Google Profile does not have Email! Sign in using Email');
            //TODO: logout the Google Profile here?
            $window.location.href = '/login.html';
        } else {
            $scope.dataLoading = true;
            //fetch user from our database using goolge profile ID, if present.
            $http({
                url: serviceURL + '/get-gprof/' + profile.getId(),
                method: 'GET',
                headers: { 'Content-Type': '*/*' }
            }).then(function successLogin(response) {
                // console.log('API response: ' + JSON.stringify(response));
                if (response.data.status != 'error') {
                    console.log('response status: ' + response.data.status);
                    var dbuser = JSON.parse(response.data.user);
                    if (dbuser != null) {
                        console.log('this google user exists in our database.');
                        //set user using fetched user and redirect accordingly.
                        updateScopeUser(dbuser);
                        console.log('updating session user.');
                        localStorageService.set('sessionUser', null);
                        localStorageService.set('sessionUser', dbuser);
                        // console.log('The dbuser type is: ' + dbuser.type);
                        if (dbuser.type == 'vol') {
                            $window.location.href = '/volunteer.html';
                        } else if (dbuser.type == 'org') {
                            $window.location.href = '/organization.html';
                        }
                    } else {
                        console.log('This google user is here for the first time. Save them to our database.');
                        //show choose type page first and then save the user from there.
                        updateScopeTempUser(gUser);
                        console.log('updating local temp (google) user.');
                        localStorageService.set('tempGUser', null);
                        localStorageService.set('tempGUser', gUser);
                        $window.location.href = '/tempGLogin.html';
                    }
                } else {
                    console.log('Could not read the details for this Google user from our database.');
                    showToast('Google Login failed. Signup using Email!');
                    $window.location.href = '/login.html';
                }
            }, function failLogin(response) {
                console.log('Could not read the details for this Google user from our database.');
                showToast('Google Login failed. Signup using Email!');
                $window.location.href = '/login.html';
            }).finally(function () {
                $scope.dataLoading = false;
            });
        }

        $scope.$digest();
    }
    window.onSignIn = onSignIn;

    // Update Scope Temp User function Start
    var updateScopeTempUser = function (usr) {
        console.log('updating Temp Google user.');
        UserService.loggedInUser = usr;
        $scope.tempGUser = usr;
    }
    // Update Scope Temp User function end

    $scope.setGUserType = function (type) {
        $scope.dataLoading = true;
        var usr = $scope.tempGUser;
        // console.log('TempGUser is: ' + JSON.stringify(usr));
        usr.type = type;

        console.log('Google User has set their type.');
        //saving this user to our database.
        var saveGUser = serviceURL + '/save-gprof';
        var gUserInfo = {
            'u_name': usr.uName,
            'email': usr.email,
            'gprofid': usr.gprofid,
            'password': usr.gprofid,
            'type': type
        };

        $http({
            url: saveGUser,
            method: 'POST',
            data: gUserInfo,
            headers: { 'Content-Type': 'application/json' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var user = JSON.parse(response.data.user);
                user.password = usr.gprofid;
                user.gprofid = usr.gprofid;
                updateScopeUser(user);
                localStorageService.set('sessionUser', null);
                localStorageService.set('sessionUser', user);
                console.log('removing scope temp Google User.');
                updateScopeTempUser(null);
                localStorageService.set('tempGUser', null);
                if (user.type === 'vol') {
                    $window.location.href = '/volunteer.html';
                } else {
                    $window.location.href = '/organization.html';
                }
            } else {
                console.log('Error: ' + response.data.message);
                showToast('Sorry Could not sign in! Try again later.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
            showToast('Sorry Could not sign in! Try again later.');
        }).finally(function () {
            $scope.dataLoading = false;
        });
    }

    $scope.loadParticipants = function (eid, ev) {
        console.log('Showing participants for event: ' + eid);
        var srvURL = serviceURL + '/approved-vols/' + eid;
        $http({
            method: 'GET',
            url: srvURL,
            headers: { 'Content-Type': '*/*' }
        }).then(function (response) {
            // console.log('API response: ' + JSON.stringify(response));
            if (response.data.status != 'error') {
                console.log('response status: ' + response.data.status);
                var participants = JSON.parse(response.data.volunteers);
                updateScopeParticipants(participants);
                console.log('updating session participants.');
                localStorageService.set('participants', null);
                localStorageService.set('participants', participants);
                $mdDialog.show({
                    controller: 'index',
                    templateUrl: 'participants.modal.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            } else {
                console.log('Error: ' + response.data.message);
                showToast('Could not load participants for event.');
            }
        }, function (response) {
            console.log('ERROR: ' + JSON.stringify(response));
        });
    }

    $scope.okPModal = function() {
        $mdDialog.cancel();
    }

    // Update Scope Participants function Start
    var updateScopeParticipants = function (p) {
        console.log('updating Service participants.');
        UserService.participants = p;
        $scope.participants = p;
    }
    // Update Scope Participants function end

}]);