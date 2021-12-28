@echo off
title -= Balance Manager Java app gradle clean build =-
echo Clean, then build, then refresh dependencies. Skip all tests ...
gradlew clean && gradlew cleanEclipse build eclipse --refresh-dependencies -x test -x iT && echo Finished cleaning and building the project