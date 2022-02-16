  # Movie rating application for Android with node.js server and FCM for notifications. 

  For the service part node.js and mongodb along with mongodb tools will be needed for the server to be run.
  
  For the notification part with FCM, you will need to generate your fcm server config file which is found
inside the service accounts tab of your Firebase project
  
  ###### To initialize the node.js the following command will be needed:
```
npm install
```
  ###### To load the database that I have created into the new device the following command will be run inside the terminal(from the server directory):
```
mongorestore --db movies ./dump/movies/ 
```
  ###### After all that you can start the server by typing the following command:
```
npm run devStart
```
  
  The server is found at http://localhost:3000/movies, where we find all the movies inside the database.
  
  
  The android application consists of a RecyclerView which shows the movies that are returned by the server
when the aidl service makes the get request to it. The list can be sorted by the movie name, rating or the
release date.

  When a movie from the list is selected a new activity is launched, that shows various information about
the movie and where you can rate the respective movie.

| First Header  | Second Header | Third Header  |
| ------------- | ------------- | ------------- |
| <a href="url"><img src="https://github.com/octavians23/movie-rating-app-android/blob/main/images/list_app.png" width="250" height="500"></a> | Content Cell  |               |

  ###### The main activity: 
  
