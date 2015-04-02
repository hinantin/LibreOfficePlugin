#!/bin/bash

startmessage="
         ******************************************************
         *                    Project Squoia,                 *
         *        Institute of Computational Linguistics      *
         *             at the University of Zurich            *
         *    LibreOffice Spellchecking Plugin v0.3.1         *
         *                    created by                      *
         *          Richard Alexander Castro Mamani           *
         *        Copyright (c) 2015 by Project Squoia.       *
         *                All Rights Reserved.                *
         ******************************************************
"

usage="$(basename "$0") [-h] [-x -c -j -o] -- program to generate the spellchecking plugin for LibreOffice

where:
    -x  clean generated files for jar
    -c  compile 
    -j  generate jar
    -o  generate oxt
    "

# sed -i -e 's@/\*.*\*/@@' *.java

while getopts ':hxcjon:' option; do
  case "$option" in
    h) echo "$usage"
       exit
       ;;
    x)

       ant clean

       ;;
    c)

       ant compile

       ;;
    j)

       ant jar

       ;;
    o)

       rm -f qhichwa.oxt
       7z a qhichwa.oxt lib
       7z a qhichwa.oxt resources
       7z a qhichwa.oxt META-INF
       7z a qhichwa.oxt ./jar/qhichwa.jar
       7z a qhichwa.oxt description.xml
       7z a qhichwa.oxt description.txt

       ;;
    :) printf "missing argument for -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1
       ;;
   \?) printf "illegal option: -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1
       ;;
  esac
done
shift $((OPTIND - 1))

