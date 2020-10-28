#!/bin/bash

if [ ! $# == 1 ]; then
  echo "Usage: $0 properties/resource"
  exit
fi

if [[ "$1" == *properties ]]; then
    java -cp target/db-checko.jar:drivers/* cz.raptor22fa.dbchecko.cmd.DbCheckoApp check -p $1
fi
