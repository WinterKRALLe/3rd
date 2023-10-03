#include <gtest/gtest.h>
#include "mathfuncs.h"

TEST(MathFuncsTest, AddTest)
{
    EXPECT_EQ(add(2, 3), 5);
}

TEST(MathFuncsTest, SubTest)
{
    EXPECT_EQ(sub(5, 3), 2);
}

// cmake -G "Unix Makefiles" .. && cmake --build . && ctest