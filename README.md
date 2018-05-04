# Bugle Project 

## Team 
Sachin Kumar, Sumit Srivastava, Pooja Gosavi, Nida Syed, Lin Zhu, Shyam Ramakrishnan, Cameron Harris

## Tagline 
Making a difference just got easier!

## Description
Bugle is a mobile website that helps bridge the disconnect between volunteers and volunteering organizations. It provides a platform for volunteers interested to participate in volunteering events that suit their interests. It also helps organizations searching for the enthusiastic volunteers who share their passion.

## Project Milestones:

1. Requirement Gathering and Design
* Gathered requirements from the client: [Requirements](Requirements.md)
* Developed wireframes for the design: [Wireframes](Wireframes)

2. Construction and Implementation

Following functionalities implemented as part of the construction and implementation phase:

     1. Login/Sign Up and Profile Page
          - Login and signup functionality for volunteers and organisations
     2. Volunteer Home Page
          - See list of available events to apply
          - Apply for a event
     3. Organization Home Page
          - Host events
     4. Event Details Page and Approve/Reject Page
          - Approve or Reject volunteers for the hosted event
          - More description about the event
     5. Chat Page
          - Volunteers and Organisers can interact with chat
          - Separate persistent chat for each event
     6. Optional Features (Google Login)
          - Option to login with google authentication
          
3. User Testing
* Validation of features

4. Deployment
* Deployment of application on Heroku platform for public access.

## Technology Stack used
* Node express - UI server
* Play framework - backend server with APIs for transacting data with database
* AngularJS - Frontend of website
* Google authentication - Google sign in.
* Google Maps - maps integration for easier navigation
* Heroku - Deployment platform
* PostgreSQL - database server
* Socket.io - websocket framework for Chat functionality

## Features
* Mobile website for Bugle Volunteering app.
* Sign up with email for Volunteers and Organizations.
* Google sign in for Volunteers and Organizations.
* Organizations can host events for volunteers to participate.
* Volunteers can see events based on every organization and apply for the event.
* Organizations can choose volunteers for their events.
* Volunteers can chat with organizers and other volunteers for the event he/she was approved for.
* Google Maps integration for navigation to volunteer site.

## Future Work
* Profile rating system for organizations and volunteers.
* Integration with BeenVerified for background verification.
* Option for users to add profile pictures.
* Facebook integration - signin and sharing.
* Chat notifications on mobile devices.
* Email verification and captcha integration

## Screenshots:
* [Volunteer Screens](Media/Screenshots/Volunteer)
* [Organization Screens](Media/Screenshots/Organization)

## Screencast: 
[![Bugle Video](https://img.youtube.com/vi/LVQ9Lop0-x4/0.jpg)](https://www.youtube.com/watch?v=LVQ9Lop0-x4)

## Progress Reports: 
[Report 1](Documentation/Progress%20Reports/Report%201.txt), [Report 2](Documentation/Progress%20Reports/Report%202.txt), [Report 3](Documentation/Progress%20Reports/Report%203.txt), [Report 4](Documentation/Progress%20Reports/Report%204.txt), [Report 5](Documentation/Progress%20Reports/Report%205.txt), [Report 6](Documentation/Progress%20Reports/Report%206.txt).

## GitHub: 
[Bugle](https://github.com/NCSUMobiles/spring18_Bugle)

## Live URL: 
[Bugle](https://bugle-npm-srv.herokuapp.com/)- deployed on Heroku
Sample Users:
Organization- email: org1@org.com, pwd: o1
Volunteer- email: usr1@vol.com, pwd: v1

## Specification: 
[Backend API Specification](bugle-play-server/README.md)
