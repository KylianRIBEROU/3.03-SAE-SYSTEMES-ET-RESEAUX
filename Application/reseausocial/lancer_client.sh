#!/bin/bash
# prendre les 2 arguments passés en paramètre et les mettre dans le -Dexec.args
# conditionnellle si pas d'arguments passés en paramètre
# on verra pour demander un input à l'utilisateur si il n'a rien mis en paramètres d'entrée
if [ $# -eq 0 ]
  then
    echo "Entrez le nom du serveur ou son IP pour vous connecter."
    echo "Nom du serveur"
    read -p "> " arg1
    mvn clean install exec:java@run-client -Dexec.args="$arg1" -DskipTests=true
  else
    mvn clean install exec:java@run-client -Dexec.args="$1" -DskipTests=true
fi
