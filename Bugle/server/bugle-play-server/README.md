# Bugle Play Server

## Play Framework

### Steps to run:

- Install latest version of sbt: [SBT](http://www.scala-sbt.org/download.html)
- Clone this repository: git clone https://github.com/srivassumit/bugle-play-server
- cd to server folder: cd Bugle/server/bugle-play-server
- Type sbt run to run the application.
- For stopping the server, Press the Enter key on keyboard.
- For opening the sbt console, type sbt from the command prompt.
- This server is also deployed on Heroku: [Bugle Server](https://bugle-pl-srv.herokuapp.com/)

### API Specification

| Type | mapping | description | Sample Request |
|---|---|---|---|
| GET | /                           | The default home page. | [Bugle](https://bugle-pl-srv.herokuapp.com/) |
| GET | /organizations              | Get a list of Organizations. | [Organizations](https://bugle-pl-srv.herokuapp.com/organizations) |
| GET | /organizations/:orgId       | Get a list of events for an organization. | [Events](https://bugle-pl-srv.herokuapp.com/organizations/1) |
| GET | /volunteer-events/:vId      | Get a list of event which a volunteer has applied to. | [EventList](https://bugle-pl-srv.herokuapp.com/volunteer-events/4) |
| GET | /event-volunteers/:eId      | Get a list of volunteers who have applied to an event. | [VolunteerList](https://bugle-pl-srv.herokuapp.com/event-volunteers/1) |
| POST | /event                     | Create an Event. | [event.json](sample-json/event.json) |
| POST | /login                     | Login action for a user. | [login.json](sample-json/login.json) |
| POST | /signup                    | Sign Up action for creating a new user. | [signup.json](sample-json/signup.json) |
| POST | /apply-event               | Apply for an event by a volunteer. | [apply-event.json](sample-json/apply-event.json) |
| POST | /approve-volunteers        | Approve a list of volunteers for an event | [approve-volunteers.json](sample-json/approve-volunteers.json) |
| POST | /reject-volunteers         | Reject a list of volunteers for an event | [reject-volunteers.json](sample-json/reject-volunteers.json) |