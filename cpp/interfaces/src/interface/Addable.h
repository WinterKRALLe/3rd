template <class T>
class Addable
{
    virtual T operator+(const T &other) const = 0;
};