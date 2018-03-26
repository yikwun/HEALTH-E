Welcome!

HEALTH-E is an Android specific application designed to monitor the health of users, aimed towards the elderly. 
In this application, you'll find that a corresponding smartwatch must also be present as sensors such as the heartbeat monitor can only be found on a smartwatch.
Thus, the two devices must also be able to communicate via Bluetooth to be able to correctly track the user's heartbeat.

In the smartphone application, data received from the smartwatch will be displayed graphically, using jjoe64's GraphView, and will detect significant discrepancies. If such a discrepancy is found, such as from 80 bpm to 20 bpm, the phone will automatically dial the emergency contact that is created when the user first downloads the application, if not cancelled within five seconds. 

The application will also send a text message stating the discrepancy that was found, the user's location, time of occurrence, user's age, and name. This will ensure that information is still transferred if the user is unable to speak during the emergency phone call.

This project was completed in collaboration with Ali N. and Vincent Z.
Thank you for visiting the project! I encourage you to visit my other project focused on desiging desktop user interfaces!



How to set up Android smartwatch:

wear connection commands:

Run following commands in AS Terminal

adb start-server
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444
