import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
public class DudeNotFull extends Dude implements Transformable{

    public DudeNotFull(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod, 0, resourceLimit);
    }
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }
    @Override
    public boolean moveTo( WorldModel world, Entity target, EventScheduler scheduler) {
        Plant targetPlant = ((Plant) target);
        if (this.getPosition().adjacent( targetPlant.getPosition())) {
            this.setResourceCount(this.getResourceCount() + 1);
            targetPlant.setHealth(targetPlant.getHealth()-1);
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
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= this.getResourceLimit()) {
            DudeFull dude = new DudeFull(this.getId(), this.getPosition(), this.getImages(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit());

            world.removeEntity( scheduler, this);
            scheduler.unscheduleAllEvents( this);

            dude.addEntity(world);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
    @Override
    public  void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest( this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveTo( world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }
}
