//
// Created by jiahong on 22/01/17.
//

#ifndef LZ78_BIT_ENCODE_H
#define LZ78_BIT_ENCODE_H

#include <fstream>
#include <vector>
#include "Token.h"
#include "Byte.h"

#define BYTE 8

class Bit_encode {
public:
    typedef unsigned  uint;
    static constexpr int MAX_BUFF = (int)5e5;
    /* Constructors */
    Bit_encode(std::ofstream& out, uint char_bits, uint index_bits, uint no_tokens, int max_buff_size=MAX_BUFF);

    /* Interface */

    /**
     * @brief Encodes the token into ofstream as provided in the constructor (in bits)
     * @param Token - token that should be encoded in bits
     */
    void encode(const Token&);

    /**
     * @brief Closes and flushes all data into ofstream
     *
     * Users do not need to manually close ofstream after calling this method
     */
    void close();


private:
    // out stream to output our tokens as bits
    std::ofstream& out;
    // number of bits to encode characters
    uint char_bits;
    // number of bits to encode indices
    uint index_bits;
    // number of tokens
    uint no_tokens;
    // byte buffer
    std::vector<byte_t> buffer;
    // byte accumulator, capable of holding at least 0-255 (1 byte)
    byte_t accumulator;
    // "pointer" that guides the insertion of the next bit
    int next;
    // maximum size (number of bytes to hold) before flushing buffer
    int max_buff_size;


    // private interfaces
    void check_buffer();
    void flush_buff();
};


#endif //LZ78_BIT_ENCODE_H
