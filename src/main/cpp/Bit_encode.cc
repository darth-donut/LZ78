//
// Created by jiahong on 22/01/17.
//

#include <cmath>
#include "Bit_encode.h"

Bit_encode::Bit_encode(std::ofstream &out, uint char_bits, uint index_bits, uint no_tokens, int max_buff_size)
        : out(out),
          char_bits(char_bits),
          index_bits(index_bits),
          no_tokens(no_tokens),
          accumulator(0),
          next(0),
          max_buff_size(max_buff_size) {

    out.write((const char *)&char_bits, sizeof(uint));
    out.write((const char *)&index_bits, sizeof(uint));
    out.write((const char *)&no_tokens, sizeof(uint));
}

void
Bit_encode::encode(const Token &tok) {
    for (long long i = char_bits-1; i >= 0; i--) {
        if ((tok.get_char() >> i) & 1) {
            accumulator += pow(2, next);
        }
        next++;
        check_buffer();
    }

    for (long long i = index_bits-1; i >= 0; i--) {
        if ((tok.get_index() >> i) & 1) {
            accumulator += pow(2, next);
        }
        next++;
        check_buffer();
    }

}

void
Bit_encode::close() {
    flush_buff();
    out.close();
}

void
Bit_encode::check_buffer() {
    if (next == BYTE && static_cast<long long>(buffer.size()) > max_buff_size-1) {
        flush_buff();
    } else if (next == BYTE) {
        buffer.push_back(accumulator);
        accumulator = 0;
        next = 0;
    }
}

void
Bit_encode::flush_buff() {
    buffer.push_back(accumulator);
    const char *in = static_cast<const char *>(&buffer[0]);
    out.write(in, buffer.size());
    out.clear();
    accumulator = 0;
    next = 0;
}