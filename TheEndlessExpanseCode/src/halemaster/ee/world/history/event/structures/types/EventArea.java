package halemaster.ee.world.history.event.structures.types;

import halemaster.ee.world.Area;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.structures.EventObject;
import java.util.Random;

/**
 * @name EventZone
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventArea implements EventObject
{
  public static final String ID = "area";
  public static final String GROW = "GROW";
  public static final String CENTER = "CENTER";
  private int x, y;
  private Event event;
  
  public void setEvent (Event event)
  {
    this.event = event;
  }
  
  public void init (Random random)
  {
  }

  public String getValue ()
  {
    return String.valueOf (this.x)+","+String.valueOf(this.y);
  }
  
  public String getPrint ()
  {
    return getValue();
  }
  
  public void setValue (String value)
  {
    String[] values = value.split(",");
    this.x = Integer.valueOf (values[0]);
    this.y = Integer.valueOf (values[1]);
  }
  
  public String getType ()
  {
    return ID;
  }
  
  public Area getArea ()
  {
    return new Area (this.x, this.y);
  }

  public void modify (EventObject modifier, String type)
  {
    /*
     * CENTER - with settlement as modifier
     * GROW   - with settlement as modifier
     */
    EventSettlement settle;
    Area growth, center;
    
    if (modifier instanceof EventSettlement && type.equals (GROW))
    {
      settle = (EventSettlement) modifier;
      growth = settle.getAvailableGrowth ();
      if (null != growth)
      {
        this.x = growth.getX ();
        this.y = growth.getY ();
        settle.addGrowth (this.x, this.y);

        this.event.setLocation (this.x, this.y);
      }
    }
    else if (modifier instanceof EventSettlement && type.equals (CENTER))
    {
      settle = (EventSettlement) modifier;
      center = settle.findAvailableCenter ();
      this.x = center.getX ();
      this.y = center.getY ();
      settle.setCenter (this.x, this.y);
      
      this.event.setLocation (this.x, this.y);
    }
  }
}
