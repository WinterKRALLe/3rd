#include "../interface/ComplexMul.h"
#include "../interface/ComplexInv.h"

template <class T>
class AbstractComplex : public Multiplicable<T>, Invertible<T>
{
public:
    virtual T operator/(const T &other) const = 0;
    virtual T power(int index) const = 0;
};