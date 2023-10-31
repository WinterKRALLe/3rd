#include <Vector.h>
#include <iostream>
#include <cmath>

Vector::Vector(float x, float y) : _x(x), _y(y)
{
}

void Vector::printMe()
{
    std::cout << "vektor" << std::endl;
    std::cout << _x << "," << _y << std::endl;
}

float Vector::getX()
{
    return _x;
}

float Vector::getY()
{
    return _y;
}

Vector Vector::add(Vector a, Vector b)
{
    return Vector(a.getX() + b.getX(), a.getY() + b.getY());
}

Vector Vector::multiply(float a, Vector b)
{
    return Vector(a * b.getX(), a * b.getY());
}

Euclid::Euclid(float x, float y) : Vector(x, y)
{
}

float Euclid::scalar_mul(Euclid other)
{
    return this->_x * other.getX() + this->_y * other.getY();
}

float Euclid::scalar_mul(Euclid a, Euclid b)
{
    return a.getX() * b.getX() + a.getY() * b.getY();
}

float Euclid::operator+(Euclid other)
{
    return Euclid::scalar_mul(*this, other);
}

Euclid Euclid::add(Euclid a, Euclid b)
{
    Vector res = Vector::add(a, b);
    return Euclid(res.getX(), res.getY());
}

float Euclid::getSize()
{
    float mojex = _x;
    return sqrt(scalar_mul(*this, *this));
}

void Euclid::printMe()
{
    std::cout << "euclidean vector" << std::endl;
    std::cout << _x << "," << _y << std::endl;
}
