/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package halemaster.ee.world.history.event.structures.types;

import halemaster.ee.localization.NameGen;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.structures.EventObject;
import java.util.Random;

/**
 *
 * @author Halemaster
 */
public class EventBattle implements EventObject
{
  public static final String ID = "battle";
  private String name = "";
  private Event event;
  
  public void setEvent (Event event)
  {
    this.event = event;
  }
  
  public void init (Random random)
  {
    this.name = NameGen.getName (random, ID);
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

  public void modify (EventObject modifier, String type)
  {
    
  }
}
