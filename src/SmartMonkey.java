import bagel.Image;

import java.util.ArrayList;

/**
 * SmartMonkey inherits from normal monkeys, except that now they have updated images and
 * shoot bananas
 */
public class SmartMonkey extends Monkey{
    private Image SMARTMONK_LEFT_IMAGE;
    private Image SMARTMONK_RIGHT_IMAGE;
    private int frameCount = 0;
    private final int FIVE_SECS = 5 * 60; // Game runs at 60fps, so 5 seconds is 5*60 frames

    /**
     * Constructor for smart monkeys. Modified to change their left and right images.
     * @param data
     */
    public SmartMonkey(String data){
        super(data);
        this.SMARTMONK_LEFT_IMAGE = new Image("res/intelli_monkey_left.png");
        this.SMARTMONK_RIGHT_IMAGE = new Image("res/intelli_monkey_right.png");
    }

    /**
     * Update method for smart monkeys includes logic for shooting bullets every five seconds
     * on a fixed cycle. Handles gravity, movement and image. Finally draws donkey out to the screen
     * @param platforms
     * @param projectiles
     * @param mario
     * @return
     */
    public boolean update(Platform[] platforms, ArrayList<Projectile> projectiles, Mario mario) {
        boolean points = false;
        if (!this.checkDestroyed()) {
            //Manage left/right movement here.
            this.manageMovement(platforms);

            if (this.checkFacingRight()){
                MONKEY_IMAGE = SMARTMONK_RIGHT_IMAGE;
            } else {
                MONKEY_IMAGE = SMARTMONK_LEFT_IMAGE;
            }
            points = marioMonkey(mario); //Return points accumulated from killing monkeys with hammer
            //Handle Bananas here
            this.frameCount++;
            if (frameCount == FIVE_SECS) {
                frameCount = 0;
                int sign = (this.checkFacingRight() ? 1 : -1);
                projectiles.add(new Banana(this.getX(), this.getY(), sign));
            }


            draw();

        }
        return points;
    }
}
