#include <mathlib.h>
#include <vector>
#include <list>

namespace MyMathModule
{
    Vector add(Vector a, Vector b)
    {
        return Vector(a.getX() + b.getX(), a.getY() + b.getY());
    }

    Vector mul(int s, Vector v)
    {
        return Vector(s * v.getX(), s * v.getY());
    }

    Vector list_comb(int s1, Vector v1, int s2, Vector v2)
    {
        Vector mul1 = mul(s1, v1);
        Vector mul2 = mul(s2, v2);
        return add(mul1, mul2);
    }

    std::vector<Vector> mul(std::list<Vector> v, std::list<float> f)
    {
        if (v.empty() || f.empty())
        {
            return {};
        }

        int s = f.front();
        f.pop_front();
        const Vector &firstVector = v.front();
        v.pop_front();

        Vector resultVector = mul(s, firstVector);

        std::vector<Vector> result = mul(v, f);

        result.insert(result.begin(), resultVector);

        return result;
    }

}