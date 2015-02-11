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
mv ./target/*.jar ./target/SketchMapper.jar
mkdir ./target/library
cp -v ./target/*.jar ./target/library/

echo ' -- moving everything to target/surface-mapper-gui'
mkdir target/SketchMapper
cp -r src target/SketchMapper/
cp -r target/reference target/SketchMapper/
cp -r examples target/SketchMapper/
cp -r target/library target/SketchMapper/
cp library.properties target/SketchMapper/

echo ' -- generating zip file '
cd target
zip -r SketchMapper.zip SketchMapper 

