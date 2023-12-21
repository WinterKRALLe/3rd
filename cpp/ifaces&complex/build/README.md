build:  
cmake -G "Unix Makefiles" .. && cmake --build . && ./bin/interfaces  
tests:  
cmake -G "Unix Makefiles" .. && cmake --build . && ctest  
