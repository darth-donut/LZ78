cmake_minimum_required(VERSION 3.5)
project(lz78)

set(CMAKE_CXX_STANDARD 11)
set(CXX_FLAGS "-Wall -Werror -Wpedantic -Wextra -g")

#compiler settings
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${CXX_FLAGS}")

# include dependencies (Trie.h, Node.h from /usr/local/include/ds/trie/)
include_directories("libs")

# exe
set(SOURCE_FILES lz78.cc Bit_encode.cc Bit_encode.h Byte.h Token.cc Token.h)
add_executable(lz78 ${SOURCE_FILES})
