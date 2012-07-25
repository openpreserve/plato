#!/bin/bash 
set -x

#
# gimpMigrate <fuScript> [<parameters of fuScript>] <inFile> <outFile>
# 

# we have to deal with an unknown number of parameters of the fu-script

SED=/bin/sed
function ltrim() { echo "$1" | $SED -e "s/^ *//"; }
function rtrim() { echo "$1" | $SED -e "s/ *$//"; }


nrOfScriptParams=$#
nrOfScriptParams=$nrOfScriptParams-3

fuScriptParams=""
i=0

echo "out:$outFile"
echo "in:$inFile"

# the first parameter is the fu script
fuScript=$1

for param in $*; do
  
  if (( $i <= $nrOfScriptParams && $i != 0 ))
  then
   fuScriptParams="$fuScriptParams $param"
  fi

  if (( $i == ($#-1) ))
  then
     outputFile=$param
  fi

  if (( $i == ($#-2) ))
  then
     inputFile=$param
  fi
  
  ((i++))

done;

# to pass the string to the fu script we have to remove leading ending white spaces
fuScriptParams="$(ltrim "$fuScriptParams")"
fuScriptParams="$(rtrim "$fuScriptParams")"

echo "script: $fuScript"
echo "Parameters for the fuScript: $fuScriptParams"
echo "inFile: $inputFile"
echo "outFile: $outputFile"

#--system-gimprc gimprc
gimp --verbose -i -b "($fuScript \"$inputFile\" \"$outputFile\" $fuScriptParams)" -b '(gimp-quit 0)'

# (define (mPNGtoTIFF infile outfile compression) (let* ((image (car (file-png-load 1 infile infile))) (drawable (car (gimp-image-active-drawable image))))(if (gimp-drawable-has-alpha drawable)(set! drawable (car (gimp-image-flatten image))))(file-tiff-save 1 image drawable outfile outfile compression))) (mPNGtoTIFF '\"$inputFile\"' '\"$outputFile\"' 0)




