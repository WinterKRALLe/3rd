#include <vector>
#include <list>

int add(int a, int b)
{
    return a + b;
}

int sub(int a, int b)
{
    return a - b;
}

int mul(int a, int b)
{
    return a * b;
}

float division(float a, float b)
{
    if (b == 0.0)
        return 0.0;
    return a / b;
}

int sum(std::list<int> li, int accumulator)
{
    if (li.empty())
        return accumulator;

    int front = li.front();
    li.pop_front();
    return sum(li, accumulator + front);
}