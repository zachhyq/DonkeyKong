import bagel.*;

/**
 * Banana inherits from Projectile. It customises its speed and image. The isEnemy method is overridden
 * here too.
 */
public class Banana extends Projectile{
    /**
     * Creates a Banana projectile with banana image at coordinates (x,y) in the chosen direction
     * @param x
     * @param y
     * @param sign +1 for going right, -1 for going left
     */
    public Banana(double x, double y, int sign) {
        super(x, y, sign);
        this.setSpeed(1.8);
        this.image = new Image("res/banana.png");
    }

    /**
     * returns that banana is an enemy of mario, so true.
     * @return
     */
    @Override
    public boolean isEnemy() {
        return true;
    }
}
