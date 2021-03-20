package halemaster.ee.world.history.event.structures;

import halemaster.ee.world.history.event.Event;
import java.util.Random;

/**
 * @name EventObject
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public interface EventObject 
{
  public void setEvent (Event event);
  public void init (Random random);
  public String getValue ();
  public void setValue (String value);
  public String getPrint ();
  public String getType ();
  public void modify (EventObject modifier, String type);
}
