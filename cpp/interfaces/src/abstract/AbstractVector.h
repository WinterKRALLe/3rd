#include "../interface/Addable.h"
#include "../interface/Scalarable.h"

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