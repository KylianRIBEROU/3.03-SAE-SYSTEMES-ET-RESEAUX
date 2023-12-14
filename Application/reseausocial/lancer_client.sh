#!/bin/bash
# prendre les 2 arguments passés en paramètre et les mettre dans le -Dexec.args
# conditionnellle si pas d'arguments passés en paramètre
# on verra pour demander un input à l'utilisateur si il n'a rien mis en paramètres d'entrée
if [ $# -eq 0 ]
  then
    echo "Vous devez entrer 2 arguments. Le Hostname et votre nom d'utilisateur"
    echo "Nom du serveur"
    read -p "> " arg1
    echo "Nom d'utilisateur"
    read -p "> " arg2
    mvn clean install exec:java@run-client -Dexec.args="$arg1 $arg2" -DskipTests=true
  else
    echo "Arguments supplied"
    mvn clean install exec:java@run-client -Dexec.args="$1 $2" -DskipTests=true
fi
