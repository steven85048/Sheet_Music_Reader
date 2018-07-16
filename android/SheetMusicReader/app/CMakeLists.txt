cmake_minimum_required(VERSION 3.4.1)

file(GLOB main_source
    ../../../native/dependencies/deps/djinni/support-lib/jni/*.cpp
    ../../../generated-src/jni/*.cpp
    ../../../generated-src/cpp/*.cpp
)

add_library(helloworld SHARED ${main_source})

# include directories for header files
include_directories(
    ../../../native/dependencies/deps/djinni/support-lib/
    ../../../native/dependencies/deps/djinni/support-lib/jni/
    ../../../generated-src/cpp/
    ../../../generated-src/jni/
    ../../../src/cpp/
)

target_link_libraries(helloworld)