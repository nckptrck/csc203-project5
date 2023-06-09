public class Node {
    public Point p;
    public int gCost;
    public int fCost;
    public int hCost;

    public Node last;


    public Node(Point p){
        this.p = p;
        this.gCost = 0;
        this.fCost = 0;
        this.hCost = 0;
        this.last = null;
    }

    @Override
    public String toString(){
        return "[ Point:  (" + this.p.x + "," + this.p.y + "), " + "g=" + this.gCost + ", f=" + this.fCost + "]";
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Node)){
            return false;
        }
        Node n = (Node) o;
        return n.p.equals(this.p);

    }



}
