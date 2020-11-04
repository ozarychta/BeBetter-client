# BeBetter-client

BeBetter is a mobile habit tracker application with social elements and reward system.

Users can create challenges (habits) and track progress in completing that challenges. Many users can join one challenge and have a conversation in the form of comments. For completing goals users are rewarded with ranking points and achievements, which are visible on user’s profile page.

Project is still under development - additional funcionalities will be added and refactoring will be done.

## Architecture

Project was created in client-server architecture. 

The main purpose of the server application ([BeBetter-server](https://github.com/ozarychta/BeBetter-server)) is to implement application logic, save and read data from a global database and to share it with mobile application with REST API.

BeBetter-client is an Android mobile application that consumes the API and serves as a graphical user interface for the application.

## Main functionalities

#### Signing in with Google account
<img src="screenshots/login1.png" height="500" width="270"> <img src="screenshots/login2.jpg" height="500" width="270">


#### Searching for challenges (habits) to join
<img src="screenshots/main1.png" height="500" width="270"> <img src="screenshots/main2.png" height="500" width="270"> <img src="screenshots/main3.png" height="500" width="270">


#### Or creating new challenges
<img src="screenshots/add_challenge1.png" height="500" width="270"> <img src="screenshots/add_challenge2.png" height="500" width="270">


#### My challenges view showing challenges that user is taking part in
<img src="screenshots/my_challenges1.png" height="500" width="270">


#### Tracking progress in challenges (and receiving points for it)
<img src="screenshots/challenge1.png" height="500" width="270"> <img src="screenshots/challenge2.png" height="500" width="270">


#### Setting reminders
<img src="screenshots/reminder1.png" height="250" width="540">


#### Simple statistics
<img src="screenshots/statistics1.png" height="500" width="270"> <img src="screenshots/statistics2.png" height="500" width="270">


#### Commenting challenges
<img src="screenshots/comments1.png" height="500" width="270">


#### User profile
<img src="screenshots/profile1.png" height="500" width="270"> <img src="screenshots/profile2.png" height="500" width="270">


#### Friends list (also serving as a ranking)
<img src="screenshots/friends1.png" height="500" width="270">


## Technologies and tools
* Android
* Java
* Google Sign-In
* Volley
* MPAndroidChart
* Room Persistence Library, SQLite
* Android Studio


## Functionalities to add in future
* Add more statistics (comparing user with other challenge participants, maybe ranking for each challenge)
* Showing list of challenges that user participates in on his profile page
* ToDoToday view showing in one place progress tracking sections from all challenges active on that day
* Setting custom message for reminder
* Setting profile picture
* Application dark theme
