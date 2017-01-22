# LZ78
LZ78 ASCII file compression

Emulates the [LZ78](https://en.wikipedia.org/wiki/LZ77_and_LZ78) algorithm (a variant of [Gzip's algorithm](http://www.gzip.org/algorithm.txt))

This repository is for educational purposes only. It performs worse than most compression tools due to the rounding of output bytes [see below](#improvements)

## Improvements
  - since we assume ascii, the characters can be encoded in 7 bits (instead of 8, as this Java implementation was)
  - writing to compressed file in bits instead of bytes (rounded up to nearest byte as of this moment, __extremely__ inefficient)
  - file I/O overhead from raw file -> intermediate dictionary (LZ78 output) -> bytefile
 
 
##### Todos:
  - [x] ~~Command-line argument (compressing, decompressing)~~
  - [ ] Stress tests
  - [ ] Deal with outliers (some files have special characters)
  - [ ] Using a compressed Trie (instead of a HashMap)
  - [ ] Translating to C++
 
