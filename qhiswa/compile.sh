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
       rm -f *.oxt

       ;;
    c)

       ant compile

       ;;
    j)

       ant jar

       ;;
    o)

       chmod +x build.properties
       . build.properties
       name=qusqu_qhiswa-$buildversion.oxt
       rm -f *.oxt
       zip -r $name lib
       zip -r $name resources
       zip -r $name META-INF
       zip -j $name jar/qhichwa.jar
       sed -e "s/version value=\".*\"/version value=\"$buildversion\"/" description.base.xml > description.xml
       zip $name description.xml
       rm -f description.xml
       zip $name description.txt

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

