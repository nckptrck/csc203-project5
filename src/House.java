import processing.core.PImage;

import java.util.List;

public class House extends Entity{
    public int health;
    public House(String id, Point position, List<PImage> images, int health) {

        super(id, position, images);
        this.health = health;
    }
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        if (this.health <= 0) {
            Entity house = new House(this.getId() + "_" + this.getId(), this.getPosition(), imageStore.getImageList( STUMP_KEY), 1);

            world.removeEntity( scheduler, this);

            house.addEntity(world);

            return true;
        }

        return false;
    }

}
