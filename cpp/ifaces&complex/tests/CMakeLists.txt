include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

add_executable(
  complex_tests
  complex_tests.cpp
)
target_link_libraries(
  complex_tests
  gtest_main
  ComplexNumber
)

include(GoogleTest)
gtest_discover_tests(complex_tests)
