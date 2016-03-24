echo Starting Security System

echo Starting Security Sensor Console
java SecurityAlertSensor &

echo Starting Security Controller Console
java SecurityController &

echo Starting Security Console
java SecurityConsole