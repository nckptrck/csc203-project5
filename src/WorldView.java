import processing.core.PApplet;
import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class WorldView {
    public static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;
    private static final int COLOR_MASK = 0xffffff;
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    public Viewport viewport;

    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    private static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public  void shiftView( int colDelta, int rowDelta) {
        int newCol = clamp(this.viewport.col + colDelta, 0, this.world.numCols - this.viewport.numCols);
        int newRow = clamp(this.viewport.row + rowDelta, 0, this.world.numRows - this.viewport.numRows);

        this.viewport.shift(newCol, newRow);
    }
    public  void drawBackground() {
        for (int row = 0; row < this.viewport.numRows; row++) {
            for (int col = 0; col < this.viewport.numCols; col++) {
                Point worldPoint = this.viewport.viewportToWorld( col, row);
                Optional<PImage> image = this.world.getBackgroundImage( worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
                }
            }
        }
    }

    public  void drawEntities() {
        for (Entity entity : this.world.entities) {
            Point pos = entity.getPosition();

            if (this.viewport.contains(pos)) {
                Point viewPoint = this.viewport.worldToViewport( pos.x, pos.y);
                this.screen.image(getCurrentImage(entity), viewPoint.x * this.tileWidth, viewPoint.y * this.tileHeight);
            }
        }
    }

    public  void drawViewport() {
        this.drawBackground();
        this.drawEntities();
    }
    public static PImage getCurrentImage(Object object) {
        if (object instanceof Background background) {
            return background.images.get(background.imageIndex);
        } else if (object instanceof Entity entity) {
            return entity.getImages().get(entity.getImageIndex() % entity.getImages().size());
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }
    public static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public static List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }



}
