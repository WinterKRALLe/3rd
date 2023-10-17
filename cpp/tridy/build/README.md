main:
cmake -G "Unix Makefiles" .. && cmake --build . && ./bin/tridy
tests:
cmake -G "Unix Makefiles" .. && cmake --build . && ctest
