package halemaster.ee.item;

/**
 * @name InventoryListener
 * 
 * @version 0.0.0
 * 
 * @date Mar 30, 2014
 */
public interface InventoryListener 
{
  public void pickupItem (ItemSet item);
  public void loseItem (ItemSet item);
}
