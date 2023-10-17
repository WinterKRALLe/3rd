add_library(mathlib mathlib.cpp)

target_include_directories(mathlib PUBLIC include)

target_link_libraries(mathlib Vector)