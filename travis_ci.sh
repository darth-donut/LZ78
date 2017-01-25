#!/bin/bash

jar="build/libs/lz78-2.0.SNAPSHOT.jar"



mkdir testdir
cd testdir
echo "Starting tests"
for f in $(ls ../src/main/resources/testfiles); do
    echo "testing on file ${f}"
    # compress file
    java -jar ../$jar -c ../src/main/resources/testfiles/$f
    # uncompress
    java -jar ../$jar -x "$f".lz
    # check
    # TODO: remove $f
    if [[ $(diff -w $f ../src/main/resources/testfiles/$f) ]]; then
        exit -1
    fi
    echo "test ${f} successful"
done

cd ..
rm -rf testdir

exit 0
