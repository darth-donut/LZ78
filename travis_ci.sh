#!/bin/bash

jar="build/libs/lz78-2.0.SNAPSHOT.jar"


function clean {
    cd ..
    rm -rf testdir
}


mkdir testdir
cd testdir
echo "Starting tests"
for f in $(ls ../src/main/resources/testfiles/); do
    echo "testing on file ${f}"
    # compress file
    java -jar ../$jar -c ../src/main/resources/testfiles/$f
    # uncompress
    java -jar ../$jar -x "$f".lz78
    # check
    out=$(diff $f ../src/main/resources/testfiles/$f) 
    if [[ "${out}" ]]; then
        echo "test ${f} failed with:"
        echo $out
        clean
        exit -1
    fi
    echo "test ${f} successful"
done

clean

exit 0
