import java.awt.geom.*;

public class Triangle
{
    private final Ellipse2D.Double[] triangle;

    public Triangle(Point a, Point b, Point c)
    {
        triangle = new Ellipse2D.Double[3];
        triangle[0] = a;
        triangle[1] = b;
        triangle[2] = c;
    }

    public double GetX(int index) { return triangle[index].x; }
    public double GetY(int index) { return triangle[index].y; }
}
