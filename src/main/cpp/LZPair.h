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
    typedef wchar_t charset;
    // constructors
    LZPair(std::istream&, int bitsize);
    // interface
    size_t dump(FILE*) const ;

private:
    charset ch;
    size_type index;
    int bitsize;
};

LZPair::LZPair(std::istream &in, int bitsize) : bitsize(bitsize) {
    ch = in.get();
    std::string buffer;
    getline(in, buffer);
    index = (size_type)std::stoll(buffer, nullptr, 0);
}

size_t
LZPair::dump(FILE* fp) const {
}


#endif //LZ78_LZPAIR_H
