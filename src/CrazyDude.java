import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrazyDude extends Dude {

    public CrazyDude(String id, Point position, List<PImage> images, double actionPeriod,  double animationPeriod,  int resourceLimit) {
        super(id, position, images, actionPeriod,  animationPeriod, 0, resourceLimit);
    }
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public  boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler){
        if (this.getPosition().adjacent( target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition( world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);

            }
            return false;
        }
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = new DudeNotFull(this.getId(), this.getPosition(), this.getImages(), this.getActionPeriod(), this.getAnimationPeriod(),  this.getResourceLimit() );

        world.removeEntity( scheduler, this);

        dude.addEntity(world);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    @Override
    public void executeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest( this.getPosition(), new ArrayList<>(List.of(Fairy.class)));

        if (fullTarget.isPresent() && this.moveTo( world, fullTarget.get(), scheduler)) {
            world.removeEntity(scheduler,this);
            scheduler.unscheduleAllEvents(this);
        } else {

            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }


}