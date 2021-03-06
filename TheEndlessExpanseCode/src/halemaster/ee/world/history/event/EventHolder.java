package halemaster.ee.world.history.event;

import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.history.event.structures.Tree;
import halemaster.ee.world.history.event.type.EventType;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @name EventHolder
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventHolder 
{
  private class LockList
  {
    public Set<Event> list = new HashSet<Event>();
    
    public List<Event> getList (boolean locked, int date)
    {
      List<Event> events = new ArrayList<Event> ();
      for (Event event : this.list)
      {
        if (locked && event.getLockDate () <= date &&
                event.getLockDate () != Event.UNLOCKED)
        {
          events.add (event);
        }
        else if (!locked && (event.getLockDate () > date || 
                event.getLockDate () == Event.UNLOCKED))
        {
          events.add (event);
        }
      }
      
      return events;
    }
    
    public Set<Event> getList ()
    {
      return this.list;
    }
    
    public void addEvent (Event event)
    {
      this.list.add (event);
    }
    
    public void removeEvent (Event event)
    {
      this.list.remove (event);
    }
  }
  
  private class TreeHolder
  {
    public LockList anywhere = new LockList();
    public Tree<Integer, Tree<Integer, LockList>> eventLocations;
  }
  
  private Tree <Integer, TreeHolder> events = new Tree<Integer, TreeHolder>();
  private Set<EventTypeHolder> typeHolders = new HashSet<EventTypeHolder>();
  private Biome[][] biomes;
  private String worldName;
  
  public EventHolder (String world, Biome[][] biomes)
  {
    this.worldName = world;
    this.biomes = biomes;
  }
  
  public EventHolder (String world)
  {
    this (world, null);
  }
  
  public String getWorldName ()
  {
    return this.worldName;
  }
  
  public Biome[][] getWorld ()
  {
    return this.biomes;
  }
  
  public int getWorldSize ()
  {
    return this.biomes.length;
  }
  
  /**
   * Add a new type holder to this event holder for reference.
   * 
   * @param holder holder to add.
   */
  public void addTypeHolder (EventTypeHolder holder)
  {
    this.typeHolders.add (holder);
  }
  
  public Set<EventTypeHolder> getTypeHolders ()
  {
    return this.typeHolders;
  }
  
  /**
   * get the type from one of the holders from the id.
   * 
   * @param id id to look for.
   * @return type found.
   */
  public EventType getType (String id)
  {
    EventType type = null;
    
    for (EventTypeHolder temp : this.typeHolders)
    {
      if (null == type)
      {
        type = temp.getType (id);
      }
    }
    
    return type;
  }
  
  public boolean hasEventsAt (Area area)
  {
    boolean eventsAt = false;
    
    for (TreeHolder tree : this.events.get (0, Integer.MAX_VALUE))
    {
      if (null != tree.eventLocations.get (area.getX ()))
      {
        if (null != tree.eventLocations.get (area.getX ()).get (area.getY ()))
        {
          eventsAt = true;
        }
      }
    }
    
    return eventsAt;
  }
  
  /**
   * Get all events, either locked or unlocked, from the given parameters
   * 
   * @param begin begin date
   * @param end end date
   * @param left left x
   * @param right right x
   * @param top top y
   * @param bottom bottom y
   * @param locked whether events are locked
   * @return all events in the given area.
   */
  public Event[] getEvents (int begin, int end, int left, int right, int top, 
          int bottom, boolean locked)
  {
    Event[] foundEvents = new Event[0];
    List<Event> listEvents = new ArrayList<Event>();
    List<TreeHolder> dates = null;
    List<Tree<Integer, LockList>> xLocations;
    List<LockList> yLocations;
    boolean concurrentWait;
    
    // Wait until modifying threads are no longer modifying
    do
    {
      concurrentWait = false;
      try
      {
        dates = this.events.get (begin, end);
      }
      catch (ConcurrentModificationException e)
      {
        concurrentWait = true;
      }
    } while (concurrentWait);
    
    for (TreeHolder date : dates)
    {
      listEvents.addAll (date.anywhere.getList (locked, end));
      xLocations = date.eventLocations.get (left, right);
      for (Tree<Integer, LockList> xLocation : xLocations)
      {
        yLocations = xLocation.get (top, bottom);
        for (LockList yLocation : yLocations)
        {
          listEvents.addAll (yLocation.getList (locked, end));
        }
      }
    }
    
    foundEvents = listEvents.toArray (foundEvents);
    
    return foundEvents;
  }
  
  /**
   * Find all events within the associated parameters.
   * Although code seems copied and pasted, this version does not loop through
   * the trees twice (one for locked and one for unlocked), thus saving time
   * as opposed to just calling the above function twice.
   * 
   * @param begin
   * @param end
   * @param left
   * @param right
   * @param top
   * @param bottom
   * @return 
   */
  public Event[] getEvents (int begin, int end, int left, int right, int top, 
          int bottom)
  {
    Event[] foundEvents = new Event[0];
    List<Event> listEvents = new ArrayList<Event>();
    List<TreeHolder> dates;
    List<Tree<Integer, LockList>> xLocations;
    List<LockList> yLocations;
    
    dates = this.events.get (begin, end);
    
    for (TreeHolder date : dates)
    {
      listEvents.addAll (date.anywhere.getList ());
      xLocations = date.eventLocations.get (left, right);
      for (Tree<Integer, LockList> xLocation : xLocations)
      {
        yLocations = xLocation.get (top, bottom);
        for (LockList yLocation : yLocations)
        {
          listEvents.addAll (yLocation.getList ());
        }
      }
    }
    
    foundEvents = listEvents.toArray (foundEvents);
    
    return foundEvents;
  }
  
  /**
   * Add a new event with the given locked condition to the holder.
   * 
   * @param event event to add.
   * @param locked locked condition of the event.
   */
  public void addEvent (Event event)
  {
    TreeHolder date;
    Tree<Integer, LockList> xLocation;
    LockList yLocation;
    
    date = this.events.get (event.getDate ());
    
    if (null == date)
    {
      date = new TreeHolder ();
      date.eventLocations = new Tree<Integer, Tree<Integer, LockList>>();
      date.anywhere = new LockList();
      if (Area.ANYWHERE.equals(event.getLocation ()))
      {
        date.anywhere.addEvent (event);
      }
      else
      {
        xLocation = new Tree<Integer, LockList>();
        yLocation = new LockList();
        yLocation.addEvent (event);
        xLocation.put (event.getLocation ().getY (), yLocation);
        date.eventLocations.put (event.getLocation ().getX (), xLocation);
      }
      this.events.put (event.getDate (), date);
    }
    else
    {
      if (Area.ANYWHERE.equals (event.getLocation ()))
      {
        date.anywhere.addEvent (event);
      }
      else
      {
        xLocation = date.eventLocations.get
                (event.getLocation ().getX ());
        if (null == xLocation)
        {
          xLocation = new Tree<Integer, LockList>();
          yLocation = new LockList();
          yLocation.addEvent (event);
          xLocation.put (event.getLocation ().getY (), yLocation);
          date.eventLocations.put 
                  (event.getLocation ().getX (), xLocation);
        }
        else
        {
          yLocation = xLocation.get (event.getLocation ().getY ());
          if (null == yLocation)
          {
            yLocation = new LockList();
            yLocation.addEvent (event);
            xLocation.put (event.getLocation ().getY (), yLocation);
          }
          else
          {
            yLocation.addEvent (event);
          }
        }
      }
    }
  }
  
  /**
   * Lock all events in this holder based on the given event.
   * 
   * @param event event to lock other events on.
   */
  public void lockFromEvent (Event event)
  {
    Event[] lockoutEvents;
    String lock;
    boolean locks;
    
    lockoutEvents = getEvents (0, Integer.MAX_VALUE, 0, 
            Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
    for (Event lockout : lockoutEvents)
    {
      lock = lockout.getType ().locksOut (event.getType ());
      if (lock != null)
      {
        locks = false;
        for (Map<String, String> values : 
                lockout.getType ().getLockouts (event.getType ()))
        {
          boolean temp = true;
          for (Map.Entry<String, String> value : values.entrySet ())
          {
            if (lockout.getVariable (value.getKey ()) != 
                    event.getVariable (value.getValue ()))
            {
              temp = false;
            }
          }
          if (temp)
          {
            locks = true;
          }
        }

        if (locks)
        {
          setLock (lockout, event.getDate ());
        }
      }
    }
  }
  
  /**
   * Set the lock of the event given in the holder.
   * 
   * @param event event to change lock status of.
   * @param locked the new lock status of the event.
   */
  public void setLock (Event event, int locked)
  {
    event.setLock (locked);
  }
  
  public void removeEvent (Event event)
  {
    TreeHolder date;
    Tree<Integer, LockList> xLocation;
    LockList yLocation;
    
    date = this.events.get (event.getDate ());
    
    if (null != date)
    {
      if (Area.ANYWHERE.equals (event.getLocation ()))
      {
        date.anywhere.removeEvent (event);
      }
      else
      {
        xLocation = date.eventLocations.get
                (event.getLocation ().getX ());
        if (null != xLocation)
        {
          yLocation = xLocation.get (event.getLocation ().getY ());
          if (null != yLocation)
          {
            yLocation.removeEvent (event);
          }
        }
      }
    }
  }
  
  public void removeEvents (int begin, int end, int left, int right, int top, 
          int bottom)
  {
    List<Event> listEvents = new ArrayList<Event>();
    List<TreeHolder> dates;
    List<Tree<Integer, LockList>> xLocations;
    List<LockList> yLocations;
    
    dates = this.events.get (begin, end);
    
    for (TreeHolder date : dates)
    {
      xLocations = date.eventLocations.get (left, right);
      for (Tree<Integer, LockList> xLocation : xLocations)
      {
        yLocations = xLocation.get (top, bottom);
        for (LockList yLocation : yLocations)
        {
          listEvents.addAll (yLocation.getList ());
        }
      }
    }
    
    for (Event event : listEvents)
    {
      removeEvent (event);
    }
  }
}
