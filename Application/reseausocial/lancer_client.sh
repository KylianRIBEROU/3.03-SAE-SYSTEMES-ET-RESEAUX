#!/bin/bash

mvn clean install exec:java@run-client -Dexec.args="localhost date" -DSkipTests

