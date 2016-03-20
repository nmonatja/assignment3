%ECHO OFF
%ECHO Starting Security System
PAUSE
%ECHO Security Console
START "MUSEUM SECURITY SYSTEM CONSOLE" /NORMAL java SecurityConsole %1
%ECHO Starting Security Controller Console
START "SECURITY CONTROLLER CONSOLE" /MIN /NORMAL java SecurityController %1
%ECHO Starting Security Sensor Console
START "SECURITY SENSOR CONSOLE" /MIN /NORMAL java SecurityAlertSensor %1
