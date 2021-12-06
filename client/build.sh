#!/bin/bash

FILE_NAME=MailRobot.jar

mvn clean package --file ../MailRobot/pom.xml
cp ../MailRobot/target/MailRobot-1.0-standalone.jar ./$FILE_NAME

java -jar ./$FILE_NAME -c

read -n 1 -s -r -p "Press any key to continue"