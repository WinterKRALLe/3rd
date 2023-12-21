#include <iostream>
#include <cmath>
#include <AbstractComplex.h>

class Complex : public AbstractComplex<Complex>
{
public:
    float size;
    float angle;

public:
    Complex(float size, float angle) : size(size), angle(angle) {}

    Complex operator*(const Complex &other) const
    {
        float newSize = size * other.size;
        float newAngle = angle + other.angle;
        return Complex(newSize, newAngle);
    }

    Complex inv() const override
    {
        float newSize = 1 / size;
        float newAngle = -angle;

        newAngle = fmod(newAngle, 360.0f);

        if (newAngle < 0)
        {
            newAngle += 360.0f;
        }

        return Complex(newSize, newAngle);
    }

    Complex operator/(const Complex &other) const override
    {
        return *this * other.inv();
    }

    Complex power(int index) const
    {
        if (index == 0)
        {
            return Complex(1, 0);
        }
        else
        {
            Complex result = *this * power(index - 1);

            result.angle = fmod(result.angle, 360.0f);

            if (result.angle < 0)
            {
                result.angle += 360.0f;
            }

            return result;
        }
    }

    friend std::ostream &operator<<(std::ostream &os, const Complex &other)
    {
        os << "[" << other.size << "," << other.angle << "]";
        return os;
    }
};