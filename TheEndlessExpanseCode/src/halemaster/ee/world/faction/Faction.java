package halemaster.ee.world.faction;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.state.Menu;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * @name Faction
 * 
 * @version 0.0.0
 * 
 * @date Nov 9, 2013
 */
public class Faction 
{
  public static final String FACTION_FOLDER = "/factions";
  private static Map<String, FactionHandler> factions =
          new HashMap<String, FactionHandler> ();
  private String name;
  private String leader;
  private transient FactionHandler handler;
  private Map<String, Integer> relations = new HashMap<String, Integer> ();
  private Map<String, Settlement> settlements = 
          new HashMap<String, Settlement> ();
  
  public Faction (String name, FactionHandler handler)
  {
    this.name = name;
    this.handler = handler;
  }
  
  /**
   * Change the diplomacy of this faction with another faction.
   * 
   * @param faction the faction to change diplomacy with.
   * @param diplomacy the diplomacy of the faction.
   */
  public void changeDiplomacy (Faction faction, int diplomacy)
  {
    this.relations.put (faction.getName (), diplomacy);
    DiplomaticGroup group = faction.getDiplomacy (this);
    DiplomaticGroup myGroup = getDiplomacy (faction);
    if (null == group)
    {
      faction.changeDiplomacy (this, diplomacy);
    }
    else if (group != myGroup)
    {
      faction.changeDiplomacy (this, (group.getLow () >= myGroup.getHigh ())
              ? (myGroup.getHigh ()) : (myGroup.getLow ()));
    }
  }
  
  public void modifyDiplomacy (Faction faction, int modify)
  {
    if (null != this.relations.get (faction.getName ()))
    {
      modify += this.relations.get (faction.getName ());
    }
    this.relations.put (faction.getName (), modify);
    DiplomaticGroup group = faction.getDiplomacy (this);
    DiplomaticGroup myGroup = getDiplomacy (faction);
    if (null == group)
    {
      faction.changeDiplomacy (this, modify);
    }
    else if (group != myGroup)
    {
      faction.changeDiplomacy (this, (group.getLow () >= myGroup.getHigh ())
              ? (myGroup.getHigh ()) : (myGroup.getLow ()));
    }
  }
  
  public void changeLeader (String leader)
  {
    this.leader = leader;
  }
  
  public String getLeader ()
  {
    return this.leader;
  }
  
  /**
   * Get the diplomacy of this faction with another faction.
   * 
   * @param faction the faction to get the diplomacy of.
   * @return the diplomacy between this faction and the other.
   */
  public DiplomaticGroup getDiplomacy (Faction faction)
  {
    Integer value = this.relations.get (faction.getName ());
    if (null == value)
    {
      return null;
    }
    return DiplomaticGroup.getGroup (value);
  }
  
  public Integer getRawDiplomacy (Faction faction)
  {
    return this.relations.get (faction.getName ());
  }
  
  public String getName ()
  {
    return this.name;
  }
  
  /**
   * Create a new settlement with the parameters for this Faction.
   * 
   * @param x x value of first area.
   * @param y y value of first area.
   * @param size population of first area.
   * @param name name of the settlement.
   */
  public void addSettlement (int x, int y, int size, String name)
  {
    Settlement settlement = new Settlement (name, this);
    settlement.setPopulation (size);
    settlement.addArea (x, y);
    this.settlements.put (name, settlement);
    if (null != this.handler)
    {
      this.handler.addSettlement (x, y, settlement);
    }
  }
  
  public void addSettlementArea (Settlement settlement, int x, int y)
  {
    if (null != this.handler)
    {
      this.handler.addSettlement (x, y, settlement);
    }
  }
  
  public Settlement getSettlement (String name)
  {
    return this.settlements.get (name);
  }
  
  public void removeSettlement (String name)
  {
    Settlement settle = this.settlements.remove (name);
    if (null != this.handler)
    {
      for (Area area : settle.getAreas ())
      {
        this.handler.removeSettlement (area.getX (), area.getY ());
      }
    }
  }
  
  public void removeSettlementArea (int x, int y)
  {
    this.handler.removeSettlement (x, y);
  }
  
  /**
   * Get all settlements in this faction.
   * 
   * @return array of settlements in this faction.
   */
  public Settlement[] getSettlements ()
  {
    Settlement[] settlementArray = new Settlement[this.settlements.size ()];
    this.settlements.values ().toArray (settlementArray);
    
    return settlementArray;
  }
  
  /**
   * Save this faction in the given world folder.
   * 
   * @param world name of the world to save in. 
   */
  public void saveFaction (String world) throws IOException
  {
    if (null == world)
    {
      throw new NullPointerException ("world");
    }
    new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER + "/" + world + 
            FACTION_FOLDER).mkdirs ();
    Json.saveJson (new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + world + FACTION_FOLDER + "/" + getName ()), this);
  }
  
  /**
   * Load all factions for the given world into the handler.
   * 
   * @param world world name to load.
   * @param handler handler to load into.
   * @return array of factions from the handler.
   */
  public static Faction[] load (String world, FactionHandler handler)
          throws IOException
  {
    Faction[] loaded;
    ArrayList<Faction> factionList = new ArrayList<Faction> ();
    File worldFolder = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER + "/" + world + FACTION_FOLDER);
    
    if (worldFolder.exists ())
    {
      for (File file : worldFolder.listFiles ())
      {
        loaded = Json.getFromFile (file, Faction.class);
        factionList.addAll (Arrays.asList (loaded));
      }
    }
    
    for (Faction faction : factionList)
    {
      handler.addFaction (faction);
    }
    
    return handler.getFactions ();
  }
  
  public FactionHandler getHandler ()
  {
    return this.handler;
  }
  
  public static void save (String world, FactionHandler handler)
          throws IOException
  {
    Menu.emptyDirectory (new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER + "/" + world + FACTION_FOLDER));
    for (Faction faction : Faction.getFactions (world))
    {
      faction.saveFaction (world);
    }
  }
  
  public static void save (String world) throws IOException
  {
    if (factions.containsKey (world))
    {
      save (world, factions.get (world));
    }
  }
  
  /**
   * Load all factions from the given world into the main handler.
   * 
   * @param world world to load for.
   * @return array of factions from the main Handler after load.
   */
  public static Faction[] load (String world) throws IOException
  {
    if (!factions.containsKey (world))
    {
      factions.put (world, new FactionHandler ());
    }
    return load (world, factions.get (world));
  }
  
  /**
   * Add a new faction with the given name to the main faction handler.
   * 
   * @param name name of new faction.
   */
  public static void addFaction (String world, String name) throws IOException
  {
    if (!factions.containsKey (world))
    {
      load (world);
    }
    factions.get(world).addFaction (name);
  }
  
  /**
   * get the faction with the given name from the main handler.
   * 
   * @param name name of faction.
   * @return faction of the given name.
   */
  public static Faction getFaction (String world, String name) 
          throws IOException
  {
    if (!factions.containsKey (world))
    {
      load (world);
    }
    return factions.get (world).getFaction (name);
  }
  
  /**
   * Get all factions from the main handler.
   * 
   * @return array of all factions from the main handler.
   */
  public static Faction[] getFactions (String world) throws IOException
  {
    if (!factions.containsKey (world))
    {
      load (world);
    }
    return factions.get(world).getFactions ();
  }
  
  public static void removeFaction (String world, String name) 
          throws IOException
  {
    if (!factions.containsKey (world))
    {
      load (world);
    }
    factions.get(world).remove (name);
  }
  
  public static void resetFactions ()
  {
    factions.clear ();
  }
  
  public static boolean settlementAt (String world, int x, int y)
  {
    try
    {
      if (!factions.containsKey (world))
      {
        load (world);
      }
      return null != factions.get (world).getSettlement (x, y);
    }
    catch (IOException e)
    {
      // log
    }
    return false;
  }
  
  public static List<Area> settlementLocations (String world)
  {
    List<Area> locs = new ArrayList<Area> ();
    List<Settlement> settles;
    
    try
    {
      if (!factions.containsKey (world))
      {
        load (world);
      }
      settles = factions.get (world).getSettlements ();
      for (Settlement settle : settles)
      {
        locs.addAll (Arrays.asList (settle.getAreas ()));
      }
    }
    catch (IOException e)
    {
      // log
    }
    return locs;
  }
  
  public static Area getNewCenter (String world, Random random, 
          Biome[][] biomes)
  {
    Area area = null;
    Area currentPoint = new Area (random.nextInt (biomes.length), 
            random.nextInt (biomes.length));
    Area nextPoint;
    Queue<Area> next = new ArrayDeque<Area> ();
    Set<Area> visitedUsable = new HashSet<Area> ();
    Set<Area> visitedUnusable = new HashSet<Area> ();
    FactionHandler handler = factions.get (world);
    
    while (null == area)
    {
      if (0 < biomes[currentPoint.getX ()][currentPoint.getY ()]
              .getHabituality ())
      {
        visitedUsable.add (currentPoint);
      }
      else
      {
        visitedUnusable.add (currentPoint);
      }
      
      nextPoint = new Area (currentPoint.getX () + Settlement.MOVEMENT, 
              currentPoint.getY ());
      if (0 <= nextPoint.getX () && biomes.length > nextPoint.getX () &&
              0 <= nextPoint.getY () && biomes.length > nextPoint.getY () &&
              !visitedUsable.contains (nextPoint) && 
              !visitedUnusable.contains (nextPoint) && 
              !next.contains (nextPoint) && null == handler
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        next.add (nextPoint);
      }
      nextPoint = new Area (currentPoint.getX () - Settlement.MOVEMENT, 
              currentPoint.getY ());
      if (0 <= nextPoint.getX () && biomes.length > nextPoint.getX () &&
              0 <= nextPoint.getY () && biomes.length > nextPoint.getY () &&
              !visitedUsable.contains (nextPoint) && 
              !visitedUnusable.contains (nextPoint) && 
              !next.contains (nextPoint) && null == handler
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        next.add (nextPoint);
      }
      nextPoint = new Area (currentPoint.getX (), 
              currentPoint.getY () - Settlement.MOVEMENT);
      if (0 <= nextPoint.getX () && biomes.length > nextPoint.getX () &&
              0 <= nextPoint.getY () && biomes.length > nextPoint.getY () &&
              !visitedUsable.contains (nextPoint) && 
              !visitedUnusable.contains (nextPoint) && 
              !next.contains (nextPoint) && null == handler
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        next.add (nextPoint);
      }
      nextPoint = new Area (currentPoint.getX (), 
              currentPoint.getY () + Settlement.MOVEMENT);
      if (0 <= nextPoint.getX () && biomes.length > nextPoint.getX () &&
              0 <= nextPoint.getY () && biomes.length > nextPoint.getY () &&
              !visitedUsable.contains (nextPoint) && 
              !visitedUnusable.contains (nextPoint) && 
              !next.contains (nextPoint) && null == handler
              .getSettlement (nextPoint.getX (), nextPoint.getY ()))
      {
        next.add (nextPoint);
      }
      
      for (Area usable : visitedUsable)
      {
        if (random.nextInt (Byte.MAX_VALUE) < 
                biomes[usable.getX ()][usable.getY ()].getHabituality ())
        {
          area = usable;
        }
      }
      
      if (next.isEmpty ())
      {
        int newX = currentPoint.getX () + 1;
        int newY = currentPoint.getY () + 1;
        
        if (newX >= biomes.length)
        {
          newX -= 2;
        }
        if (newY >= biomes.length)
        {
          newY -= 2;
        }
        
        nextPoint = new Area (newX, newY);
        next.add (nextPoint);
      }
      
      currentPoint = next.poll ();
    }
    
    return area;
  }
}
