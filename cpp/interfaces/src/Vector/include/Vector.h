#include <iostream>
#include <AbstractVector.h>

class EVector : public AbstractVector<EVector>
{
    float _x, _y;

public:
    EVector(float x, float y)
    {
        _x = x;
        _y = y;
    }

    EVector operator+(const EVector &other) const
    {
        return EVector(_x + other._x, _y + other._y);
    }

    float scalar(const EVector &other) const
    {
        return _x * other._x + _y * other._y;
    }

    friend std::ostream &operator<<(std::ostream &os, const EVector &other)
    {
        os << other._x << other._y;
        return os;
    }
};