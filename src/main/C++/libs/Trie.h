//
// Created by jiahong on 22/01/17.
//

#ifndef TRIE_TRIE_H
#define TRIE_TRIE_H

#include <memory>
#include "Node.h"

namespace ds {

template<class T>
class Trie {
public:
    static constexpr int ASCII = 128;
    typedef size_t size_type;

    Trie(size_type type) : char_set(type), root(nullptr, ASCII) {}

    Trie() : Trie(Trie::ASCII) {}


    void add(std::string, T *);

    bool has(const std::string);

    T *get(const std::string);


private:
    size_type char_set;
    ds::Node<T> root;
};

template<class T>
void
Trie<T>::add(std::string key, T *val) {
    ds::Node<T> *node = &root;
    for (std::string::size_type i = 0; i < key.size(); i++) {
        // if node doesn't branch out from this string, add a child
        if (!node->has_child((size_t)key[i]))
            node->add_child((i == key.size() - 1) ? val : nullptr, (size_t) key[i]);
        // special case: when this string is a prefix of another existing string already
        else if (node->has_child((size_t)key[i]) && i == key.size()-1)
            node->get_child((size_t)key[i])->set_data(val);
        node = node->get_child((size_t)key[i]);
    }
}

template<class T>
bool
Trie<T>::has(const std::string key) {
    return Trie<T>::get(key) != nullptr;
}


template<class T>
T*
Trie<T>::get(const std::string key) {
    ds::Node<T> *node = &root;
    for (char c : key) {
        if (!node->has_child((size_t)c))
            return nullptr;
        node = node->get_child((size_t)c);
    }
    return node->get_data();
}


}
#endif //TRIE_TRIE_H
