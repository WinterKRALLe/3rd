#include <gtest/gtest.h>
#include "mathlib.h"

TEST(MathlibTests, AddTest)
{
    EXPECT_EQ(MyMathModule::add(2, 3), 5);
}

TEST(MathlibTests, AddVector)
{
    Vector a(2.0f, 3.0f);
    Vector b(1.0f, 2.0f);
    Vector result = MyMathModule::add(a, b);

    EXPECT_FLOAT_EQ(result.getX(), 3.0f);
    EXPECT_FLOAT_EQ(result.getY(), 5.0f);
}

// cmake -G "Unix Makefiles" .. && cmake --build . && ctest