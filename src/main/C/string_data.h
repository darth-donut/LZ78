#ifndef __STRING_H
#define __STRING_H

typedef struct string_t*    String;

struct string_t {
    char *string;
    void *info;
};

/**
 * Creates and return a new string
 */
String new_string(char *string, void *data);

#endif
