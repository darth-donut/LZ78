//
// Created by jiahong on 22/01/17.
//

#ifndef TRIE_NODE_H
#define TRIE_NODE_H
#include <memory>
#include <iostream>

namespace ds {

template<class T>
class Node {
public:
    Node(T *data, size_t child_count)
            : data(data),
              children(nullptr),
              parent(nullptr),
              max_child_count(child_count),
              current_child_count(0),
              self_index(0) {}

    Node() : Node(nullptr, 0) {}

    Node(const Node&);

    ~Node() { destruct(); }

    Node& operator=(const Node&);

    void add_child(T* data, size_t index);
    // we preserve encapsulation here,
    // no one can touch parent node (we're only giving the address)
    const Node* get_parent() const { return parent;}
    bool has_child(size_t index) const ;
    Node* get_child(size_t index) const {
        if (children)
            return children[index];
        return nullptr;
    }
    T* get_data() { return data; }
    void set_data(T *data) {
        delete this->data;
        this->data = data;
    }

private:
    T *data;
    Node **children;
    Node *parent;
    size_t max_child_count;
    size_t current_child_count;
    size_t self_index;
    void destruct();
};


template <class T>
bool
Node<T>::has_child(size_t index) const {
    if (index >= max_child_count)
        throw std::invalid_argument("Attempting to index out of allocated array space");
    return children != nullptr && children[index];
}


template<class T>
Node<T>&
Node<T>::operator=(const Node &other) {
    throw std::runtime_error("Node has no assignment semantic");
}



/**
 * Creates a dangling node
 * this node points to the same data, children array
 * and also has the same max and current children count.
 * However, it is a dangling node because its parent is not aware of this node at all
 * Hence, it is required for the calling interface to manually assign it a parent
 * @tparam T
 * @param other
 */
template<class T>
Node<T>::Node(const Node &other) {
    throw std::runtime_error("Node has no copy constructor semantic");
}


template<class T>
void
Node<T>::destruct() {
    if (children != nullptr) {
        for (size_t i = 0; i < max_child_count; i++)
            delete children[i];
        delete[] children;
    }
    delete data;
}

template<class T>
void
Node<T>::add_child(T *data, size_t index) {
    // if this node is adding a child for the first time
    if (!children) {
        children = new Node*[max_child_count];
        for (size_t i = 0; i < max_child_count; i++)
            children[i] = nullptr;
    }
    // if this node can't add a child with index = index
    if (index >= max_child_count)
        throw std::invalid_argument("Cannot add new child at index greater than allocated array size");

    // we should be clear to add children node now
    children[index] = new Node(data, max_child_count);
    children[index]->parent = this;
    current_child_count++;
    self_index = index;
}


}

#endif //TRIE_NODE_H

