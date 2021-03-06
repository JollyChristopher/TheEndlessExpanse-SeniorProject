package halemaster.ee.world.terrain.tile;

/**
 * @name Tile
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public class Tile 
{
  public static final String TILE_ANIMATION = "tile";
  
  private TiledType type;
  private String[][] images;
  private float speed;
  private int layer;
  
  public enum TileDirection 
  {
    ALL (0),
    MOST_DOWN (1),
    MOST_LEFT (2),
    MOST_UP (3),
    MOST_RIGHT (4),
    LINE_HORIZONTAL (5),
    LINE_VERTICAL (6),
    CORNER_LOWER_LEFT (7),
    CORNER_UPPER_LEFT (8),
    CORNER_UPPER_RIGHT (9),
    CORNER_LOWER_RIGHT (10),
    LEFT (11),
    UP (12),
    RIGHT (13),
    DOWN (14),
    NONE (15);
    
    private int index;
    
    private TileDirection (int index)
    {
      this.index = index;
    }
    
    public int getIndex ()
    {
      return this.index;
    }
    
    public static TileDirection getFromDirection (boolean left, boolean up, 
            boolean right, boolean down)
    {
      TileDirection dir = NONE;
      
      if (left && up && right && down)
      {
        dir = ALL;
      }
      else if (left && up && right)
      {
        dir = MOST_UP;
      }
      else if (down && up && right)
      {
        dir = MOST_RIGHT;
      }
      else if (left && down && right)
      {
        dir = MOST_DOWN;
      }
      else if (left && up && down)
      {
        dir = MOST_LEFT;
      }
      else if (left && right)
      {
        dir = LINE_HORIZONTAL;
      }
      else if (up && down)
      {
        dir = LINE_VERTICAL;
      }
      else if (left && down)
      {
        dir = CORNER_LOWER_LEFT;
      }
      else if (left && up)
      {
        dir = CORNER_UPPER_LEFT;
      }
      else if (right && up)
      {
        dir = CORNER_UPPER_RIGHT;
      }
      else if (right && down)
      {
        dir = CORNER_LOWER_RIGHT;
      }
      else if (left)
      {
        dir = LEFT;
      }
      else if (up)
      {
        dir = UP;
      }
      else if (right)
      {
        dir = RIGHT;
      }
      else if (down)
      {
        dir = DOWN;
      }
      
      return dir;
    }
  }
  
  public Tile ()
  {
    this (null, 0, 0);
  }
  
  public Tile (TiledType type, int speed, int layer, String[] ... images)
  {
    this.type = type;
    this.images = images;
    this.layer = layer;
    this.speed = speed;
  }

  public TiledType getType ()
  {
    return type;
  }

  public void setType (TiledType type)
  {
    this.type = type;
  }

  public String[][] getImages ()
  {
    return this.images;
  }
  
  /**
   * Get the images with the specified direction
   * @param direction direction of image
   * @return images associated with the direction.
   */
  public String[] getImages (TileDirection direction)
  {
    if (1 < this.images.length)
    {
      return this.images[direction.getIndex ()];
    }
    else
    {
      return this.images[0];
    }
  }

  public void setImages (String[] ... images)
  {
    this.images = images;
  }

  public int getLayer ()
  {
    return layer;
  }

  public void setLayer (int layer)
  {
    this.layer = layer;
  }

  public float getSpeed ()
  {
    return speed;
  }

  public void setSpeed (float speed)
  {
    this.speed = speed;
  }
  
  /**
   * Check if something coming onto this tile from the given direction is 
   * blocked.
   * 
   * @param direction direction coming from.
   * @return whether the object is blocked.
   */
  public boolean blocks (TileStyle.Direction direction)
  {
    return this.type.blocks (direction);
  }
}
