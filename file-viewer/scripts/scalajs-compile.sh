#!/bin/bash

cd ../ && sbt fastOptJS
cp target/scala-2.12/file-viewer-fastopt.js view/src/scala/
