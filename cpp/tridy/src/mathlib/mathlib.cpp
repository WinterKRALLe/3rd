#include <mathlib.h>

namespace MyMathModule
{
    int add(int a, int b)
    {
        return a + b;
    }

    Vector add(Vector a, Vector b)
    {
        return Vector(a.getX() + b.getX(), a.getY() + b.getY());
    }
}