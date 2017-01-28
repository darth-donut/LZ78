//
// Created by jiahong on 28/01/17.
//

#ifndef LZ78_TOKEN_H
#define LZ78_TOKEN_H


class Token {
public:
    /* Types */
    typedef char charset_t;
    typedef unsigned long long index_t;

    /* Constructors */
    Token(charset_t ch, index_t index)
            : ch(ch), index(index) {}
    Token()
            : Token(charset_t(), index_t()) {}

    /* Interface */
    charset_t get_char() const { return ch; }
    index_t get_index() const { return index; }


private:
    charset_t ch;
    index_t index;
};


#endif //LZ78_TOKEN_H
