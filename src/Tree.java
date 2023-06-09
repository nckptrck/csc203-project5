import processing.core.PImage;
import java.util.List;
public class Tree extends Plant{
    public static final String STUMP_KEY = "stump";

    public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health){
        super(id, position,images, actionPeriod,  animationPeriod, health, Sapling.getTreeHealthMax());
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        if (this.getHealth() <= 0) {
            Entity stump = new Stump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList( STUMP_KEY));

            world.removeEntity( scheduler, this);

            stump.addEntity(world);

            return true;
        }

        return false;
    }
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public  void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transform(world, scheduler, imageStore)) {

            scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }


}
