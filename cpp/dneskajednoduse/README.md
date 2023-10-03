cd build\
main:\
cmake -G "Unix Makefiles" .. && cmake --build . && ./bin/dneskajednoduse\
tests:\
cmake -G "Unix Makefiles" .. && cmake --build . && ctest\
pro Win to bude "MinGW Makefiles" (cmake --help)
