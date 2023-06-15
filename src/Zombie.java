import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Zombie extends ActivityEntity implements Moveable{
    public String ZOMBIE_KEY = "zombie";
    public Zombie(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod );
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
        Optional<Entity> zombieTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(House.class)));
        Optional<Entity> nearestTree = world.findNearest(this.getPosition(), new ArrayList<>(List.of(KillerTree.class)));
        System.out.println(nearestTree.get().getPosition());
        if (zombieTarget.isPresent()) {
            Point tgtPos = zombieTarget.get().getPosition();
            if (nearestTree.isPresent()){
                System.out.println("Checking");
                if (this.getPosition().adjacent(nearestTree.get().getPosition())) {
                    System.out.println("Contact");
                    world.removeEntity(scheduler, this);
                    scheduler.unscheduleAllEvents(this);
                    world.removeEntity(scheduler, nearestTree.get());
                    scheduler.unscheduleAllEvents(nearestTree.get());
                    while (world.isOccupied(nearestTree.get().getPosition())){
                        System.out.println("HOUSE");
                        world.removeEntityAt(nearestTree.get().getPosition());
                        scheduler.unscheduleAllEvents(nearestTree.get());
                    }
                    House house = new House("house", nearestTree.get().getPosition(), imageStore.getImageList("house"), 1);
                    house.addEntity(world);

                }
            }
            if (this.moveTo(world, zombieTarget.get(), scheduler)) {
                world.removeEntityAt(tgtPos);
            }
        }

        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
    }

}
