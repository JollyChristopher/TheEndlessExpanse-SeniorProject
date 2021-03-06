package halemaster.ee.world.faction;

import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * @name Settlement
 * 
 * @version 0.0.0
 * 
 * @date Nov 9, 2013
 */
public class Settlement 
{
  public static final int MOVEMENT = 50;
  private String name;
  private transient Faction faction;
  private int population;
  private Set<Area> areas = new HashSet<Area> ();
  private HashMap<Area, List<String>> npcs = new HashMap<Area, List<String>>();
    
  public Settlement (String name, Faction faction)
  {
    if (null == name)
    {
      throw new NullPointerException ("name");
    }
    
    this.name = name;
    this.faction = faction;
  }
  public String getName ()
  {
    return this.name;
  }

  public Faction getFaction ()
  {
    return this.faction;
  }

  public int getPopulation ()
  {
    return this.population;
  }

  public void setPopulation (int size)
  {
    this.population = size;
  }
  
  public void addNPC (String name, Area location)
  {
    List<String> npcsAtLoc = this.npcs.get (location);
    if (null == npcsAtLoc)
    {
      npcsAtLoc = new ArrayList<String>();
      this.npcs.put (location, npcsAtLoc);
    }
    
    npcsAtLoc.add (name);
  }
  
  public void removeNPC (String name)
  {
    for (Entry<Area, List<String>> area : this.npcs.entrySet ())
    {
      area.getValue ().remove (name);
    }
  }
  
  public List<String> getNPCs (Area location)
  {
    return this.npcs.get (location);
  }

  /**
   * Get all areas this settlement is made of.
   * 
   * @return array of areas this settlement spans.
   */
  public Area[] getAreas ()
  {
    Area[] areaArray = new Area[this.areas.size ()];
    this.areas.toArray (areaArray);
    return areaArray;
  }
  
  /**
   * Add a new area for this settlement. If the area already exists, it is not
   * added a second time.
   * 
   * @param x x of the area.
   * @param y y of the area.
   */
  public void addArea (int x, int y)
  {
    Area area = new Area (x, y);
    this.areas.add (area);
    if (null != this.faction)
    {
      this.faction.addSettlementArea (this, x, y);
    }
  }

  /**
   * Remove the area from this settlment at the given coordinates.
   * 
   * @param x x of the area.
   * @param y y of the area.
   * @return the area at the given location, or null if it isn't in this 
   *         Settlement.
   */
  public Area removeArea (int x, int y)
  {
    Area removed = null;
    Area tempArea = new Area (x, y);
    
    for (Area area : getAreas ())
    {
      if (area.equals (tempArea))
      {
        removed = area;
        this.areas.remove (removed);
        if (null != this.faction)
        {
          this.faction.removeSettlementArea (removed.getX (), removed.getY ());
        }
      }
    }
    
    return removed;
  }
  
  public Area getGrowth (Random random, Biome[][] biomes)
  {
    Area area = null;
    Area nextPoint;
    List<Area> visitedUsable = new ArrayList<Area> ();
    Set<Area> visitedUnusable = new HashSet<Area> ();
    
    for (Area temp : this.areas)
    {
      visitedUnusable.add (temp);
    }
    
    for (Area temp : this.areas)
    {
      nextPoint = new Area (temp.getX () + 1, temp.getY ());
      if (0 <= nextPoint.getX () && 0 <= nextPoint.getY () 
              && biomes.length > nextPoint.getX () 
              && biomes.length > nextPoint.getY ()&&
              0 < biomes[nextPoint.getX ()][nextPoint.getY ()].getHabituality ()
              && !visitedUnusable.contains (nextPoint)
              && null == this.faction.getHandler ()
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        visitedUsable.add (nextPoint);
      }
      nextPoint = new Area (temp.getX () - 1, temp.getY ());
      if (0 <= nextPoint.getX () && 0 <= nextPoint.getY () 
              && biomes.length > nextPoint.getX () 
              && biomes.length > nextPoint.getY () &&
              0 < biomes[nextPoint.getX ()][nextPoint.getY ()].getHabituality ()
              && !visitedUnusable.contains (nextPoint)
              && null == this.faction.getHandler ()
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        visitedUsable.add (nextPoint);
      }
      nextPoint = new Area (temp.getX (), temp.getY () + 1);
      if (0 <= nextPoint.getX () && 0 <= nextPoint.getY () 
              && biomes.length > nextPoint.getX () 
              && biomes.length > nextPoint.getY () &&
              0 < biomes[nextPoint.getX ()][nextPoint.getY ()].getHabituality ()
              && !visitedUnusable.contains (nextPoint)
              && null == this.faction.getHandler ()
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        visitedUsable.add (nextPoint);
      }
      nextPoint = new Area (temp.getX (), temp.getY () - 1);
      if (0 <= nextPoint.getX () && 0 <= nextPoint.getY () 
              && biomes.length > nextPoint.getX () 
              && biomes.length > nextPoint.getY () &&
              0 < biomes[nextPoint.getX ()][nextPoint.getY ()].getHabituality ()
              && !visitedUnusable.contains (nextPoint)
              && null == this.faction.getHandler ()
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        visitedUsable.add (nextPoint);
      }
    }
    
    if (!visitedUsable.isEmpty ())
    {
      area = visitedUsable.get (random.nextInt (visitedUsable.size ()));
    }
    
    return area;
  }
  
  /**
   * Determine if equals another Settlement.
   * 
   * @param obj object to compare to.
   * @return if the object equals this object.
   */
  @Override
  public boolean equals (Object obj)
  {
    if (obj instanceof Settlement)
    {
      Settlement set = (Settlement) obj;
      
      return (set.getName ().equals (getName ()) && 
              getFaction () == set.getFaction ());
    }
    else
    {
      return false;
    }
  }

  /**
   * Get hashcode fo this Settlement. Used for the above equals.
   * 
   * @return hashcode for this Settlement.
   */
  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + (this.name != null ? this.name.hashCode () : 0);
    hash = 53 * hash + (this.faction != null ? this.faction.hashCode () : 0);
    return hash;
  }
}
