#include <iostream>
#include <fstream>
#include <utility>

#include "Trie.h"


using std::cout; using std::endl;
using std::ifstream; using std::string;

template<class T1, class T2>
class Tuple {
public :
    T1 first;
    T2 second;

    Tuple(T1 first, T2 second) : first(first), second(second) {}

};

int main(int , char *argv[]) {
    Trie<Tuple<char, int>> trie;
    ifstream in(argv[1]);
    string buffer;

    int ch;
    int last_index = 0;    // index of last seen string
    int insert_index = last_index;

    while ((ch = in.get()) != -1) {
        buffer.push_back((char)ch);
        // if we can find this character, record the last index
        Tuple<char, int>* res;
        if ((res = trie.get(buffer)) != nullptr) {
            last_index = res->second;
        } else {
            cout << (char)ch << last_index << endl;
            res = new Tuple<char, int>((char)ch, ++insert_index);
            trie.add(buffer, res);
            buffer.clear();
            last_index = 0;
        }
    }

    if (buffer.size() > 0) {
        // we need to flush the remaining items out of buffer
        // 2 cases, buffer is len(1), or buffer len(>1)
        if ((int)buffer.size() == 1) {
            cout << buffer[0] << "0" << endl;
        } else {
            cout << buffer[buffer.size()-1];
            buffer = buffer.substr(0, buffer.size()-1);
            Tuple<char, int>* res = trie.get(buffer);
            assert(res != nullptr);     // it has to exists, by invariant
            cout << res->second << endl;
        }
    }
    //todo: free Tuples in trie*/
    return 0;
}