package halemaster.ee.ai;

import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.Sprite;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.micro.AreaGenerator;
import halemaster.ee.world.terrain.tile.BiomeTile;
import halemaster.ee.world.terrain.tile.TileStyle.Direction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @name MovementAI
 * 
 * @version 0.0.0
 * 
 * @date Feb 19, 2014
 */
public class MovementAI extends AI
{
  public static final float WALK_SPEED = 4f * Sprite.DEFAULT_SIZE;
  public static final int MAX_DISTANCE = 32;
  
  private Area moveTo = null;
  private Vector2f moveLeft = null;
  private Deque<Area> path = null;
  private Area currentArea = null;

  public Area getMoveTo ()
  {
    return moveTo;
  }

  public void setMoveTo (Area moveTo)
  {
    this.moveTo = moveTo;
    this.path = null;
    this.moveLeft = null;
  }
  
  /**
   * Called as an update during the Sprite's update
   * @param tpf delta since last call
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    Area next;
    Vector2f direction;
    
    if (null != this.moveTo)
    {
      if (null == this.path)
      {
        this.path = findPath ();
      }
      // move toward next tile
      next = this.path.peekFirst ();
      if (null != next)
      {
        if (null == this.moveLeft)
        {
          this.moveLeft = new Vector2f (Math.abs (next.getX ()), 
                  Math.abs (next.getY ()));
          this.currentArea = getEntity ().getExactLocation ();
        }
        if (next.getX () > 0 && !Entity.CHANNEL_RIGHT.equals(getEntity ()
                .getSprite ().getCurrentAnimation ()))
        {
          getEntity ().setAnimation (Entity.CHANNEL_RIGHT);
        }
        if (next.getX () < 0 && !Entity.CHANNEL_LEFT.equals(getEntity ()
                .getSprite ().getCurrentAnimation ()))
        {
          getEntity ().setAnimation (Entity.CHANNEL_LEFT);
        }
        direction = new Vector2f (next.getX (), next.getY ());
        direction.x *= tpf * WALK_SPEED;
        direction.y *= tpf * WALK_SPEED;
        getEntity ().moveAbsolute (direction.x, direction.y);
        this.moveLeft.x -= Math.abs (direction.x / AreaGenerator.TILE_WIDTH);
        this.moveLeft.y -= Math.abs (direction.y / AreaGenerator.TILE_HEIGHT);

        // remove tile if we have made it
        if (this.moveLeft.x <= 0 && this.moveLeft.y <= 0)
        {
          this.path.removeFirst();
          getEntity ().snapToGrid (new Area (this.currentArea.getX () + 
                  next.getX (), this.currentArea.getY () + next.getY ()));
          this.moveLeft = null;
        }
      }
      
      if (this.path.isEmpty ())
      {
        this.moveTo = null;
        this.path = null;
        this.moveLeft = null;
      }
    }
  }
  
  /**
   * Finds the relative movement from the entity's current location to moveTo
   * @return Deque of relative areas that can be popped properly for a path
   */
  private Deque<Area> findPath ()
  {
    Deque<Area> found = new ArrayDeque<Area> ();
    List<Area> neighbors;
    Set<Area> evaluated = new HashSet<Area>();
    Set<Area> next = new HashSet<Area>();
    Map<Area, Area> cameFrom = new HashMap<Area, Area>();
    Map<Area, Integer> gScore = new HashMap<Area, Integer>();
    Map<Area, Integer> fScore = new HashMap<Area, Integer>();
    Area start = getEntity ().getExactLocation ();
    Area current = null;
    int possibleGScore;
    boolean foundEnd = false;
    boolean blocking;
    Biome biome;
    BiomeTile tempTile;
    Area temp;
    
    next.add (start);
    gScore.put (start, 0);
    fScore.put (start, gScore.get (start) +
            estimateDistance (start, this.moveTo));
    
    while (!next.isEmpty () && !foundEnd)
    {
      Area lowest = null;
      for (Area attempt : next)
      {
        if (lowest == null || fScore.get (attempt) < fScore.get (lowest))
        {
          lowest = attempt;
        }
      }
      
      current = lowest;
      
      if (lowest.equals (this.moveTo))
      {
        foundEnd = true;
      }
      else
      {
        next.remove (current);
        evaluated.add (current);
        // find list of neighbors which are non blocking tiles
        neighbors = new ArrayList<Area>();
        
        blocking = false;
        temp = new Area (current.getX () + 1, current.getY ());
        if (null != getEntity ())
        {
          tempTile = getEntity ().getHolder ().getHolder ().getTile (temp);
        }
        else
        {
          tempTile = null;
        }
        if (null != tempTile)
        {
          biome = BiomeClassifier.getBiome (tempTile.getBiomeId ());
          for (Integer id : getEntity ().getHolder ().getHolder ().getTile 
                  (temp).getTileIds ())
          {
            if (biome.getGeneration ().getTiles ()[id].blocks (Direction.WEST))
            {
              blocking = true;
            }
          }
          if (!blocking)
          {
            neighbors.add (temp);
          }
        }
        
        blocking = false;
        temp = new Area (current.getX () - 1, current.getY ());
        if (null != getEntity ())
        {
          tempTile = getEntity ().getHolder ().getHolder ().getTile (temp);
        }
        else
        {
          tempTile = null;
        }
        if (null != tempTile)
        {
          biome = BiomeClassifier.getBiome (tempTile.getBiomeId ());
          for (Integer id : getEntity ().getHolder ().getHolder ().getTile 
                  (temp).getTileIds ())
          {
            if (biome.getGeneration ().getTiles ()[id].blocks (Direction.EAST))
            {
              blocking = true;
            }
          }
          if (!blocking)
          {
            neighbors.add (temp);
          }
        }
        
        blocking = false;
        temp = new Area (current.getX (), current.getY () + 1);
        if (null != getEntity ())
        {
          tempTile = getEntity ().getHolder ().getHolder ().getTile (temp);
        }
        else
        {
          tempTile = null;
        }
        if (null != tempTile)
        {
          biome = BiomeClassifier.getBiome (tempTile.getBiomeId ());
          for (Integer id : getEntity ().getHolder ().getHolder ().getTile 
                  (temp).getTileIds ())
          {
            if (biome.getGeneration ().getTiles ()[id].blocks (Direction.NORTH))
            {
              blocking = true;
            }
          }
          if (!blocking)
          {
            neighbors.add (temp);
          }
        }
        
        blocking = false;
        temp = new Area (current.getX (), current.getY () - 1);
        if (null != getEntity ())
        {
          tempTile = getEntity ().getHolder ().getHolder ().getTile (temp);
        }
        else
        {
          tempTile = null;
        }
        if (null != tempTile)
        {
          biome = BiomeClassifier.getBiome (tempTile.getBiomeId ());
          for (Integer id : getEntity ().getHolder ().getHolder ().getTile 
                  (temp).getTileIds ())
          {
            if (biome.getGeneration ().getTiles ()[id].blocks (Direction.SOUTH))
            {
              blocking = true;
            }
          }
          if (!blocking)
          {
            neighbors.add (temp);
          }
        }
        
        for (Area neighbor : neighbors)
        {
          if (!evaluated.contains (neighbor))
          {
            possibleGScore = gScore.get (current) + estimateDistance 
                    (current, neighbor);
            if (!next.contains (neighbor) || possibleGScore < gScore.get
                    (neighbor))
            {
              cameFrom.put (neighbor, current);
              gScore.put (neighbor, possibleGScore);
              fScore.put (neighbor, gScore.get (neighbor) + 
                      estimateDistance (neighbor, this.moveTo));
              if (!next.contains (neighbor) && 
                      estimateDistance (start, neighbor) < MAX_DISTANCE)
              {
                next.add (neighbor);
              }
            }
          }
        }
      }
    }
    
    if (foundEnd)
    {
      temp = cameFrom.get (current);
      while (null != temp)
      {
        found.addFirst (new Area (current.getX () - temp.getX (), 
                current.getY () - temp.getY ()));
        current = temp;
        temp = cameFrom.get (current);
      }
    }
    
    return found;
  }
  
  /**
   * Estimate the Distance between two areas
   * @param start start area
   * @param goal goal area
   * @return distance between two areas
   */
  private int estimateDistance (Area start, Area goal)
  {
    int xd = start.getX () - goal.getX ();
    int yd = start.getY () - goal.getY ();
    return (int) Math.sqrt (xd * xd + yd * yd);
  }

  /**
   * Control the render
   * @param rm render manager
   * @param vp view port
   */
  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    // does nothing. Overridden for abstract
  }
}
