# Share Taxi App
#### *A simple android app for sharing taxi*

Share Taxi is an app that enables a user to search for an existing of offer his own taxi ride so that he could share a taxi ride with someone, which leads to cheaper taxi rides. The user logs into app with his/her account and can post his own ride and/or search for someone else's ride that suits him. Once a user accepts another user's ride they can communicate using the app's chat functionality to arrange details. The app uses realtime database for rides, users, messages and chat functionalities as well as geolocationg services for quick address retrieving.

### Major challenges
The app uses a variety of functionalities which results in a lot of different possible approaches. For the realtime database handling Firebase Realtime Database was used. Also, the user authentication functionality is handled through Firebase Authentication. 

## Utilised snippets/solutions/libraries/SO answers

[ButterKnife](http://jakewharton.github.io/butterknife/)

[RecyclerView dependencies](https://developer.android.com/topic/libraries/support-library/packages.html)

[Android Support Library: *com.android.support:design:26.1.0*](https://developer.android.com/topic/libraries/support-library/packages.html)

[Zoftino: Firebase Realtime Database Example"](http://www.zoftino.com/firebase-realtime-database-android-example)

## Instructions
Start by registering as the app's user either by email and password or with an existing Gmail account. After a successful login choose between searching for a ride and accepting the one that suits you or post your own ride and wait for someone to accept it. After accepting someone's ride/having your ride accepted by another user get in touch with the other user through app's chat functionality - sending direct messages to arrange details. When creating a ride, editing your own or searching for an existing one, feel free to use the geolocating functionality (small location icons in FROM and TO fields) which will automatically fetch your location and turn it into address. Enjoy!
