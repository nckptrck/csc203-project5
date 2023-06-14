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
        if (FairyAdjacent(world).isPresent()){
            System.out.println("HELLLOOOOOO");
            Fairy f = FairyAdjacent(world).get();

            world.removeEntity(scheduler, f);



        }

    }

    public Optional<Fairy> FairyAdjacent(WorldModel world){
        for(Entity e: world.entities){
            if (e instanceof Fairy){
                Fairy f = (Fairy) e;
                if (f.getPosition().adjacent(this.getPosition())){
                    return Optional.of(f);
                }
            }
        }
        return Optional.empty();
    }



}
