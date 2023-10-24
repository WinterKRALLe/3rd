#include <Vector.h>
#include <vector>
#include <list>

namespace MyMathModule
{
    Vector add(Vector a, Vector b);

    Vector mul(int s, Vector v);

    Vector list_comb(int s1, Vector v1, int s2, Vector v2);

    std::vector<Vector> mul(std::list<Vector>, std::list<float>);

    Vector list_add(std::list<Vector>);
}