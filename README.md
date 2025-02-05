# Scoreboard

This is a scoreboard program for making rankings between teams and players for various games, featuring custom localization files, a gui (Swing), cli mode

Run `mvn package` to create the jar file for this project

Then you can run the app, it has 2 optional command line arguments, the localization file (defaults to en.json) and the switch for cli mode, any of the following are valid combinations to run the app

```
java -cp target/scoreboard-1.0-SNAPSHOT.jar com.filip.scoreboard.App
java -cp target/scoreboard-1.0-SNAPSHOT.jar com.filip.scoreboard.App en.json
java -cp target/scoreboard-1.0-SNAPSHOT.jar com.filip.scoreboard.App en.json --cli
```
