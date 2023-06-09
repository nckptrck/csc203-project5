import processing.core.PImage;
import java.util.List;
import java.util.Random;

public class Sapling extends Plant{
    public static final String TREE_KEY = "tree";
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000;
    private static final int SAPLING_HEALTH_LIMIT = 5;

    public Sapling(String id, Point position, List<PImage> images, int health){
        super(id, position,images, SAPLING_ACTION_ANIMATION_PERIOD,  SAPLING_ACTION_ANIMATION_PERIOD, health, SAPLING_HEALTH_LIMIT);

    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = new Stump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList( STUMP_KEY));

            world.removeEntity(scheduler, this);

            stump.addEntity(world);

            return true;
        } else if (this.getHealth() >= SAPLING_HEALTH_LIMIT) {
            Tree tree = new Tree(TREE_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(TREE_KEY), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);

            tree.addEntity(world);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }
        return false;
        }
    private static double getNumFromRange(double max, double min) {
            Random rand = new Random();
            return min + rand.nextDouble() * (max - min);
        }

    private static int getIntFromRange(int max, int min) {
            Random rand = new Random();
            return min + rand.nextInt(max-min);
        }
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
            scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.getActionPeriod());
            scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());
        }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.setHealth(this.getHealth()+ 1);
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    public static  int getTreeHealthMax(){
        return TREE_HEALTH_MAX;
    }



}
