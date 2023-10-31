#include <iostream>
#include <Vector.h>

using namespace std;

int main()
{
    Vector v = Vector(4, 4);
    v.printMe();
    Vector result = Vector::add(Vector(5, 5), Vector(5, 5));
    result.printMe();

    Vector result2 = Euclid::add(Euclid(5, 5), Euclid(5, 5));

    float scalarres = Euclid::scalar_mul(Euclid(2, 2), Euclid(3, 3));

    float methodres = Euclid(6, 6).scalar_mul(Euclid(7, 7));
    float methodres2 = Euclid(6, 6) + (Euclid(7, 7));

    cout << methodres << endl;

    cout << methodres2 << endl;

    return 0;
}