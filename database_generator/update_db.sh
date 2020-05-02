#!/bin/sh

python3 abc2db.py && 
rm -f ../Irish_Whistle/app/src/main/res/raw/* && 
cp ./output/* ../Irish_Whistle/app/src/main/res/raw/