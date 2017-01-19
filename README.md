# LZ78
LZ78 ASCII file compression

Emulates the [LZ78](https://en.wikipedia.org/wiki/LZ77_and_LZ78) algorithm (a variant of [Gzip's algorithm](http://www.gzip.org/algorithm.txt)).

### Because this is just an emulation, there are much more improvements that can be made, for example :##
  - since we assume ascii, the characters can be encoded in 7 bits (instead of 8, as this Java implementation was)
  - substituting integers with pointers in dictionary entries
  - file I/O overhead from raw file -> intermediate dictionary (LZ78 output) -> bytefile
 
 
##### Todos:
  - [x] ~~Command-line argument (compressing, decompressing)~~
  - [ ] Stress tests
  - [ ] Deal with outliers (some files have special characters)
  - [ ] Using a compressed Trie (instead of a HashMap)
  - [ ] Translating to C++
 
