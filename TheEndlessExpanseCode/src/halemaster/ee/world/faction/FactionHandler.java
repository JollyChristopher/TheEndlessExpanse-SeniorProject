package halemaster.ee.world.faction;

import halemaster.ee.world.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name FactionHandler
 * 
 * @version 0.0.0
 * 
 * @date Nov 9, 2013
 */
public class FactionHandler 
{
  private Map<String, Faction> factions = new HashMap<String, Faction> ();
  private Map<Area, Settlement> cityLocations = 
          new HashMap<Area, Settlement>();
  
  /**
   * Get the faction with the given name from this handler.
   * 
   * @param name name of the faction.
   * @return the faction with the given name.
   */
  public Faction getFaction (String name)
  {
    return factions.get (name);
  }

  /**
   * Get all factions of this handler.
   * 
   * @return array of factions.
   */
  public Faction[] getFactions ()
  {
    Faction[] factionArray = new Faction[this.factions.size ()];
    this.factions.values ().toArray (factionArray);
    
    return factionArray;
  }

  /**
   * Add a new faction to this handler.
   * 
   * @param faction add the faction to the handler.
   */
  public void addFaction (String name)
  {
    Faction faction = new Faction (name, this);
    addFaction (faction);
  }
  
  /**
   * Add a new faction to this handler.
   * 
   * @param faction add the faction to the handler.
   */
  public void addFaction (Faction faction)
  {
    if (!this.factions.containsKey (faction.getName ()))
    {
      for (Faction temp : getFactions ())
      {
        faction.changeDiplomacy (temp, 0);
      }
      
      for (Settlement settle : faction.getSettlements ())
      {
        for (Area loc : settle.getAreas ())
        {
          this.cityLocations.put (loc, settle);
        }
      }

      this.factions.put (faction.getName (), faction);
    }
  }
  
  public void remove (String name)
  {
    Faction faction = this.factions.remove (name);
    for (Settlement settle : faction.getSettlements ())
    {
      faction.removeSettlement (settle.getName ());
    }
  }
  
  public void addSettlement (int x, int y, Settlement settlement)
  {
    this.cityLocations.put (new Area (x, y), settlement);
  }
  
  public Settlement getSettlement (int x, int y)
  {
    return this.cityLocations.get (new Area (x, y));
  }
  
  public List<Settlement> getSettlements ()
  {
    List<Settlement> settles = new ArrayList<Settlement>();
    
    settles.addAll (this.cityLocations.values ());
    
    return settles;
  }
  
  public void removeSettlement (int x, int y)
  {
    this.cityLocations.remove (new Area (x, y));
  }
}
