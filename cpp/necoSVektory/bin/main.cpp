#include <iostream>
#include <mathlib.h>
#include <vector>
#include <list>

int main(int argc, char const *argv[])
{
    Vector sum = MyMathModule::add(Vector(2, 3), Vector(3, 2));
    std::cout << "Add: ";
    sum.printMe();

    Vector mul = MyMathModule::mul(2, Vector(2, 3));
    std::cout << "Mul: ";
    mul.printMe();

    Vector list_comb = MyMathModule::list_comb(2, Vector(2, 3), 3, Vector(4, 5));
    std::cout << "List combination: ";
    list_comb.printMe();

    std::list<Vector> vectors = {Vector(1.0f, 2.0f), Vector(3.0f, 4.0f), Vector(5.0f, 6.0f)};
    std::list<float> scalars = {2, 3, 4};
    std::vector<Vector> result = MyMathModule::mul(vectors, scalars);
    std::cout << "List of vectors:" << std::endl;
    for (size_t i = 0; i < result.size(); ++i)
    {
        Vector v = result[i];
        std::cout << "v" << i + 1 << "";
        v.printMe();
    }

    return 0;
}
