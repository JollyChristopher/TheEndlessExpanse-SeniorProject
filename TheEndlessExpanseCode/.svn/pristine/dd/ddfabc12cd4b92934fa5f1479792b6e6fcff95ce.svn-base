package halemaster.ee.world.history.event.structures.types;

import halemaster.ee.world.Area;
import halemaster.ee.world.faction.Faction;
import halemaster.ee.world.faction.Settlement;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.structures.EventObject;
import java.io.IOException;
import java.util.Random;

/**
 * @name EventSettlement
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventSettlement implements EventObject
{
  public static final String ADD_NPC = "ADD_NPC";
  public static final String ADD_NPC_FARMER = "ADD_NPC_FARMER";
  public static final String ADD_NPC_BLACKSMITH = "ADD_NPC_BLACKSMITH";
  public static final String ADD_NPC_MAYOR = "ADD_NPC_MAYOR";
  public static final String ADD_NPC_SHOPKEEPER = "ADD_NPC_SHOPKEEPER";
  public static final String ADD_NPC_PRIEST = "ADD_NPC_PRIEST";
  public static final String ADD_NPC_GUARD = "ADD_NPC_GUARD";
  public static final String ADD_NPC_FISHER = "ADD_NPC_FISHER";
  public static final String ADD_NPC_LEATHER = "ADD_NPC_LEATHERWORKER";
  public static final String ADD_NPC_BEGGAR = "ADD_NPC_BEGGAR";
  public static final String ADD_NPC_NOBLE = "ADD_NPC_NOBLE";
  public static final String REMOVE_NPC = "REMOVE_NPC";
  public static final String ID = "settlement";
  public static final String SEPARATOR = ";";
  public static final int SPACING = 10;
  public static final int HABITUALITY_SPACE = 5;
  public static final int SIZE_PER_GROWTH_MAX = 256;
  public static final int SIZE_PER_GROWTH_MIN = 64;
  private String name;
  private Event event;
  private Random random;
  private Area center = Area.ANYWHERE;
  private String faction;
  private int size = 1;
  
  public void setEvent (Event event)
  {
    this.event = event;
  }
  
  public void init (Random random)
  {
    // get a random name here
    this.random = random;
    this.size = random.nextInt (SIZE_PER_GROWTH_MAX - SIZE_PER_GROWTH_MIN) 
            + SIZE_PER_GROWTH_MIN;
  }

  public String getValue ()
  {
    String value = this.name + SEPARATOR + this.center.getX () + SEPARATOR + 
            this.center.getY () + SEPARATOR + this.size + SEPARATOR + 
            this.faction;
    
    return value;
  }
  
  public String getPrint ()
  {
    return this.name;
  }
  
  public void setValue (String value)
  {
    int x, y;
    String[] values = value.split (SEPARATOR);
    
    this.name = values[0];
    x = Integer.valueOf (values[1]);
    y = Integer.valueOf (values[2]);
    this.size = Integer.valueOf (values[3]);
    this.center = new Area (x, y);
    this.faction = values[4];
  }
  
  public String getType ()
  {
    return ID;
  }

  public void modify (EventObject modifier, String type)
  {
    /*
     * ADD_NPC* - person
     * REMOVE_NPC - person
     */
    try
    {
      if (ADD_NPC.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_FARMER.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_BLACKSMITH.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_MAYOR.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_SHOPKEEPER.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_PRIEST.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_GUARD.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_FISHER.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_LEATHER.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_BEGGAR.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (ADD_NPC_NOBLE.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.addNPC (person.getPrint (), person.getLocation ());
      }
      else if (REMOVE_NPC.equals (type) && modifier instanceof EventPerson)
      {
        EventPerson person = (EventPerson) modifier;
        Settlement settle = Faction.getFaction (this.event.getHolder ()
              .getWorldName (), this.faction).getSettlement (this.name);
        settle.removeNPC (person.getPrint ());
      }
    }
    catch (IOException e)
    {
      // log some
    }
    catch (NullPointerException e)
    {
      // log some
    }
  }
  
  public void setName (String name)
  {
    this.name = name;
  }
  
  public void setFaction (String faction)
  {
    this.faction = faction;
  }
  
  public Area getAvailableGrowth ()
  {
    Area area = null;
    try
    {
      Settlement settle = Faction.getFaction (this.event.getHolder ()
            .getWorldName (), this.faction).getSettlement (this.name);
      area = settle.getGrowth (this.random, 
              this.event.getHolder ().getWorld ());
    }
    catch (IOException e)
    {
      // log some
    }
    return area;
  }
  
  public Area findAvailableCenter ()
  {
    Area area;
    area = Faction.getNewCenter (
            this.event.getHolder ().getWorldName (), this.random, 
            this.event.getHolder ().getWorld ());
    return area;
  }
  
  public Area getCenter ()
  {
    return this.center;
  }
  
  public void setCenter (int x, int y)
  {
    this.center = new Area (x, y);
  }
  
  public Area[] getAreas ()
  {
    Area[] area = null;
    try
    {
      Settlement settle = Faction.getFaction (this.event.getHolder ()
            .getWorldName (), this.faction).getSettlement (this.name);
      area = settle.getAreas ();
    }
    catch (IOException e)
    {
      // log some
    }
    return area;
  }
  
  public void addGrowth (int x, int y)
  {
    
    this.size += this.size = random.nextInt (SIZE_PER_GROWTH_MAX - 
            SIZE_PER_GROWTH_MIN) + SIZE_PER_GROWTH_MIN;
    try
    {
      Settlement settle = Faction.getFaction (this.event.getHolder ()
            .getWorldName (), this.faction).getSettlement (this.name);
      settle.addArea (x, y);
      settle.setPopulation (this.size);
    }
    catch (IOException e)
    {
      // log some
    }
  }
  
  public int getSize ()
  {
    return this.size;
  }
}
