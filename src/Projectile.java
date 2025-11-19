import bagel.*;
import bagel.util.Rectangle;
import java.util.ArrayList;

/**
 * Projectile is the abstract class that sets blueprint for all the entities fired by the monkeys and
 * mario. It implements Shootable as to easily pass information such as whether it will hurt monkeys
 * mario.
 */
public abstract class Projectile implements Shootable {
    public Image image;
    public final double distance = 300;
    private double speed;
    private double end;
    private int sign;
    private double x;
    private final double y;
    private boolean isDestroyed = false;

    /**
     * Constructor for each projectile is takes in the initial coordinates and their direction
     * @param x initial x
     * @param y initial y
     * @param sign direction (+1 = right, -1 = left)
     */
    Projectile(double x, double y, int sign) {
        this.sign = sign;
        this.x = x;
        this.y = y;
        if (sign == -1){
            end = Math.max(0, x - distance); //Check left-bound is not past 0px
        } else {
            end = Math.min(x+distance, Window.getWidth()); //Check right-bound is not past right side
        }

    }

    /**
     * Update method for all projectiles. Separates them into enemy and !enemy projectiles.
     * Enemy will only interact with mario
     * !Enemy will only interact with monkeys and donkeys
     * Platforms will destroy all bullets.
     *
     * @param monkeys
     * @param mario
     * @param donkey
     */
    public int update(ArrayList<Monkey> monkeys, Mario mario, Donkey donkey, Platform[] platforms){
        if (!isDestroyed){
            //Update bullet position
            x += sign * speed;
            if (sign == -1 && x < end){
                this.destroy();
            } else if (sign == 1 && x > end){
                this.destroy();
            }
        }
        int deadMonkeys = 0;
        if (this.isEnemy()){ //Handle the enemy's bullets, they can only kill mario
            this.projectileCollision(mario);
        } else { //Handle mario's bullets, they kill monkeys and donkey dong
            for (Monkey monkey : monkeys){
                boolean hit = this.projectileCollision(monkey); //Any monkey hit by bullet
                if (hit){deadMonkeys++;}
            }
            this.projectileCollision(donkey); //Donkey hit by bullet
        }
        //Destroy projectile if they hit a platform
        for(Platform platform : platforms){
            if (this.getBoundingBox().intersects(platform.getBoundingBox())){
                this.destroy();
                break;
            }
        }

        this.draw();
        return deadMonkeys;
    }

    /**
     * Draw the Projectile to the bagel screen
     */
    public void draw() {
        if (!isDestroyed) {
            image.draw(x, y);
        }
    }

    /**
     * Set isDestroyed to true. Mandatory for the interface.
     */
    public void destroy(){
        isDestroyed = true;
    }

    /**
     * Mandatory for the interface. Return whether projectile is friend or foe to mario.
     * Left to child classes to implement.
     * @return
     */
    public abstract boolean isEnemy();

    /**
     * Collision method takes a projectile and a Shootable object.
     * Destroy them if they are on different sides of the game (friend/enemy)
     * Calls destroy on them if overlap detected
     * @param other donkey ,monkey etc.
     */
    public boolean projectileCollision(Shootable other){
        if (this.isEnemy() != other.isEnemy()){
            if (this.getBoundingBox().intersects(other.getBoundingBox())) {
                //Mutual destruction
                other.destroy();
                this.destroy();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Rectangle type to use for overlap calculations.
     * @return Rectangle
     */
    public Rectangle getBoundingBox() {
        if (isDestroyed) {
            return new Rectangle(-1000, -1000, 0, 0); // Off-screen if destroyed
        }
        return new Rectangle(
                x - (image.getWidth() / 2),
                y - (image.getHeight() / 2),
                image.getWidth(),
                image.getHeight()
        );
    }

    /**
     * Get the boolean value of isDestroyed and return
     * @return
     */
    public boolean checkDestroyed(){
        return isDestroyed;
    }

    /**
     * Set the double value of speed
     * @param speed
     */
    public void setSpeed(double speed) {this.speed = speed;}
}
