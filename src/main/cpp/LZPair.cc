//
// Created by jiahong on 22/01/17.
//

#include "LZPair.h"


LZPair::LZPair(std::istream &in, int bitsize) : bitsize(bitsize) {
    ch = (charset)in.get();    // assume 1 byte max
    std::string buffer;
    getline(in, buffer);
    index = (size_type)std::stoll(buffer, nullptr, 0);
}

int
LZPair::get_bitsize() {
    return sizeof(charset) + bitsize;
}
