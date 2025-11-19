import bagel.*;

/**
 * Bullet inherits from Projectile. It is the bullet from mario's gun. It is modified to
 * change its speed. Also, the image is set to be either going left or right, based on its direction.
 */
public class Bullet extends Projectile{

    /**
     * instantiate a bullet at (x,y) coords to face some direction.
     * @param x
     * @param y
     * @param sign +1 for going right ,-1 for going left.
     */
    Bullet(double x, double y, int sign){
        super(x, y, sign);
        this.setSpeed(3.8);
        if (sign > 0){
            this.image = new Image("res/bullet_right.png");
        } else {
            this.image = new Image("res/bullet_left.png");
        }
    }

    /**
     * Return that bullets are on mario's side (he won't take damage from them)
     * @return
     */
    public boolean isEnemy(){
        return false;
    }
}
