import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairy extends ActivityEntity implements Moveable{
    public static final String SAPLING_KEY = "sapling";

    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }
    @Override
    public boolean moveTo( WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent( target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }
    @Override
    public Point nextPosition( WorldModel world, Point destPos) {
        PathingStrategy pathingStrategy = new AStarPathingStrategy(world);

        Predicate<Node> canPassThrough = n -> world.withinBounds(n.p) &&
                !(world.isOccupied(n.p)) &&
                !(world.getOccupancyCell(n.p) instanceof House) &&
                !(world.getOccupancyCell(n.p) instanceof Stump) &&
                !(world.getOccupancyCell(n.p) instanceof Obstacle) &&
                !(world.getOccupancyCell(n.p) instanceof Plant) &&
                !(world.getOccupancyCell(n.p) instanceof Dude) &&
                !(world.getOccupancyCell(n.p) instanceof Fairy);



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

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest( this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList( SAPLING_KEY), 0);

                sapling.addEntity(world);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
    }
}
