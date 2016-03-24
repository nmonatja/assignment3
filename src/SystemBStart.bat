%ECHO OFF
%ECHO Starting Security and Fire System
PAUSE
%ECHO Security Console
START "MUSEUM SECURITY AND FIRE SYSTEM CONSOLE" /NORMAL java SecurityConsole %1
%ECHO Starting Security Controller Console
START "SECURITY AND FIRE CONTROLLER CONSOLE" /MIN /NORMAL java SecurityController %1
%ECHO Starting Smoke Detector
START "SMOKE DETECTOR" /MIN /NORMAL java SmokeDetector %1
%ECHO Starting Security Sensor Console
START "SECURITY SENSOR CONSOLE" /MIN /NORMAL java SecurityAlertSensor %1
%ECHO Starting Sprinkler Controller
START "SPRINKLER CONTROLLER" /MIN /NORMAL java SprinklerController %1
%ECHO Starting Sprinkler
START "SPRINKLER" /MIN /NORMAL java Sprinkler %1
