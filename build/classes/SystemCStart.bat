%ECHO SYSTEM C
%ECHO Starting Maintenance Console
START "CONSOLE - Maintenance" /NORMAL java MaintenanceConsole %1

PAUSE

%ECHO BASE SYSTEM
%ECHO PAUSE
%ECHO Starting ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java TemperatureController %1
%ECHO Starting Humidity Controller Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java HumidityController %1
%ECHO Starting Temperature Sensor Console
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java HumiditySensor %1


%ECHO SYSTEM A
%ECHO Starting Security System
%ECHO PAUSE
%ECHO Security Console
START "MUSEUM SECURITY SYSTEM CONSOLE" /NORMAL java SecurityConsole %1
%ECHO Starting Security Controller Console
START "SECURITY CONTROLLER CONSOLE" /MIN /NORMAL java SecurityController %1
%ECHO Starting Security Sensor Console
START "SECURITY SENSOR CONSOLE" /MIN /NORMAL java SecurityAlertSensor %1



%ECHO SYSTEM B
%ECHO Fire Management System 
%ECHO PAUSE
%ECHO Starting Smoke Detector
START "SMOKE DETECTOR" /MIN /NORMAL java SmokeDetector %1
%ECHO Starting FireAlarm Controller
START "FIREALARM CONTROLLER" /MIN /NORMAL java FireAlarmController %1
%ECHO Starting Sprinkler
START "SPRINKLER" /MIN /NORMAL java Sprinkler %1
%ECHO Starting Sprinkler Controller
START "SPRINKLER CONTROLLER" /MIN /NORMAL java SprinklerController %1


