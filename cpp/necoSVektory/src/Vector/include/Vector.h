/*
struct Vector
{
    float _x, _y; // field, nevyžaduje getter a setter

    Vector(float x, float y); // constructor

    ~Vector(); // destructor

    void printMe();
};
*/

class Vector
{
    float _x, _y;

public:
    Vector(float x, float y);

    float getX();

    float getY();

    ~Vector();

    void printMe();
};