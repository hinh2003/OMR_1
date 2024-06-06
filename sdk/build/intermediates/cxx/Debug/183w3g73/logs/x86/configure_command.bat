@echo off
"D:\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HE:\\Projects Android\\nckh8\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=D:\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=D:\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=D:\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=D:\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=E:\\Projects Android\\nckh8\\sdk\\build\\intermediates\\cxx\\Debug\\183w3g73\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=E:\\Projects Android\\nckh8\\sdk\\build\\intermediates\\cxx\\Debug\\183w3g73\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BE:\\Projects Android\\nckh8\\sdk\\.cxx\\Debug\\183w3g73\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
