#include <gtest/gtest.h>
#include <ComplexNumber.h>

TEST(ComplexTest, Multiplication)
{
    Complex a(1, 60);
    Complex b(3, 30);
    Complex result = a * b;

    EXPECT_FLOAT_EQ(result.size, 3);
    EXPECT_FLOAT_EQ(result.angle, 90);
}

TEST(ComplexTest, Inverse)
{
    Complex a(1, 60);
    Complex result = a.inv();

    EXPECT_FLOAT_EQ(result.size, 1);
    EXPECT_FLOAT_EQ(result.angle, 300);
}
