#!/bin/bash  

### %ECHO OFF   ## I don't think the equivalent is needed

## START "EVENT MANAGER REGISTRY" /MIN /NORMAL rmiregistry
echo "Starting EVENT MANAGER REGISTRY"
rmiregistry &

## START "EVENT MANAGER" /MIN /NORMAL java MessageManager
echo "Starting Event Manager"
java MessageManager