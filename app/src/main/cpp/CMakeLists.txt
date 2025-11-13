# Sets the minimum CMake version required.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name.
project("app")

# --- OpenCV Configuration Starts ---
# This path points to the 'opencv' folder you just copied
# (app/src/main/cpp/opencv)
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv)

find_package(OpenCV REQUIRED)
message(STATUS "OpenCV library status:")
message(STATUS "    version: ${OpenCV_VERSION}")
message(STATUS "    libraries: ${OpenCV_LIBS}")
message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
# --- OpenCV Configuration Ends ---


# Creates the library.
# This MUST be 'native-lib' to match the Java code 'System.loadLibrary("native-lib")'
add_library(native-lib SHARED
        native-lib.cpp)


# Find NDK libraries
find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

# Links all the libraries together
target_link_libraries(native-lib
        # Link OpenCV
        ${OpenCV_LIBS}

        # Link NDK libraries
        ${log-lib}
        ${jnigraphics-lib}
        android)