#!/bin/bash

FILE_NAME=MockMock.jar

mvn clean package --file ../MockMock/pom.xml
cp ../MockMock/target/MockMock-1.4.0.one-jar.jar ./$FILE_NAME
docker build -t mockmock . --build-arg filename=$FILE_NAME

read -n 1 -s -r -p "Press any key to continue"