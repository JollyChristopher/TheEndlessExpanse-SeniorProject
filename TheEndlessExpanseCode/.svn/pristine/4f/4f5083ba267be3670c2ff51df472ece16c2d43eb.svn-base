package halemaster.ee.world.faction;

/**
 * @name DiplomaticGroup
 * 
 * @version 0.0.0
 * 
 * @date Nov 9, 2013
 */
public enum DiplomaticGroup 
{
  NEUTRAL ("neutral", -250, 250),
  FRIENDLY ("friendly", NEUTRAL.getHigh (), 2000),
  ALLY ("ally", FRIENDLY.getHigh (), Integer.MAX_VALUE),
  UNFRIENDLY ("unfriendly", -2000, NEUTRAL.getLow ()),
  ENEMY ("enemy", Integer.MIN_VALUE, UNFRIENDLY.getLow ());
  
  private String name;
  private int lowValue;
  private int highValue;
  
  private DiplomaticGroup (String name, int low, int high)
  {
    this.name = name;
    this.lowValue = low;
    this.highValue = high;
  }
  
  public String getName ()
  {
    return this.name;
  }

  public int getLow ()
  {
    return this.lowValue;
  }

  public int getHigh ()
  {
    return this.highValue;
  }

  /**
   * Get the group whose range contains the given value.
   * 
   * @param value value of the relation to find.
   * @return the group the value is within.
   */
  public static DiplomaticGroup getGroup (int value)
  {
    DiplomaticGroup type = null;
    
    for (DiplomaticGroup group : DiplomaticGroup.values ())
    {
      if (group.getLow () <= value && group.getHigh () >= value)
      {
        type = group;
      }
    }
    
    return type;
  }
}
