public class Point {
    String pointName;
    double pointX;
    double pointY;

    Point(String a, double b, double c){
        this.pointName = a;
        this.pointX = b;
        this.pointY = c;
    }

    public double getPointY() {
        return pointY;
    }

    public double getPointX() {
        return pointX;
    }

    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }
}
