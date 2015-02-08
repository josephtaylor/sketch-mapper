#!/bin/sh

java -Djava.library.path=./target/lib/mac/ -Dwidth=$1 -Dheight=$2 -jar ./target/surface-mapper-gui-0.0.1-SNAPSHOT-jar-with-dependencies.jar