#include <Vector.h>
#include <iostream>

Vector::Vector(float x, float y) : _x(x), _y(y)
{
    // std::cout << "jdu na scénu" << std::endl;
}

float Vector::getX()
{
    return _x;
}

float Vector::getY()
{
    return _y;
}

void Vector::printMe()
{
    std::cout << "(" << _x << "," << _y << ")" << std::endl;
}

Vector::~Vector()
{
    // std::cout << "mizím ze scény (- Rakeťáci)" << std::endl;
}