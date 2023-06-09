import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import processing.core.*;

import javax.sound.sampled.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;
    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;

    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;
    public int presses = 0;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup(){
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);


        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);

    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = appTime/ timeScale - scheduler.getCurrentTime();
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        presses += 1;
        Point pressed = mouseToPoint();
        Zombie newEntity = new Zombie("zombie", pressed, imageStore.getImageList("zombie"), 2, 0.6);
        newEntity.addEntity(world);
        newEntity.scheduleActions(scheduler, world, imageStore);
        world.setBackgroundCell(pressed, new Background("ZBackground", imageStore.getImageList("ZCentroid")));
        Optional<Entity> crazyTarget = world.findNearest(newEntity.getPosition(), new ArrayList<>(List.of(DudeFull.class, DudeNotFull.class)));
        if (crazyTarget.isPresent()) {
            Point crazyPos = crazyTarget.get().getPosition();
            world.removeEntityAt(crazyPos);
            scheduler.unscheduleAllEvents(crazyTarget.get());
            CrazyDude crazydude = new CrazyDude("crazydude", crazyPos, imageStore.getImageList("crazydude"), 1,.1,0);
            crazydude.addEntity(world);
            crazydude.scheduleActions(scheduler, world, imageStore);
        }
        for(Point p: PathingStrategy.NEIGHBORS_POINTS.apply(pressed).toList()){
            world.setBackgroundCell(p, new Background("ZBackground", imageStore.getImageList("ZBackground")));
            if(world.isOccupied(p) ){
                Entity e = world.getOccupant(p).get();
                if (e instanceof Tree){
                    Tree t = (Tree) e;
                    world.removeEntity(scheduler, t);
                    scheduler.unscheduleAllEvents(t);
                    KillerTree kt = new KillerTree("killertree", p, imageStore.getImageList("killertree"), 1, 1);
                    kt.addEntity(world);
                    kt.scheduleActions(scheduler, world, imageStore);

                }

            }



        }


        try {
            SimpleAudioPlayer.zombie();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);


        Optional<Entity> entityOptional = world.getOccupant( pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            System.out.println(entity.getId() + ": " + entity.getClass());
        }

    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.entities) {
            if(entity instanceof ActionEntity){
                ActionEntity ae = ((ActionEntity) entity);
                ae.scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private Point mouseToPoint() {
        return view.viewport.viewportToWorld( mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView( dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList( DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load( in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
        try {
            SimpleAudioPlayer.main(args);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("one");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("two");
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            System.out.println("three");
            throw new RuntimeException(e);
        }


    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        try {
            SimpleAudioPlayer.main(args);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("one");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("two");
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            System.out.println("three");
            throw new RuntimeException(e);
        }


        return virtualWorld.world.log();
    }
}
