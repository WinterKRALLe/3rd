#include <iostream>

template<class T>
class Vector {
protected:
    T x, y;

public:
    Vector(T x, T y) : x(x), y(y) {}

    Vector<T> add(const Vector<T> &other) const {
        return Vector<T>(x + other.x, y + other.y);
    }

    Vector<T> operator+(const Vector<T> &other) const {
        return add(other);
    }

    void print() const {
        std::cout << "(" << x << ", " << y << ")" << std::endl;
    }
};

template<class T>
class EVektor : public Vector<T> {
public:
    EVektor(T x, T y) : Vector<T>(x, y) {}

    EVektor(const Vector<T> &vec) : Vector<T>(vec) {}

    EVektor<T> multiply(T scalar) const {
        return EVektor<T>(this->x * scalar, this->y * scalar);
    }

    EVektor<T> operator*(T scalar) const {
        return multiply(scalar);
    }
};

template<class T>
class LLVektor : public Vector<T> {
public:
    using Vector<T>::Vector;

    LLVektor(const Vector<T> &vec) : Vector<T>(vec) {}

    LLVektor<T> operator+(const LLVektor<T> &other) const {
        LLVektor<T> result = Vector<T>::add(other);
        result.x *= 2;
        result.y *= 2;
        return result;
    }
};

int main() {
    EVektor<int> v1(1, 2);
    EVektor<int> v2(3, 4);

    std::cout << "EuclidVektor v1: ";
    v1.print();

    std::cout << "EuclidVektor v2: ";
    v2.print();

    EVektor<int> resultEuclid = v1 + v2;

    std::cout << "EuclidVektor v1 + v2: ";
    resultEuclid.print();

    LLVektor<int> v3(1, 2);
    LLVektor<int> v4(3, 4);

    std::cout << "LLVektor v3: ";
    v3.print();

    std::cout << "LLVektor v4: ";
    v4.print();

    LLVektor<int> resultLL = v3 + v4;

    std::cout << "LLVektor v3 + v4 (result multiplied by 2): ";
    resultLL.print();

    return 0;
}

/*
Jak by to mělo vypadat:
#include <iostream>

using namespace std;

template<class T>
class AbstractVector {
protected:
    float _x, _y;

public:
    float getX() const {
        return _x;
    }

    float getY() const {
        return _y;
    }

    T add(const T &other) {
        return T(_x + other._x, _y + other._y);
    }

    friend ostream &operator<<(ostream &os, const AbstractVector &other) {
        os << other._x << ", " << other._y;
        return os;
    }
};

class EVector : public AbstractVector<EVector> {
public:
    EVector(float x, float y) {
        _x = x;
        _y = y;
    }

    float scalar(const EVector &other) {
        return _x * other._x + _y * other._y;
    }

};

int main() {
    auto first = EVector(5,5);
    cout << first.add(EVector(6,6)) << endl;
    cout << first.scalar(first) << endl;

}
*/