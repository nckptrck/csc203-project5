import processing.core.PImage;
import java.util.List;
public abstract class ActivityEntity extends ActionEntity{
    public double getActionPeriod() {
        return actionPeriod;
    }

    private double actionPeriod;
    public ActivityEntity(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id,position, images, animationPeriod);
        this.actionPeriod = actionPeriod;}

    public abstract void executeActivity(WorldModel worldModel,ImageStore imageStore, EventScheduler eventScheduler );

}
