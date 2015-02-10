#!/bin/bash

echo ' -- building with maven'
mvn clean compile assembly:single
echo ' -- generating javadocs'
mvn javadoc:javadoc

echo ' -- moving javadocs'
cd target/reference
cp -rv apidocs/* .
rm -rf apidocs
cd ../..

echo ' -- copying jar to library folder'
mv ./target/*.jar ./target/SurfaceMapperGui.jar
mkdir ./target/library
cp -v ./target/*.jar ./target/library/

echo ' -- moving everything to target/surface-mapper-gui'
mkdir target/SurfaceMapperGui
cp -r src target/SurfaceMapperGui/
cp -r reference target/SurfaceMapperGui/
cp -r examples target/SurfaceMapperGui/
cp -r target/library target/SurfaceMapperGui/

echo ' -- generating zip file '
cd target
zip -r SurfaceMapperGui.zip SurfaceMapperGui 

