#include <iostream>
#include <cmath>
#include <ComplexNumber.h>

using namespace std;

int main()
{

    cout << Complex(5, 6) << endl; // [5.0,6.0]

    cout << Complex(1, 60).inv() << endl; //[1.0,300.0]

    cout << Complex(1, 60) * Complex(3, 30) << endl; //[3.0,90.0]

    cout << Complex(1, 60) / Complex(5, 90) << endl; // [0.2, 330.0]

    cout << Complex(1, 60).power(5) << endl; // [1.0, 300.0]

    cout << Complex(2, 45).power(6) << endl; // [64.0, 270.0]

    cout << Complex(2, 135).power(6) << endl; // [64.0, 90.0]

    return 0;
}