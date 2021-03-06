package halemaster.ee.world;

/**
 * @name Area
 * 
 * @version 0.0.0
 * 
 * @date Nov 9, 2013
 */
public class Area 
{
  public static final Area ANYWHERE = new Area (-1, -1);
  private int x;
  private int y;
  
  public Area (int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  
  public int getX ()
  {
    return this.x;
  }
  
  public int getY ()
  {
    return this.y;
  }
  
  /**
   * Required to make sure we are testing more than the pointers.
   * 
   * @param obj obj to compare to.
   * @return whether the objects equal each other.
   */
  @Override
  public boolean equals (Object obj)
  {
    if (obj instanceof Area)
    {
      return (getX () == ((Area) obj).getX () && 
              getY () == ((Area) obj).getY ());
    }
    else
    {
      return false;
    }
  }
  
  /**
   * get the distance between two Areas
   * @param other other area to get distance of
   * @return integer distance between two areas
   */
  public int distance (Area other)
  {
    return (int) Math.sqrt (Math.pow (Math.abs (getX () - other.getX ()), 2) + 
            Math.pow (Math.abs (getY () - other.getY ()), 2));
  }

  /**
   * Used to determine the hash of this area. Equals likes to have this
   * implemented, so I used a default implementation.
   * 
   * @return the hash code of this object.
   */
  @Override
  public int hashCode ()
  {
    int hash = 3;
    hash = 43 * hash + this.x;
    hash = 43 * hash + this.y;
    return hash;
  }
  
  @Override
  public String toString ()
  {
    if (ANYWHERE != this)
    {
      return this.x + "," + this.y;
    }
    else
    {
      return "anywhere";
    }
  }
}
