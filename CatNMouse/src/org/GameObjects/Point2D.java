package org.GameObjects;

public class Point2D extends Object {
    public double x, y;

    //int constructor
    public Point2D(int x, int y) {
        setX(x);
        setY(y);
    }
    //float constructor
    public Point2D(float x, float y) {
        setX(x);
        setY(y);
    }
    //double constructor
    public Point2D(double x, double y) {
        setX(x);
        setY(y);
    }

    public boolean compare(Point2D point) {
        if(point.X() == this.X() && point.Y() == this.Y()) {
            return true;
        }
        return false;
    }
    //X property
    public double X() {
        return x;
    }
    public void setX(double x) { this.x = x; }
    public void setX(float x) { this.x = (double) x; }
    public void setX(int x) { this.x = (double) x; }

    //Y property
    public double Y() { return y; }
    public void setY(double y) { this.y = y; }
    public void setY(float y) { this.y = (double) y; }
    public void setY(int y) { this.y = (double) y; }
    
    public Point2D copyPoint() {
        Point2D tmp = new Point2D(this.X(),this.Y());
        return tmp;
    }
}


