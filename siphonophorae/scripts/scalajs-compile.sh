#!/bin/bash

cd ../ && sbt "project frontendBackend" fastOptJS
cp frontend-backend/target/scala-2.12/frontend-backend-fastopt.js electron-view/src/scala
