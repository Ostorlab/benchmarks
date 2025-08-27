cmake_minimum_required(VERSION 3.22.1)

project("ovaa")

add_library(ovaa SHARED ovaa.cpp)

find_library(log-lib log)

target_link_libraries(ovaa ${log-lib})