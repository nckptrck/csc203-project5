import processing.core.PImage;

import java.util.List;

public abstract class ActionEntity extends Entity{

    private double animationPeriod;
    private int imageIndex;

    public ActionEntity(String id, Point position, List<PImage> images, double animationPeriod){
        super(id,position,images);
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }



    public abstract void scheduleActions(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore);


}
