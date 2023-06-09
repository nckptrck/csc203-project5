/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean adjacent( Point p2) {
        return (this.x == p2.x && Math.abs(this.y - p2.y) == 1) || (this.y == p2.y && Math.abs(this.x - p2.x) == 1);
    }

    public  int distanceSquared( Point p2) {
        int deltaX = this.x - p2.x;
        int deltaY = this.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public int manhattanDistance(Point p2){
        int dist = Math.abs(this.x-p2.x) + Math.abs(this.y - p2.y);
        return dist;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Point)){
            return false;
        }
        Point p = (Point) o;
        return p.x == this.x && p.y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
}
