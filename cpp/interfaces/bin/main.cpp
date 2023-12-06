#include <iostream>
#include <cmath>
#include <Vector.h>

using namespace std;

int main()
{

    auto ev = EVector(5, 5) + EVector(4, 4);

    auto res = AbstractVector<EVector>::norm(EVector(3, 3));

    cout << EVector(3, 4).scalar(EVector(3, 3)) << endl;

    auto angle = AbstractVector<EVector>::angle(EVector(3, 4), EVector(3, 3));

    cout << res << endl;

    cout << angle << endl;

    return 0;
}

/*
#include <iostream>
#include <cmath>

using namespace std;

template <class T>
class Addable
{
    virtual T operator+(const T &other) const = 0;
};

template <class T>
class Scalarable
{
    virtual float scalar(const T &other) const = 0;
};

template <class T>
class AbstractVector : public Addable<T>, Scalarable<T>
{
public:
    static float norm(const T &other)
    {
        return sqrt(other.scalar(other));
    }

    static float angle(const T &first, const T &second)
    {
        return acos(first.scalar(second) / (norm(first) * norm(second)));
    }
};

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

    friend ostream &operator<<(ostream &os, const EVector &other)
    {
        os << other._x << other._y;
        return os;
    }
};

// udelat LLVektor
class LLVector : public AbstractVector<LLVector>
{
};

int main()
{

    auto ev = EVector(5, 5) + EVector(4, 4);

    auto res = AbstractVector<EVector>::norm(EVector(3, 3));

    cout << EVector(3, 4).scalar(EVector(3, 3)) << endl;

    auto angle = AbstractVector<EVector>::angle(EVector(3, 4), EVector(3, 3));

    cout << res << endl;

    cout << angle << endl;

    return 0;
}
*/

/*
// udelat abstractVektor, ktery ma x,y
// ma statickou funkci add, ktera secte dva vektory
template <class T>
class AbstractVector
{
protected:
    float _x, _y;

public:
    T operator+(const T &other)
    {
        return T(_x + other._x, _y + other._y);
    }

    friend ostream &operator<<(ostream &os, const AbstractVector &other)
    {
        os << other._x << other._y;
        return os;
    }
};

// udelat EuclideanVector, podedi AV
// navic má skalarni soucin
class EVector : public AbstractVector<EVector>
{

public:
    EVector(float x, float y)
    {
        _x = x;
        _y = y;
    }
};

// udelat LLVektor
class LLVector : public AbstractVector<LLVector>
{
};

int main()
{

    cout << "Good Morning, World" << endl;

    auto ev = EVector(5, 5) + EVector(4, 4);

    cout << ev << endl;

    return 0;
}*/