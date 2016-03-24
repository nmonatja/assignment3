echo Starting Security and Fire System

echo Starting Security Controller Console
java SecurityController &

echo Starting Smoke Detector
java SmokeDetector &

echo Starting Security Sensor Console
java SecurityAlertSensor &

echo Starting Sprinkler Controller
java SprinklerController &

echo Starting Sprinkler
java Sprinkler &

echo Security Console
java SecurityConsole 


