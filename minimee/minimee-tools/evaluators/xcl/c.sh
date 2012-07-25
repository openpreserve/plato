#!/bin/bash

i=1;
for file in /home/kulovits/dev/xcl/files/*; do
    
  convert $file /home/kulovits/dev/xcl/files/$i.png
  convert /home/kulovits/dev/xcl/files/$i.png /home/kulovits/dev/xcl/files/$i.tif

  cd /home/kulovits/dev/xcl/extractor-e
  ./extractor $file

  mv xcdlOutput.xcdl /home/kulovits/dev/xcl/files/$i-jpg.xcdl

  ./extractor /home/kulovits/dev/xcl/files/$i.png
  mv xcdlOutput.xcdl /home/kulovits/dev/xcl/files/$i-png.xcdl

  ./extractor /home/kulovits/dev/xcl/files/$i.tif
  mv xcdlOutput.xcdl /home/kulovits/dev/xcl/files/$i-tif.xcdl

  ((i++))

done
