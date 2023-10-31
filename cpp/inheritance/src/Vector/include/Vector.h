class Vector
{

protected:
    float _x;
    float _y;

public:
    Vector(float x, float y);

    float getX();
    float getY();

    void printMe();

    static Vector add(Vector a, Vector b);

    static Vector multiply(float a, Vector b);
};

class Euclid : public Vector
{

public:
    Euclid(float x, float y);

    static float scalar_mul(Euclid a, Euclid b);

    float scalar_mul(Euclid a);

    float operator+(Euclid other);

    static Euclid add(Euclid a, Euclid b);

    float getSize();

    void printMe();
};