package halemaster.ee.world.history.event;

import halemaster.ee.localization.Localizer;
import halemaster.ee.world.Area;
import halemaster.ee.world.history.HistoryGenerator;
import halemaster.ee.world.history.event.structures.EventObject;
import halemaster.ee.world.history.event.structures.EventObjectType;
import halemaster.ee.world.history.event.type.EventType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @name Event
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class Event 
{
  public static final int UNLOCKED = -1;
  
  private transient EventType type;
  private transient int date;
  private transient Area location;
  private transient int lockDate = UNLOCKED;
  private transient Map<String, EventObject> variables = 
          new HashMap<String, EventObject>();
  private transient EventHolder holder;
  private transient boolean settled = true;
  
  private String unshakenTypeId = null;
  private Integer unshakenX = null;
  private Integer unshakenY = null;
  private Map<String, String> unshakenVariables = null;
  private Integer unshakenDate = null;
  private Integer unshakenLockDate = null;

  public boolean isSettled ()
  {
    return settled;
  }

  public void setSettled (boolean settled)
  {
    this.settled = settled;
  }
  
  /**
   * Get the localized description of this event based upon values and the date.
   * 
   * @return localized description
   */
  public String getDescription ()
  {
    String description = "event." + this.type.getId ();
    String[] arguments = new String[this.variables.size () + 1];
    List<String> varNames = new ArrayList<String>();
    String season = "event.season.one";
    
    for (String varName : this.variables.keySet ())
    {
      varNames.add (varName);
    }
    
    Collections.sort (varNames);
    
    for (int i = 0; i < varNames.size (); i++)
    {
      arguments[i] = this.variables.get (varNames.get (i)).getPrint ();
    }
    
    switch (this.date % HistoryGenerator.SEASON_COUNT)
    {
      case 0:
        season = "event.season.one";
        break;
      case 1:
        season = "event.season.two";
        break;
      case 2:
        season = "event.season.three";
        break;
      case 3:
        season = "event.season.four";
        break;
    }
    
    arguments[arguments.length - 1] = Localizer.getString (season, 
            this.date / HistoryGenerator.SEASON_COUNT);
    
    description = Localizer.getString (description, (Object[]) arguments);
    
    return description;
  }
  
  /**
   * Shake unshaken values in order to fill out the event with the given values. 
   */
  public void shake () throws InstantiationException, 
          IllegalAccessException
  {
    EventObject obj;
    
    if (null != unshakenDate)
    {
      setDate (unshakenDate);
      unshakenDate = null;
    }
    if (null != unshakenLockDate)
    {
      setLock (unshakenLockDate);
      unshakenLockDate = null;
    }
    if (null != unshakenTypeId)
    {
      setType (this.holder.getType (unshakenTypeId));
      unshakenTypeId = null;
    }
    if (null != unshakenX && null != unshakenY)
    {
      setLocation (unshakenX, unshakenY);
      unshakenX = null;
      unshakenY = null;
    }
    if (null != unshakenVariables)
    {
      for (Entry<String, String> var : unshakenVariables.entrySet ())
      {
        obj = EventObjectType.getByType 
                (this.type.getVariableType (var.getKey ()))
                .getObject (var.getValue ());
        if (null == obj)
        {
          obj = EventObjectType.getByType 
                  (this.type.getVariableType (var.getKey ()))
                .getSetObject (var.getValue ());          
          obj.setEvent (this);
        }
        addVariable (var.getKey (), obj);
      }
      
      unshakenVariables = null;
    }
  }
  
  /**
   * Unshake the Event in order to save it to the file.
   */
  public void unShake ()
  {
    unshakenTypeId = getType ().getId ();
    unshakenX = getLocation ().getX ();
    unshakenY = getLocation ().getY ();
    unshakenDate = getDate ();
    unshakenLockDate = getLockDate ();
    unshakenVariables = new HashMap<String, String>();
    for (Entry<String, EventObject> var : this.variables.entrySet ())
    {
      unshakenVariables.put (var.getKey (), var.getValue ().getValue ());
    }
  }
  
  public void setHolder (EventHolder holder)
  {
    this.holder = holder;
  }
  
  public EventHolder getHolder ()
  {
    return this.holder;
  }
  
  public int getLockDate ()
  {
    return this.lockDate;
  }
  
  public void setLock (int locked)
  {
    this.lockDate = locked;
  }

  public EventType getType ()
  {
    return type;
  }

  public void setType (EventType type)
  {
    this.type = type;
  }

  public int getDate ()
  {
    return date;
  }

  public void setDate (int date)
  {
    this.date = date;
  }

  public Area getLocation ()
  {
    return location;
  }
  
  public void setLocation (int x, int y)
  {
    this.location = new Area (x, y);
  }
  
  public void addVariable (String name, EventObject object)
  {
    this.variables.put (name, object);
  }
  
  public EventObject getVariable (String name)
  {
    EventObject obj = this.variables.get (pathVar (this.type, name));
    
    return obj;
  }
  
  /**
   * Turn a variable from the counts as tree into a variable name usable by this
   * event.
   * 
   * @param thisType event type we are currently at in the tree
   * @param variable name of the variable that is possibly from this type.
   * @return name of that variable reevaluated as a variable from this object.
   */
  private String pathVar (EventType thisType, String variable)
  {
    String path = null;
    String[] counts;
    int countId = 0;
    
    for (String var : thisType.getVariables ())
    {
      if (var.equals (variable))
      {
        path = var;
      }
    }
    
    if (null == path)
    {
      counts = thisType.getCountsIds ();
      for (int i = 0; null == path && i < counts.length; i++)
      {
        if (null != this.holder)
        {
          path = pathVar(this.holder.getType (counts[i]), variable);
        }
        else
        {
          path = null;
        }
        if (null != path)
        {
          countId = i;
        }
      }
      
      if (null != path)
      {
        path = thisType.getCounts (counts[countId]).get (path);
      }
    }
    
    return path;
  }
  
  @Override
  public boolean equals (Object other)
  {
    if (other instanceof Event)
    {
      return this.variables.equals (((Event) other).variables);
    }
    return false;
  }

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 23 * hash + (this.variables != null ? this.variables.hashCode () : 0);
    return hash;
  }
}
