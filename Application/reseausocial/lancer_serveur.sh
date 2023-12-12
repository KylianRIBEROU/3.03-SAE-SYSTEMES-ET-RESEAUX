#!/bin/bash
mvn clean install exec:java@run-server -DSkipTests
