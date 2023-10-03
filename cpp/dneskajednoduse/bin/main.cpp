#include <iostream>
#include <mathfuncs.h>

int main(int argc, char const *argv[])
{
    std::cout << "From main" << std::endl;
    std::cout << "Sum: " + std::to_string(add(2, 2)) << std::endl;
    std::cout << "Difference: " + std::to_string(sub(2, 2)) << std::endl;
    std::cout << "Product: " + std::to_string(mul(2, 2)) << std::endl;
    std::cout << "Quotient: " + std::to_string(division(2.0, 0.0)) << std::endl;

    std::list<int> myList = {1, 2, 3, 4, 5};
    int result = sum(myList, 0);

    std::cout << result << std::endl;

    return 0;
}
