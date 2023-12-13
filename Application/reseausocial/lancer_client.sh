#!/bin/bash
# prendre les 2 arguments passés en paramètre et les mettre dans le -Dexec.args
# conditionnellle si pas d'arguments passés en paramètre
# on verra pour demander un input à l'utilisateur si il n'a rien mis en paramètres d'entrée
if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
    mvn clean install exec:java@run-client -Dexec.args="localhost date" -DSkipTests
  else
    echo "Arguments supplied"
    mvn clean install exec:java@run-client -Dexec.args="$1 $2" -DSkipTests
fi
```

