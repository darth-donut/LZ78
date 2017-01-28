[![Build Status](https://travis-ci.org/jfong361/LZ78.svg?branch=master)](https://travis-ci.org/jfong361/LZ78)
# LZ78
LZ78 ASCII file compression

This implementation of LZ78 emulates the [LZ78](https://en.wikipedia.org/wiki/LZ77_and_LZ78) algorithm
(a variant of [gzip's algorithm](http://www.gzip.org/algorithm.txt)) **__and is for educational purposes only.__**
Documentation are written throughout the methods, feel free to read them.

Clearly, this repository wasn't created for actual usage but as a for-fun project. **__DO NOT__**
use this program on any files that are important, as it hasn't been tested enough yet. Do so at your own risk.

# Implementation
Simply put, the input file is first traslated into token *_(c, k)_* pairs, where _c_ represents the 'new' character
unknown at that time, and k the index to look up before outputting c. This allows repetitive phrases to be encoded
efficiently as you only need the index _k_ to look up the rest of the characters recursively. 

Stopping here already allows efficient compression on non-trivial sized files, but encoding _k_ in a minimum of
8 or 16 bits (1 or 2 char sized) is sometimes wasteful because _k_ might be < 2^8 or 2^16.
This calls for encoding in binary.
This implies that the files can be further compressed using log2(max(_k_)) bits for each _k_.

Since Java, doesn't support bit writing, a wrapper class acts as a buffer that keeps track of the current bit and
periodically flushes the data into our binary file when enough bytes are accumulated. Similarly, the decoder wrapper
class reads in bits to decode them back into our factors *_(c, k)_*. The tokens are then re-translated back into
sentences.

The older implementation of this project(v1.0.0) uses byte as the smallest unit. The code for it still exists
within the repository for browsing.

# Requirements
If you're interested in running the program, you will require [Java](https://www.oracle.com/java/index.html)'s 
JDK version 8.0 or later.

# Installation
Check the [releases](https://github.com/jfong361/LZ78/releases) page for binary downloads (Jar files).

If you're interested in compiling from sources, there's a [gradle](https://gradle.org/gradle-download/) build file 
that automates that too.

That is,
* download gradle
* from the root directory, run ` gradle build `


# Usage
For both compiled and downloaded binaries, the usage are as follows:

* Compressing:
```aidl
java -jar LZ78-X.X.X.jar -c inputfile.[extension]
```
which produces a `inputfile.[extension].lz78` file

* Decompressing:
```aidl
java -jar LZ78-X.X.X.jar -x inputfile.[extension].lz78
```
which produces a `inputfile.[extension]` file

 
### Improvements:
  - [ ] Deal with outliers (some files have special characters)
  - [ ] Using a compressed Trie (instead of a HashMap)
  - [ ] Translating to C++ (a C++ Trie is already implemented, waiting for the compression program to be written)
 
