import bagel.util.Rectangle;

/**
 * Interface defines classes that can get shot by projectiles and ensures they have
 * interaction method, destruction method, and identification method.
 */
public interface Shootable {
    /**
     * will return whether each entity is an enemy or a mario friend.
     * This helps manage whether projectiles kill collided enemy or not
     * @return true if enemy
     */
    public boolean isEnemy();

    /**
     * Method used to change the state of the instance to destroyed. In special cases such as donkey
     * kong it will reduce their health
     */
    public void destroy();

    /**
     * To calculate interactions, all Shootables need the base BoundingBox calculations.
     * @return a Rectangle object
     */
    public Rectangle getBoundingBox();
}
