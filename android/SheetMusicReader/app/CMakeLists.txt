cmake_minimum_required(VERSION 3.4.1)

message($(CMAKE_SOURCE_DIR))


file(GLOB main_source
    ../../../native/dependencies/deps/djinni/support-lib/jni/*.cpp
    ../../../generated-src/jni/*.cpp
    ../../../generated-src/cpp/*.cpp
    ../../../native/native-src/fileio/*.cpp
    ../../../native/native-src/measuredetection/*.cpp
)

add_library(sheetmusicreader SHARED ${main_source})

# include directories for header files
include_directories(
    ../../../native/dependencies/deps/djinni/support-lib/
    ../../../native/dependencies/deps/djinni/support-lib/jni/
    ../../../generated-src/cpp/
    ../../../generated-src/jni/
    ../../../src/cpp/
    ../../../native/dependencies/opencv-android/OpenCV-android-sdk/sdk/native/jni/include
)

# open cv configuration
add_library(lib-opencv SHARED IMPORTED)
set_target_properties(lib-opencv PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/../../../native/dependencies/opencv-android/OpenCV-android-sdk/sdk/native/libs/${ANDROID_ABI}/libopencv_java.so)

target_link_libraries(sheetmusicreader lib-opencv)