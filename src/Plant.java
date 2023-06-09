import processing.core.PImage;
import java.util.List;
public abstract class Plant extends ActivityEntity implements Transformable{

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    private int health;
    private int healthLimit;

    Plant(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit){
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    @Override
    public abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
