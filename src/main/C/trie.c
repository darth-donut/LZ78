#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "trie.h"
static void insert_recur(int character_size, Node root, String string, int n);
static Data new_data(char *buffer, void *info, int count);
static Node *initialize_children(int n);
static Node new_node(void *data);
static char *tostring(char ch);
static int search_recur(Node root, String key, int n);
static void free_recur(Node root, int character_size);
static String get_recur(Node root, String key, int n);
static int translate(char ch);


/**
 * Creates a trie structure and returns a pointer to it
 */
Trie
new_trie(int character_size) {

    Trie t = (Trie)malloc(sizeof(Trie));
    assert(t && "Failed to malloc a trie");

    // the root's initialization
    //      First data(root) is just an empty string
    t->root = new_node(new_data("\0", NULL, 0));

    // initialize the total length of character set
    t->character_size = character_size;

    // trie has no word initially
    t->word_count = 0;

    t->root->children = NULL;
    return t;
}

/**
 * Inserts a string into the trie
 */
void
trie_insert(Trie t, String string) {
    insert_recur(t->character_size, t->root, string, 0);
}

/**
 * Searches for an occurance and returns the number of occurance in the trie, 0 if not found
 */
int
trie_search(Trie t, String key) {
    assert(key->string);
    return search_recur(t->root, key, 0);
}

/**
 * Assumes that the key is existing, and returns the key to user, NULL if can't find
 */
String
trie_get(Trie t, String key) {
    return get_recur(t->root, key, 0);
}

/**
 * Free the trie altogether
 */
void
trie_free(Trie t) {
    free_recur(t->root, t->character_size);
}

static void
free_recur(Node root, int character_size) {
    if (root) {
        if (root->children) {
            for (int i = 0; i < character_size; i++)
                free_recur(root->children[i], character_size);
            free(root->children);
        }
        free(root->data->string->string);
        free(root->data->string);
        free(root->data);
        free(root);
    }
}

static int
search_recur(Node root, String key, int n) {
    if (key->string[n] == '\0')
        return root->data->count;
    int index = translate(key->string[n]);
    if (!root->children || !root->children[index]) {
        return 0;
    } else {
        return search_recur(root->children[index], key, n+1);
    }
}

static String
get_recur(Node root, String key, int n) {
    if (key->string[n] == '\0') {
        return root->data->string;
    }
    int index = translate(key->string[n]);
    if (!root->children || !root->children[index]) {
        return NULL;
    } else {
        return get_recur(root->children[index], key, n+1);
    }
}

static char 
*tostring(char ch) {
    char *buffer = (char *)malloc(sizeof(char)*2);
    assert(buffer);
    buffer[0] = ch;
    buffer[1] = '\0';
    return buffer;
}

static void
insert_recur(int character_size, Node root, String string, int n) {
    int index = translate(string->string[n]);
    // base case (came from first non-base case if clause),
    // an exact match was found
    if (string->string[n] == '\0') {
        root->data->count++;
        return;
    }
    if (root->children && root->children[index]) {
        insert_recur(character_size, root->children[index], string, n+1);
    } else if (string->string[n+1] == '\0') {
        /* base case (normal base case), current node has no such child suffix;
         * we need to make one, it came here because ther was no match and
         * the search string is ending */
        assert(root && root->data);
        // make a leaf node, 1 occurance
        Data d = new_data(tostring(string->string[n]), string->info, 1);
        if (!root->children)
            root->children = initialize_children(character_size);
        root->children[index] = new_node(d);
        return;
    } else {
        if (!root->children)
            root->children = initialize_children(character_size);
        Data d = new_data(tostring(string->string[n]), string->info, 0);
        root->children[index] = new_node(d);
        insert_recur(character_size, root->children[index], string, n+1);
    }
}

static Node
*initialize_children(int n) {
    Node *array = (Node *)calloc(n, sizeof(Node));
    assert(array && "Creation of child array nodes failed");
    return array;
}

static Node
new_node(void *data) {
    Node n = (Node)malloc(sizeof(Node));
    assert(n && "Failed to create a new node");
    n->data = data;
    n->children = NULL;
    return n;
}

static Data
new_data(char *buffer, void *data, int count) {
    // Make a new data, and copy over the string into it
    Data d      = (Data)malloc(sizeof(Data));
    d->string   = new_string(buffer, data);
    d->count    = count;
    return d;
}

static int
translate(char ch) {
    if (ch == '\n') {
        return 95; // the 96th printable character(ascii 10, '\n')
    }
    return ((int)ch - (int)' ');
}

/*
 * int
 * read_word(char **buffer, int *size) {
 *     int i = 0;
 *     int ch;
 *     for(;(ch = getchar()) != EOF; i++) {
 *         if (ch == ' ' || ch == '\n') {
 *             break;
 *         }
 *         if (i >= *size) {
 *             *size *= 2;
 *             *buffer = realloc(*buffer, sizeof(char)*(*size));
 *             assert(*buffer);
 *         }
 *         (*buffer)[i] = ch;
 *     }
 *     if (ch == EOF)
 *         return EOF;
 *     (*buffer)[i++] = '\0';
 *     return i;
 * }
 * 
 * int
 * main() {
 *     Trie trie = new_trie(96);
 *     int initsize = 10;
 *     char *buffer = malloc(sizeof(char)*initsize);
 *     while (read_word(&buffer, &initsize) != EOF) {
 *         String str = new_string(buffer, NULL);
 *         if (trie_search(trie, str)) {
 *             continue;
 *         }
 *         trie_insert(trie, str);
 *     }
 *     return 0;
 * }
 */
