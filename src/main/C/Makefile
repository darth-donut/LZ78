# Makefile for trie + lz78 compression (utilizing trie and an additional 'string' data stuct)
# AUTHOR: JIAHONG FONG
# Saturday 19 November  13:23:58 AEDT 2016


CC		= gcc
CFLAGS  = -pedantic -Wall -Wextra -ansi -std=c99 -g
EXE		= lz78
HDR		= string_data.h trie.h lz78.h
SRC		= $(HDR:.h=.c)
OBJ		= $(SRC:.c=.o)


.PHONY:
$(EXE): $(OBJ)
	$(CC) $(CFLAGS) -o $@ $(OBJ)

.PHONY:
clean:
	rm -f $(OBJ)

.PHONY:
clobber: clean
	rm -f $(EXE)


$(OBJ): $(HDR)
