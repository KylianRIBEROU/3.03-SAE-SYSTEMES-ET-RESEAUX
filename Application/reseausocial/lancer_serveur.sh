#!/bin/bash
mvn clean install exec:java@run-server -DskipTests=true -Dspring.profiles.active=dev
