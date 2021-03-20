package halemaster.ee.item;

/**
 * @name ItemSet
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 * 
 * NOTE: This is a PLACEHOLDER and will move in the future!
 */
public class ItemSet 
{
  private Item item;
  private int size;
  
  public ItemSet ()
  {
    this (null, 0);
  }
  
  public ItemSet (Item item, int size)
  {
    this.item = item;
    this.size = size;
  }
  
  public String getName ()
  {
    return this.item.getName ();
  }
  
  public int getAmount ()
  {
    return this.size;
  }
  
  public Item getItem ()
  {
    return this.item;
  }
  
  public void setAmount (int amount)
  {
    this.size = amount;
  }
  
  public void setItem (Item item)
  {
    this.item = item;
  }
}
