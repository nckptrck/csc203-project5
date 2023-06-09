import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class Entity {

    public static final String STUMP_KEY = "stump";
    public static final String TREE_KEY = "tree";
    public static final String SAPLING_KEY = "sapling";

    public String getId() {
        return id;
    }

    private String id;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    private Point position;

    public List<PImage> getImages() {
        return images;
    }

    private List<PImage> images;

    public int getImageIndex() {
        return imageIndex;
    }

    private int imageIndex;



    public Entity( String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log() {
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }


    public  void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }


    public  void addEntity(WorldModel world) {
        if (world.withinBounds( this.position)) {
            world.setOccupancyCell( this.position, this);
            world.entities.add(this);
        }
    }
    public void tryAddEntity(WorldModel world) {
        if (world.isOccupied( this.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(world);
    }

}
