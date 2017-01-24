# MineSweeper
Minesweeper application assignment.

I made this application during an assignment in a course I took in college.
It's not finished (the UI mainly, and some documentation), but it uses some complex components.

Its basically a regular MineSweeper game with 3 modes: Easy, Normal and Hard.

Whenever you play a game, you need to watch for your device's orientation, because if you tilt it too much, mines will be added to your
game board every second (by a service that uses the accelerometer sensor).

If you wish to flag a tile, you need to click the Mine button in the bottom of the game activity.

When you finish a game (either by winning or losing), an animation will play.

When you win, and you've actually made it to the top 10 list (for the specific level), the system will record the time it took you,
your location (using location services), and the name you'll enter, and save it in its Firebase database.

You can watch the top 10 scores in the highscores activity, in a regular Table mode and in a map mode.

When you use the Table mode, the activity pulles the info from the Firebase database, and when you use the Map mode,
you will see every score as a marker, and you will also see your current location + the direction your device is pointing,
this is achieved by using the location services and the accelerometer + magnetic field sensors.
