#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>

#include "string_data.h"

/**
 * Creates and return a new string
 */
String
new_string(char *string, void *data) {
    String  s = (String)malloc(sizeof(String));
    assert(s && "String failed to allocate space");
    int string_len = strlen(string);
    s->string = (char *)malloc(sizeof(char)*(string_len+1));
    assert(s->string && "Failed to allocate space for String's string");
    strcpy(s->string, string);
    s->info = data;
    return s;
}
