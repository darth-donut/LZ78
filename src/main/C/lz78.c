#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <stdbool.h>

#include "lz78.h"
#include "string_data.h"
#include "trie.h"


#define MIN_ASCII 32
#define MAX_ASCII 126
#define PRINTABLE_ASCII 96 // incl newline
#define LASTCHAR(string) (((char *)string)[strlen((char*)string)-1])
#define OUTPUT_STREAM stdout

bool non_ascii(int);
StringData new_string_data(int index, size_t buffer_size);
String extend_buffer(String buffer, char ch);
char *tostring(char ch);
void print(char ch, int index);
void truncate(char *string, int by);


int
main(int argc, char *argv[]) {
    FILE *fp = (argc > 1) ? fopen(argv[1] ,"r") : stdin;
    assert(fp);
    int ch;

    bool extending_buffer = false;
    int prefix_index = 0;
    int running_index = 0;

    Trie trie = new_trie(PRINTABLE_ASCII);
    StringData starting_index = new_string_data(running_index, 2);
    String buffer = new_string("", starting_index);


    while ((ch = getc(fp)) != EOF) {
        if (non_ascii(ch)) continue;
        if (extending_buffer) {
            extend_buffer(buffer, ch);
        } else {
            StringData sd = new_string_data(++running_index, 2);
            buffer = new_string(tostring(ch), sd);
        }
        if (trie_search(trie, buffer)) {
            extending_buffer = true;
            prefix_index = ((StringData)trie_get(trie, buffer)->info)->index;
        } else {
            extending_buffer = false;
            print(ch, prefix_index);
            trie_insert(trie, buffer);
            prefix_index = 0;
        }
    }

    if (extending_buffer) {
        if (strlen(buffer->string) == 1) {
            print(LASTCHAR(buffer->string), 0);
        } else {
            char out_char = LASTCHAR(buffer->string);
            truncate(buffer->string, 1);
            prefix_index = ((StringData)trie_get(trie, buffer)->info)->index;
            print(out_char, prefix_index);
        }
    }
    trie_free(trie);
    return 0;

}


void
truncate(char *string, int by) {
    size_t string_len = strlen(string);
    int index = string_len - by;
    string[index] = '\0';
}


void
print(char ch, int index) {
    fprintf(OUTPUT_STREAM, "%c%d\n", ch, index);
}

char
*tostring(char ch) {
    char *buffer = malloc(sizeof(char)*2);
    assert(buffer);
    buffer[0] = ch;
    buffer[1] = '\0';
    return buffer;
}


bool
non_ascii(int ch) {
    if (ch == '\n') return false;
    return !(ch >= MIN_ASCII && ch <= MAX_ASCII);
}

StringData
new_string_data(int index, size_t buffer_size) {
    StringData ind = (StringData)malloc(sizeof(*ind));
    assert(ind && "Failed to malloc index");
    ind->index = index;
    ind->string_buffer_size = buffer_size;
    return ind;
}

String
extend_buffer(String buffer, char ch) {
    size_t buffer_length = strlen(buffer->string);
    if (buffer_length+2 >= ((StringData)buffer->info)->string_buffer_size) {
        ((StringData)buffer->info)->string_buffer_size *= 2;
        buffer->string = (char *)realloc(buffer->string, sizeof(char)*(((StringData)buffer->info)->string_buffer_size));
        assert(buffer->string);
    }
    buffer->string[buffer_length]   = ch;
    buffer->string[buffer_length+1] = '\0';
    return buffer;
}
