package halemaster.ee.world.terrain.tile;

/**
 * @name TileStyle
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public enum TileStyle 
{
  OPEN ("open", new boolean[][] {
    {false, false, false},
    {false, false, false},
    {false, false, false}
  }),
  BLOCK ("block", new boolean[][] {
    {true, true, true},
    {true, true, true},
    {true, true, true}
  });
  
  public enum Corner
  {
    TOP_LEFT (315, 45),
    TOP_RIGHT (45, 135),
    BOTTOM_LEFT (135, 225),
    BOTTOM_RIGHT (225, 315);
    
    private int lowRotation;
    private int highRotation;
    
    public static Corner getFromRotation (int rotation)
    {
      Corner corner = null;
      Corner[] corners = Corner.values ();
      
      rotation %= 360;
      
      for (int i = 0; null == corner && i < corners.length; i++)
      {
        if (corners[i].getLow () > corners[i].getHigh ())
        {
          if (corners[i].getLow () < rotation || 
                  corners[i].getHigh () >= rotation)
          {
            corner = corners[i];
          }
        }
        else if (corners[i].getLow () < rotation && 
                corners[i].getHigh () >= rotation)
        {
          corner = corners[i];
        }
      }
      
      return corner;
    }
    
    private Corner (int low, int high)
    {
      this.lowRotation = low;
      this.highRotation = high;
    }
    
    public int getLow ()
    {
      return this.lowRotation;
    }
    
    public int getHigh ()
    {
      return this.highRotation;
    }
  }
  
  public enum Direction
  {
    NORTH (1,0),
    SOUTH (1,2),
    EAST (2,1),
    WEST (0,1),
    NORTH_EAST (2,0),
    NORTH_WEST (0,0),
    SOUTH_EAST (2,2),
    SOUTH_WEST (0,2),
    ABOVE (1,1);
    
    private int x, y;
    
    private Direction (int x, int y)
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
  }
  
  private String name;
  private boolean [][] blocks;
  
  private TileStyle (String name, boolean[][] blocks)
  {
    this.name = name;
    this.blocks = blocks;
  }
  
  public String getName ()
  {
    return this.name;
  }
  
  /**
   * Get the style with the given name;
   * 
   * @param name name of style to get
   * @return style with the given name
   */
  public static TileStyle getStyle (String name)
  {
    TileStyle style = null;
    TileStyle[] tiles = TileStyle.values ();
    
    for (int i = 0; null == style && i < tiles.length; i++)
    {
      if (tiles[i].getName ().equals (name))
      {
        style = tiles[i];
      }
    }
    
    return style;
  }
  
  /**
   * Whether or not the style blocks 
   * 
   * @param corner
   * @param direction
   * @return 
   */
  public boolean blocks (Corner corner, Direction direction)
  {
    boolean[][] rotatedBlock = null;
    
    switch (corner)
    {
      case TOP_LEFT:
        rotatedBlock = this.blocks;
        break;
      case TOP_RIGHT:
        rotatedBlock = new boolean[this.blocks.length][this.blocks.length];
        for (int x = rotatedBlock.length - 1; x >= 0; x--)
        {
          System.arraycopy (this.blocks[rotatedBlock.length - x - 1], 0,
                  rotatedBlock[x], 0, rotatedBlock.length);
        }
        break;
      case BOTTOM_LEFT:
        rotatedBlock = new boolean[this.blocks.length][this.blocks.length];
        for (int x = 0; x < rotatedBlock.length; x++)
        {
          for (int y = rotatedBlock.length - 1; y >= 0; y--)
          {
            rotatedBlock[x][y] = this.blocks[x][rotatedBlock.length - y - 1];
          }
        }
        break;
      case BOTTOM_RIGHT:
        rotatedBlock = new boolean[this.blocks.length][this.blocks.length];
        for (int x = rotatedBlock.length - 1; x >= 0; x++)
        {
          for (int y = rotatedBlock.length - 1; y >= 0; y++)
          {
            rotatedBlock[x][y] = this.blocks[rotatedBlock.length - x - 1]
                    [rotatedBlock.length - y - 1];
          }
        }
        break;
    }
    
    return rotatedBlock[direction.getX ()][direction.getY ()];
  }
}
