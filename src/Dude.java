import processing.core.PImage;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Dude extends ActivityEntity implements Moveable{

    public int getResourceLimit() {
        return resourceLimit;
    }

    private int resourceLimit;

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    private int resourceCount;

    public Dude(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit){
        super(id, position, images, actionPeriod,  animationPeriod);
        this.resourceCount =  resourceCount;
        this.resourceLimit = resourceLimit;
    }

    @Override
    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    @Override
    public  Point nextPosition( WorldModel world, Point destPos){

        PathingStrategy pathingStrategy = new AStarPathingStrategy(world);

        Predicate<Node> canPassThrough = n -> world.withinBounds(n.p) &&
                //!(world.isOccupied(n.p)) &&
                !(world.getOccupancyCell(n.p) instanceof House) &&
                !(world.getOccupancyCell(n.p) instanceof Obstacle) &&
                !(world.getOccupancyCell(n.p) instanceof Plant) &&
                !(world.getOccupancyCell(n.p) instanceof Fairy) &&
                !(world.getOccupancyCell(n.p) instanceof Dude)
                ;



        BiPredicate<Node, Point> withinReach = (p1, p2) -> (Math.abs(p2.x - p1.p.x) + Math.abs(p2.y - p1.p.y)) == 1;

        Function<Node, Stream<Node>> potentialNeighbors = p -> PathingStrategy.CARDINAL_NEIGHBORS.apply(p);

        // Call the pathing strategy's computePath
        List<Point> searchedPath = pathingStrategy.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);

        if(searchedPath.isEmpty()) {
            return this.getPosition();}
        else{
            return searchedPath.get(0);
        }



    }

}
