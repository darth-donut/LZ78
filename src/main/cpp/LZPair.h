//
// Created by jiahong on 22/01/17.
//

#ifndef LZ78_LZPAIR_H
#define LZ78_LZPAIR_H

#include <iostream>
#include <cstdio>

class LZPair {
public:
    // typedefs
    typedef long long size_type;
    typedef unsigned char charset;
    // constructors
    LZPair(std::istream&, int bitsize);
    int get_bitsize();

private:
    charset ch;
    size_type index;
    int bitsize;
};


#endif //LZ78_LZPAIR_H
