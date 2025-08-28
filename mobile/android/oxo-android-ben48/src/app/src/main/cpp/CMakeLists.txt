cmake_minimum_required(VERSION 3.22.1)

project("nativelib")

add_library(nativelib SHARED nativelib.cpp)

find_library(log-lib log)

target_link_libraries(nativelib ${log-lib})