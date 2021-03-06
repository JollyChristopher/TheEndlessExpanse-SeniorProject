package halemaster.ee.world.terrain.tile;

import halemaster.ee.world.terrain.tile.TileStyle.Corner;
import halemaster.ee.world.terrain.tile.TileStyle.Direction;

/**
 * @name TiledType
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public class TiledType 
{
  private String style;
  private int rotation;
  
  public TiledType ()
  {
    this (null, 0);
  }
  
  public TiledType (String style, int rotation)
  {
    this.style = style;
    this.rotation = rotation;
  }

  public String getStyle ()
  {
    return style;
  }

  public void setStyle (String style)
  {
    this.style = style;
  }

  public int getRotation ()
  {
    return rotation;
  }

  public void setRotation (int rotation)
  {
    this.rotation = rotation;
  }
  
  /**
   * Check if something coming onto this tile from the given direction is 
   * blocked.
   * 
   * @param direction direction coming from.
   * @return whether the object is blocked.
   */
  public boolean blocks (Direction direction)
  {    
    return TileStyle.getStyle (this.style)
            .blocks (Corner.getFromRotation (this.rotation), direction);
  }
}
