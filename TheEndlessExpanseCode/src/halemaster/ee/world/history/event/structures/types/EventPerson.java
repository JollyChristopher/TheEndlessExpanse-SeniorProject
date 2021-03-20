package halemaster.ee.world.history.event.structures.types;

import halemaster.ee.localization.NameGen;
import halemaster.ee.world.Area;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.structures.EventObject;
import java.util.Random;

/**
 * @name EventPerson
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventPerson implements EventObject
{
  public static String ID = "person";
  public static String LEADER = "LEADER";
  public static String HERO = "HERO";
  public static String VILLAIN = "VILLAIN";
  public static String NPC = "NPC";
  public static String LOCATION = "LOCATION";
  private String name = "";
  private Random random;
  private Area location;
  private Event event;
  
  public void setEvent (Event event)
  {
    this.event = event;
  }
  
  public void init (Random random)
  {
    this.random = random;
  }

  public String getValue ()
  {
    return this.name;
  }
  
  public String getPrint ()
  {
    return this.name;
  }
  
  public void setValue (String value)
  {
    this.name = value;
  }
  
  public String getType ()
  {
    return ID;
  }
  
  public Area getLocation ()
  {
    return this.location;
  }

  public void modify (EventObject modifier, String type)
  {
    /*
     * LEADER - self
     * HERO - self
     * VILLAIN - self
     * NPC - self
     * LOCATION - area
     */
    if (LEADER.equals (type) && modifier == this)
    {
      // generate leader name
      this.name = NameGen.getName (random, "leader");
    }
    else if (HERO.equals (type) && modifier == this)
    {
      // generate hero name
      this.name = NameGen.getName (random, "hero");
    }
    else if (VILLAIN.equals (type) && modifier == this)
    {
      // generate villain name
      this.name = NameGen.getName (random, "villain");
    }
    else if (NPC.equals (type) && modifier == this)
    {
      // generate npc name
      this.name = NameGen.getName (random, "npc");
    }
    else if (LOCATION.equals (type) && modifier instanceof EventArea)
    {
      // set location
      EventArea area = (EventArea) modifier;
      this.location = area.getArea ();
    }
  }
}
