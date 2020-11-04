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
<img src="https://drive.google.com/uc?export=view&id=1Naab8EgGYPlFmSbEGulWm1WNLjJXiOmM" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=17btn4PHyP_N6UEqSNdMCMsm4_30s3Zdx" height="500" width="270">


#### Searching for challenges (habits) to join
<img src="https://drive.google.com/uc?export=view&id=1iyq2Vu03xH4jO7dQAJhcFgwYysi3csiu" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1LksPReVDJhQk5I3cGwt7e8H4tFl6NQ-t" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1zdccE_j4CXmbNtlenWZsHsCj-wf5yyD7" height="500" width="270">


#### Or creating new challenges
<img src="https://drive.google.com/uc?export=view&id=1F9lFKl-DZ0gAW1s6P3QzIKVd8Soo1QgP" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1tChgax5XlcvkJLRfmweOtSyMyBfX3KbT" height="500" width="270">


#### My challenges view showing challenges that user is taking part in
<img src="https://drive.google.com/uc?export=view&id=1fvIFkUcwcJ1ds4K0Zb8mGp51PsL5MHlM" height="500" width="270">


#### Tracking progress in challenges (and receiving points for it)
<img src="https://drive.google.com/uc?export=view&id=1IpVHlZqZfcf5DwifG1Rq6LyS3B4mJVje" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1DImTMj0_5w9H-dplECoTLXBtqPz7429v" height="500" width="270">


#### Setting reminders
<img src="https://drive.google.com/uc?export=view&id=1S8mjKX5-scDGvupIMk86a1uWlbl5iNHv" height="250" width="540">


#### Simple statistics
<img src="https://drive.google.com/uc?export=view&id=1NjQqXiwKtMojUZ_uElN7VxUJD9k6qvKH" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1OMP7grUqtZuuAT6CB4gegcAVItJ6B9Vz" height="500" width="270">


#### Commenting challenges
<img src="https://drive.google.com/uc?export=view&id=1VK9EOH8MBe--Yzx-yVmSWVpSnQFk0niC" height="500" width="270">


#### User profile
<img src="https://drive.google.com/uc?export=view&id=1-fEvI27cC5fAM26yXkcf1Df0A_PbKGNm" height="500" width="270"> <img src="https://drive.google.com/uc?export=view&id=1SUM9DZXZKPVKl5ZNGkchzRU8k9FGnfjE" height="500" width="270">


#### Friends list (also serving as a ranking)
<img src="https://drive.google.com/uc?export=view&id=1IXQA7gR_qN0fX6Zh698nE7lk77jLlLK0" height="500" width="270">


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
