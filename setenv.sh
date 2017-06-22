#!/bin/bash

# expects C:/Dev folder containg java and maven
# or on mac or linux expects ~/Dev folder
# you can symlink your JDK version to the ~/Dev/jdk folder and 
# symlink your Maven version to the ~/Dev/maven folder

echo $(uname)
if [ "$(uname)" == "CYGWIN_NT-10.0" ]; then
	PREFIX="/cygdrive/c"
else
	PREFIX=~
fi
echo "PREFIX: " $PREFIX
export JAVA_HOME=${PREFIX}/Dev/jdk
export MAVEN_HOME=${PREFIX}/Dev/maven
export PATH=$JAVA_HOME/bin:$PATH:$MAVEN_HOME/bin

echo JAVA_HOME=$JAVA_HOME

