template <class T>
class Multiplicable
{
    virtual T operator*(const T &other) const = 0;
};