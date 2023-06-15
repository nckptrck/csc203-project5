import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class KillerTree extends ActivityEntity{

    public KillerTree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);

    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }


    @Override
    public  void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (ZombieAdjacent(world).isPresent()){
            System.out.println("HELLLOOOOOO");
            Zombie f = ZombieAdjacent(world).get();
            world.removeEntity(scheduler, f);
            Point newHouse = this.getPosition();
            world.removeEntity(scheduler, this);
            House house = new House("house", newHouse, imageStore.getImageList("house"), 1);

        }
        else {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }

    }

    public Optional<Zombie> ZombieAdjacent(WorldModel world){
        for(Entity e: world.entities){
            if (e instanceof Zombie){
                Zombie f = (Zombie) e;
                if (f.getPosition() == this.getPosition()){
                    return Optional.of(f);
                }
            }
        }
        return Optional.empty();
    }



}
