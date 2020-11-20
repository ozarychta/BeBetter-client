# BeBetter-client

BeBetter is a habit tracker application with reward system and elements of competition.

With BeBetter users can create challenges (habits) and track progress in completing that challenges. Many users can join one challenge and have a conversation in the comments. For completing goals users are rewarded with points and achievements, which are visible on users' profile pages. Users can follow each other and see friends in ranking list. 

BeBetter-client is an Android mobile application that consumes the REST API exposed by ([BeBetter-server](https://github.com/ozarychta/BeBetter-server)) and serves as a graphical user interface for the application.

Project is still under development - additional funcionalities will be added and refactoring will be done.

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
* Google Sign-In, JWT authorization
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
