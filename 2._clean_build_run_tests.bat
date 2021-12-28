@echo off
title -= Balance Manager Java app gradle clean build + tests =-
echo Clean, then build, then refresh dependencies, build fatJar ...
gradlew clean && gradlew cleanEclipse build eclipse --refresh-dependencies && echo Finished cleaning and building the project and ran tests && pause