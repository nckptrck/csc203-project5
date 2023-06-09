import processing.core.PImage;
import java.util.List;
public class Obstacle extends ActionEntity{

    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod){
        super(id, position, images, animationPeriod);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
    }



}
