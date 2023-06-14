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
            Zombie z = ZombieAdjacent(world).get();

            world.removeEntity(scheduler, z);



        }

    }

    public Optional<Zombie> ZombieAdjacent(WorldModel world){
        for(Entity e: world.entities){
            if (e instanceof Zombie){
                Zombie z = (Zombie) e;
                if (z.getPosition().adjacent(this.getPosition())){
                    return Optional.of(z);
                }
            }
        }
        return Optional.empty();
    }



}
