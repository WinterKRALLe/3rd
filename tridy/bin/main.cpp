#include <iostream>
#include <mathlib.h>
// #include <Vector.h>

int main(int argc, char const *argv[])
{
    // std::cout << MyMathModule::add(5, 5) << std::endl;
    /*
    Vector *vector = nullptr;
    // { } je scope, tedy Vector bude žít než tento scope skončí
    {

        vector = new Vector(5, 5);

        // Vector v(5, 5);
        // Vector v = Vector(5, 5); // Vytvořeno na stacku

        Vector *v = new Vector(5, 5); // Vytvořeno na heapu, vyžaduje delete pro uvolnění z paměti
        v->printMe();
        delete v;
    }
    vector->printMe();
    delete vector;
    */

    Vector result = MyMathModule::add(Vector(2, 3), Vector(3, 2));

    result.printMe();

    std::cout << "Konec programu" << std::endl;
    return 0;
}
