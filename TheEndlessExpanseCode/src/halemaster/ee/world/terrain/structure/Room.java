package halemaster.ee.world.terrain.structure;

import halemaster.ee.world.Area;

/**
 * @name Room
 * 
 * @version 0.0.0
 * 
 * @date Jan 31, 2014
 */
public class Room 
{
  Area[] doors;
  Architecture layout;

  public Area[] getDoors ()
  {
    return doors;
  }

  public void setDoors (Area[] doors)
  {
    this.doors = doors;
  }

  public Architecture getLayout ()
  {
    return layout;
  }

  public void setLayout (Architecture layout)
  {
    this.layout = layout;
  }
}
