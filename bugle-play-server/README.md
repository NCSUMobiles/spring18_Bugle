# Bugle Play Server

## Play Framework

### Steps to run:

- Install latest version of sbt: [SBT](http://www.scala-sbt.org/download.html)
- Clone this repository: `git clone https://github.com/NCSUMobiles/spring18_Bugle`
- cd to server folder: `cd bugle-play-server`
- Type `sbt compile` to compile the application.
- Type `sbt run` to run the application.
- For stopping the server, Press the Enter key on keyboard.
- For opening the sbt console, type `sbt` from the command prompt.
- This server is also deployed on Heroku: [Bugle Server](https://bugle-pl-srv.herokuapp.com/)

### API Specification

#### API Request

| Type | Mapping | Description | Sample Request |
|---|---|---|---|
| GET | /                       | The default home page. | [Bugle](https://bugle-pl-srv.herokuapp.com/) |
| GET | /organizations          | Get a list of Organizations. | [Organizations](https://bugle-pl-srv.herokuapp.com/organizations) |
| GET | /organizations/:orgId   | Get a list of all active events for an organization. | [Events](https://bugle-pl-srv.herokuapp.com/organizations/1) |
| GET | /volunteer-events/:vId  | Get a list of all events which a volunteer has applied to. | [EventList](https://bugle-pl-srv.herokuapp.com/volunteer-events/4) |
| GET | /event-volunteers/:eId  | Get a list of all volunteers who have applied to an event. | [VolunteerList](https://bugle-pl-srv.herokuapp.com/event-volunteers/1) |
| GET | /chats/:vId 	      		| Get a list of all chats for a volunteer. | [Chats](https://bugle-pl-srv.herokuapp.com/chats/1) |
| POST | /event                 | Create an Event. | [event.json](sample-json/request/event.json) |
| POST | /login                 | Login action for a user. | [login.json](sample-json/request/login.json) |
| POST | /signup                | Sign Up action for creating a new user. | [signup.json](sample-json/request/signup.json) |
| POST | /apply-event           | Apply for an event by a volunteer. | [apply-event.json](sample-json/request/apply-event.json) |
| POST | /approve-volunteers    | Approve a list of volunteers for an event | [approve-volunteers.json](sample-json/request/approve-volunteers.json) |
| POST | /reject-volunteers     | Reject a list of volunteers for an event | [reject-volunteers.json](sample-json/request/reject-volunteers.json) |
| POST | /mock-db               | inserts mock values in the DB (Caution! needs key)  | [mock-db.json](sample-json/request/mock-db.json) |
| POST | /reset-db              | deletes all records from all tables in the DB (Caution! needs key)  | [reset-db.json](sample-json/request/reset-db.json) |
| POST | /edit-user             | edits the fields in user table in the DB   | [edit-user.json](sample-json/request/edit-user.json) |
| POST | /edit-event            | edits the fields in events table in the DB  | [edit-event.json](sample-json/request/edit-event.json) |
| GET  | /msgs/:mId             | gets the messages for an message ID.| [Msgs](https://bugle-pl-srv.herokuapp.com/msgs/120) |
| POST | /msgs                  | saves the chat messages.| [msgs.json](sample-json/request/msgs.json) |
| GET  | /get-user/:uId         | gets the user details.| [User](https://bugle-pl-srv.herokuapp.com/get-user/44) |
| POST | /details-val           | does the validations for event details page.| [details-val.json](sample-json/request/details-val.json) |
| POST | /leave-event           | Leave an event by a volunteer.| [EditUser Response](sample-json/request/leave-event.json) |
| POST | /save-gprof            | Save the Google Profile of a User.| [save-gprof.json](sample-json/request/save-gprof.json) |
| GET  | /get-gprof/:pId        | Get the Google Profile of a User.| [Google Profile](sample-json/request/get-gprof.json) |
| GET  | /approved-vols/:eId    | Get list of volunteers approved for an event.| [Approved Volunteers](sample-json/response/approved-vols.json) |
| GET  | /delete-event/:eId     | Delete an event.| [Delete Event](https://bugle-pl-srv.herokuapp.com/delete-event/9999) |

#### API Response

- The API will return JSON data.
- The response will always have a `status` which will be either `success` or `error`, based on whether the request was successful or it failed.
  - In case of failure, the `message` will contain more details about the failure. [Sample Failure Response](sample-json/response/failure.json)
  - In case of success, a sample response for each case is specified below:
  
| Request | Sample Response |
|---|---|
| GET /organizations         | [Organizations Response](sample-json/response/organizations.json) |
| GET /organizations/1       | [Events Response](sample-json/response/events.json) |
| GET /volunteer-events/4    | [EventList Response](sample-json/response/volunteer-events.json) |
| GET /event-volunteers/1    | [VolunteerList Response](sample-json/response/event-volunteers.json) |
| GET /chats/1   			       | [Chats Response](sample-json/response/chats.json) |
| POST /event                | [Event Response](sample-json/response/event.json) |
| POST /login                | [Login Response](sample-json/response/login.json) |
| POST /signup               | [SignUp Response](sample-json/response/signup.json) |
| POST /apply-event          | [ApplyEvent Response](sample-json/response/apply-event.json) |
| POST /approve-volunteers   | [ApproveVolnteers Response](sample-json/response/approve-volunteers.json) |
| POST /reject-volunteers    | [RejectVolunteers Response](sample-json/response/reject-volunteers.json) |
| POST /mock-db              | [MockDB Response](sample-json/response/mock-db.json) |
| POST /reset-db             | [ResetDB Response](sample-json/response/reset-db.json) |
| POST /edit-user            | [EditUser Response](sample-json/response/edit-user.json) |
| POST /edit-event           | [EditEvent Response](sample-json/response/edit-event.json) |
| GET  /msgs/:mId            | [ApproveVolnteers Response](sample-json/response/get-msgs.json) |
| POST /msgs                 | [RejectVolunteers Response](sample-json/response/msgs.json) |
| GET  /get-user/:uId        | [MockDB Response](sample-json/response/get-user.json) |
| POST /details-val          | [ResetDB Response](sample-json/response/details-val.json) |
| POST /leave-event          | [EditUser Response](sample-json/response/leave-event.json) |
| POST /save-gprof           | [EditEvent Response](sample-json/response/save-gprof.json) |
| GET  /get-gprof/:pId       | [ResetDB Response](sample-json/response/get-gprof.json) |
| GET  /approved-vols/:eId   | [EditUser Response](sample-json/response/approved-vols.json) |
| GET  /delete-event/:eId    | [EditEvent Response](sample-json/response/delete-event.json) |
