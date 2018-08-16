## *giveback*

**giveback** is an android app that matches volunteers to non-profit opportunities based on volunteer skill set, cause area interests, and location. The app utilizes [Firebase Realtime Database](https://firebase.google.com/docs/database/)
and the [Google Places SDK for Android](https://developers.google.com/places/android-sdk/intro).

## User Stories

giveback has the following functionalities:

* [x]	User can sign in to their account using Firebase Authentication or Facebook
  * [x] User can see different sign up screens based on identity (either volunteer or nonprofit)
* [x] User is persisted across app restarts
* [x]	Volunteer users can see a list of volunteer opportunities that match their skill set, cause area interests, and location on thier home feed
  * [x] The nonprofit associated with each opportunity is along with each opportunity
  * [x] From the feed, volunteers can see the opportunity's name, description, associated nonprofit, required skill, and associated cause area
  * [x] User can change the radius of the search (10 - 50 miles)
* [x] Nonprofit users can see a list of opportunities they've created on their home feed
* [x] User can click on an opportunity in the feed to be taken to a details page
* [x] From the details view, user can see the opportunity's name, description, associated nonprofit, required skill, associated cause area, address, date, and positions available
  * [x] Volunteer users can register and unregister for opportunities from the details page
  * [x] Nonprofit users can update the opportunity from the details page
  * [x] Users can click on the nonprofit's name to be taken to the non-profit's profile page
  * [x] Users can see the nonprofit's rating (gathered from pervious volunteers) from the details page
  * [x] Users can also view all the volunteers who have signed up for an opportunity from the details page
* [x] Volunteer users have a my commitments page that allows them to see all the opportunities they have signed up for
* [x] Nonprofit users are able to create a new opportunity
  * [x] After creating an opportunity, the opportunity is posted to the top of the nonprofit's home feed
* [x] Users have a profile page that displays their profile image, name, and contact information
  * [x] Users can change their profile image by clicking on their existing profile image
* [x] Users are able to edit their profile or logout of their accounts from their profile page
  * [x] Volunteer users are able to add and delete their skills and causes from the edit profile page
* [x] Users can see an overview of their account from the profile page
  * [x] Volunteer users can see the number of commits, skills, and causes they currently have
  * [x] Nonprofit users can see the number of opportunities they have, the number of volunteers committed to their opportunities, and their rating
* [x] Volunteers visiting a nonprofit's profile have the option of contacting the nonprofit, either by phone or by visitng their website
