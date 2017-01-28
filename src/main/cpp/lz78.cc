//
// Created by jiahong on 22/01/17.
//

#include <fstream>
#include <iostream>
#include <cmath>

#include "Trie.h"
#include "Bit_encode.h"

using ds::Trie;
using std::ofstream;
using std::ifstream;
using std::string;
using std::cout;
using std::endl;
using std::stoull;

void compress(ifstream& in, Bit_encode::uint token_count, Bit_encode::uint char_encoding,
              Bit_encode::uint max_index, string outfile);

int
main(int , char *argv[]) {
    Trie<int> trie;

    typedef char chr_type;
    ifstream in(argv[1]);
    ofstream out(argv[2]);

    string buffer;
    int ch;
    Bit_encode::uint token_count = 0;
    int curr_index = 0,
        last_index = 0;

    while ((ch = in.get()) != EOF) {
        if (ch >= Trie<int>::ASCII) continue;
        buffer.push_back((chr_type)ch);
        int *index;
        if ((index = trie.get(buffer)) != nullptr) {
            last_index = *index;
        } else {
            trie.add(buffer, new int(++curr_index));
            out << (char)ch << last_index << "\n";
            token_count++;
            last_index = 0;
            buffer.clear();
        }
    }

    // flush remaining characters waiting for unknown suffix
    if (buffer.size() > 0) {
        if (buffer.size() == 1) {
            out << (char)ch << "0\n";
        } else {
            buffer.substr(0, buffer.size()-1);
            out << (char)ch << *trie.get(buffer) << "\n";
        }
        token_count++;
    }

    in.close();
    out.close();

    ifstream token_file(argv[2]);
    compress(token_file, token_count, 7, curr_index, "tmp.txt");
    token_file.close();

    return 0;
}


void
compress(ifstream& in,
         Bit_encode::uint token_count,
         Bit_encode::uint char_encoding,
         Bit_encode::uint max_index,
         string outfile) {

    ofstream out(outfile.c_str());
    Bit_encode encoder(out, char_encoding, static_cast<Bit_encode::uint>(log2(max_index+1)), token_count);

    int ch;
    Token::index_t index;
    string buffer;

    while ((ch = in.get()) != EOF) {
        getline(in, buffer);
        index = static_cast<Token::index_t>(stoull(buffer));
        Token tok(static_cast<Token::charset_t >(ch), index);
        encoder.encode(tok);
    }

    encoder.close();
}
