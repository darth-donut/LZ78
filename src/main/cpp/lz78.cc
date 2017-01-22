//
// Created by jiahong on 22/01/17.
//

#include <fstream>
#include <iostream>

#include "Trie.h"

using ds::Trie;
using std::ofstream;
using std::ifstream;
using std::string;
using std::cout;
using std::endl;

int
main(int , char *argv[]) {
    Trie<int> trie;

    typedef char chr_type;
    ifstream in(argv[1]);
    ofstream out(argv[2]);

    string buffer;
    int ch;
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
    }
    return 0;

}
