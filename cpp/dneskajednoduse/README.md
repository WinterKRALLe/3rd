cd build\
main:\
cmake -G "Unix Makefiles" .. && cmake --build . && ./bin/dneskajednoduse\
tests:\
cmake -G "Unix Makefiles" .. && cmake --build . && ctest
