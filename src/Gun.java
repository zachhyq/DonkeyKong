import bagel.Image;
import bagel.util.Rectangle;

/**
 * Represents a Gun collectible in the game.
 * The gun can be collected by the player, at which point it disappears from the screen.
 * Picking it up adds 5 bullets to the player's count and erases the hammer, if it was being held.
 * Shootable interface was implemented to make use of its easy destroy method.
 * Uses code from Hammer.
 */
public class Gun implements Shootable {
    private final Image GUN_IMAGE;
    private final double WIDTH, HEIGHT;
    private final double X, Y;
    private boolean isDestroyed = false;

    /**
     * Constructs a Gun at the specified position.
     * Guns are unmoving and not subject to gravity
     * @param startX The initial x-coordinate of the hammer.
     * @param startY The initial y-coordinate of the hammer.
     */
    public Gun(double startX, double startY) {
        this.GUN_IMAGE = new Image("res/blaster.png");
        this.X = startX;
        this.Y = startY;
        this.WIDTH = GUN_IMAGE.getWidth();
        this.HEIGHT = GUN_IMAGE.getHeight();
    }

    /**
     * Returns the bounding box of the hammer for collision detection.
     * If the hammer has been collected, it returns an off-screen bounding box.
     *
     * @return A {@link Rectangle} representing the hammer's bounding box.
     */
    public Rectangle getBoundingBox() {
        if (isDestroyed) {
            return new Rectangle(-1000, -1000, 0, 0); // Move off-screen if collected
        }
        return new Rectangle(
                X - (WIDTH / 2),  // Center-based positioning
                Y - (HEIGHT / 2),
                WIDTH,
                HEIGHT
        );
    }

    /**
     * Update method is called every cycle to see if mario has picked up the gun.
     * If the gun is already destroyed, it will do nothing and won't be drawn.
     * @param mario
     */
    public void update(Mario mario) {
        if (!isDestroyed){
            if (this.getBoundingBox().intersects(mario.getBoundingBox())){
                //We have touched the gun, so we pick it up
                this.destroy();
                mario.collectGun();
            }
            this.draw();
        }
    }


    /**
     * Draws the gun on the screen if it has not been collected.
     */
    public void draw() {
        if (!isDestroyed) {
            GUN_IMAGE.draw(X, Y); // Bagel centers images automatically
//            drawBoundingBox(); // Uncomment for debugging
        }
    }

    /**
     * Marks the gun as collected, removing it from the screen. Used for Shootable interface
     */
    public void destroy() {
        isDestroyed = true;
    }

    /**
     * Returns true because guns are mario's tools. Required for the interface.
     * @return true.
     */
    public boolean isEnemy(){return false;}

}

