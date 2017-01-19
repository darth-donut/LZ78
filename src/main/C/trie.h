#ifndef __TRIE_H
#define __TRIE_H

#include "string_data.h"

typedef struct node_t*      Node;
typedef struct data_t*      Data;
typedef struct trie_t*      Trie;


struct node_t {
    Data data;
    Node *children;
};

struct data_t {
    String string;
    int count;
};

struct trie_t {
    Node root;
    int word_count;
    int character_size;
};

/**
 * Creates a trie structure and returns a pointer to it
 */
Trie new_trie(int character_size);

/**
 * Inserts a string into the trie
 */
void trie_insert(Trie t, String string);

/**
 * Searches for an occurance and returns the number of occurance in the trie, 0 if not found
 */
int trie_search(Trie t, String key);

/**
 * Free the trie altogether
 */
void trie_free(Trie t);

/**
 * Assumes that the key is existing, and returns the key to user, NULL if can't find
 */
String trie_get(Trie t, String key);
#endif
