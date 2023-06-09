public class Activity implements Action{

    private ActivityEntity entity;
    private WorldModel world;
    private ImageStore imageStore;

    public Activity(ActivityEntity entity, WorldModel worldModel, ImageStore imageStore){
        this.entity = entity;
        this.world = worldModel;
        this.imageStore = imageStore;
    }
    @Override
    public void executeAction(EventScheduler scheduler) {
        entity.executeActivity(this.world, this.imageStore, scheduler);


    }
}
