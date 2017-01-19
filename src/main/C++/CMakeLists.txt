cmake_minimum_required(VERSION 3.5)
project(lz78)

set(CMAKE_CXX_STANDARD 11)
set(CXX_FLAGS "-Wall -Werror -Wpedantic -Wextra -g")

#compiler settings
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${CXX_FLAGS}")

# include trie
set(DEP_LIBS libtrie.so)
# default library
link_directories(~/Templates/)

#include trie.h
include_directories(~/Templates/include/)

# exe
set(SOURCE_FILES main.cpp)
add_executable(lz78 ${SOURCE_FILES})
target_link_libraries(lz78 ${DEP_LIBS})