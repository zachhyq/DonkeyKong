import bagel.Image;
import bagel.Window;
import bagel.util.Rectangle;

/**
 * Monkey is the class for monkeys and the parent of SmartMonkeys. It implements shootable so that
 * the bullets can destroy it.
 */
public class Monkey implements Shootable {

    public Image MONKEY_IMAGE;
    private final Image MONKEY_LEFT_IMAGE;
    private final Image MONKEY_RIGHT_IMAGE;
    private double x;
    private double y;
    private double velocityY = 0;
    public static final double SPEED = 0.5;
    private boolean isDestroyed = false;

    private boolean faceRight;  //Boolean for whether monkey is facing right.
    private int[] directions;   //Int array describing the step cycle [400, 300, 500] etc.
    private int changes;    //Number of different changes in the step cycle
    private double currSteps;   //The number of steps taken in a particular direction
    private int currStage;  //The index of steps cycle at which the monkey is currently at.

    /**
     * The basic normal non-shooting monkey is defined by this class. It is given left and right images
     * to match its direction. Constructor parses the string from app.properties.
     * @param data
     */
    public Monkey(String data) {
        this.MONKEY_LEFT_IMAGE = new Image("res/normal_monkey_left.png");
        this.MONKEY_RIGHT_IMAGE = new Image("res/normal_monkey_right.png");
        this.MONKEY_IMAGE = MONKEY_LEFT_IMAGE;

        String[] setup = data.split(";");
        String[] partA = setup[0].split(",");
        String[] partB = setup[2].split(",");
        this.changes = partB.length;
        this.x = Integer.parseInt(partA[0]);
        this.y = Integer.parseInt(partA[1]);
        this.faceRight = (setup[1].equals("right"));
        this.directions = new int[changes];

        for (int i=0; i<changes; i++){
            directions[i] = Integer.parseInt(partB[i]);
        }
        this.currStage = 0;
        this.currSteps = directions[currStage];

    }

    /**
     * Each update, monkeys are updated to match their walking cycles, and their sprites
     * change if they change direction. Mario - monkey interactions are handled. They are destroyed
     * if mario wields a hammer, and kill him if he isn't.
     * @param platforms arraylist of platform objects
     * @param mario
     */
    public boolean update(Platform[] platforms, Mario mario) {
        boolean points = false;
        if (!isDestroyed) {
            manageMovement(platforms);

            if (faceRight){
                MONKEY_IMAGE = MONKEY_RIGHT_IMAGE;
            } else {
                MONKEY_IMAGE = MONKEY_LEFT_IMAGE;
            }
            points = marioMonkey(mario);
            draw();

        }
        return points;
    }

    /**
     * Draw the monkey to the bagel screen.
     */
    public void draw() {
        if (!isDestroyed) {
            MONKEY_IMAGE.draw(x, y);
//            drawBoundingBox(); // Uncomment for debugging
        }
    }

    /**
     * Return a rectangle which can be used for overlap calculations.
     * @return
     */
    public Rectangle getBoundingBox() {
        if (isDestroyed) {
            return new Rectangle(-1000, -1000, 0, 0); // Off-screen if destroyed
        }
        return new Rectangle(
                x - (MONKEY_IMAGE.getWidth() / 2),
                y - (MONKEY_IMAGE.getHeight() / 2),
                MONKEY_IMAGE.getWidth(),
                MONKEY_IMAGE.getHeight()
        );
    }

    /**
     * Change the walking direction of the monkey when called. That includes
     * updating walking cycle stage as well.
     */
    public void changeDirection(){
        faceRight = !faceRight;
        currStage++;
        if (currStage == changes){
            currStage = 0;
        }
        currSteps = directions[currStage];

    }

    /**
     * Check if a hammer-holding mario is interacting with any monkey. If so, kill them.
     * If mario doesn't have a hammer, destroy mario and end the game.
     * @param mario
     * @return
     */
    public boolean marioMonkey(Mario mario){
        if (this.getBoundingBox().intersects(mario.getBoundingBox())){
            if (!mario.holdHammer()){
                mario.destroy();
            } else {
                this.destroy();
                System.out.println("Killed by mario");
                return true;
            }
        }
        return false;
    }

    /**
     * This method collectively manages all gravity and platform interactions for the monkeys.
     * Also handles the horizontal movement.
     * @param platforms
     */
    public void manageMovement(Platform[] platforms) {
        // 1) Apply gravity
        velocityY += Physics.MONKEY_GRAVITY;
        if (velocityY > Physics.BARREL_TERMINAL_VELOCITY) {
            velocityY = Physics.BARREL_TERMINAL_VELOCITY;
        }
        y += velocityY;

        //a) Apply initial deltax, change side if reached end.
        boolean directionChanged = false;
        if (faceRight) {
            x += SPEED;
        } else {
            x -= SPEED;
        }
        currSteps -= SPEED;
        if (currSteps == 0 || this.getBoundingBox().right() == Window.getWidth()
                || this.getBoundingBox().left() == 0) {
            this.changeDirection();
            directionChanged = true;
        }

        // 2) Check if on platform
        for (Platform platform : platforms) {
            if (this.getBoundingBox().intersects(platform.getBoundingBox())) {
                // Position the barrel on top of the platform
                y = platform.getY() - (platform.getHeight() / 2) - (MONKEY_IMAGE.getHeight() / 2);
                velocityY = 0; // Stop falling
                // Check if it's at the end of the platform and change direction if not already done
                if (this.getBoundingBox().left() == platform.getBoundingBox().left()
                        || this.getBoundingBox().right() == platform.getBoundingBox().right()) {
                    if (!directionChanged) {
                        this.changeDirection();
                    }
                }
                break;
            }
        }
    }

    /**
     * Return true because monkeys are the enemy of mario
     * @return
     */
    public boolean isEnemy(){
        return true;
    }

    /**
     * Destroy method mandated by interface sets isDestroyed to true.
     */
    public void destroy(){
        isDestroyed = true;
    }

    /**
     * Return the current double x-value
     * @return
     */
    public double getX(){
        return x;
    }

    /**
     * Retuns the current double y-value
     * @return
     */
    public double getY(){
        return y;
    }

    /**
     * Returns the current boolean value of isDestroyed
     * @return
     */
    public boolean checkDestroyed(){
        return isDestroyed;
    }

    /**
     * Returns the current boolean value of faceRight. True if monkey facing right.
     * @return
     */
    public boolean checkFacingRight(){
        return faceRight;
    }

}

