cmake_minimum_required(VERSION 3.4.1)

add_library(sheetmusicreader)

include_directories(
    ../../../native/native-src/fileio/
    ../../../native/native-src/measuredetection/
    ../../../generated-src/cpp/
    ../../../generated-src/jni/
)

target_link_libraries(sheetmusicreader)