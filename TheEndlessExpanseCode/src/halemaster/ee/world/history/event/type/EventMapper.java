package halemaster.ee.world.history.event.type;

import halemaster.ee.world.Area;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.history.event.structures.EventObject;
import halemaster.ee.world.history.event.structures.EventObjectType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name EventMapper
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventMapper 
{
  public static final Logger LOGGER = 
          Logger.getLogger (EventMapper.class.getName ());
  private EventType type;
  private Map<String, List<Event>> parents = 
          new HashMap<String, List<Event>>();
  
  /**
   * create the event in the holder given the values.
   * 
   * @param holder holder to create event in.
   * @param random random to use for creation.
   * @param date date the event happens on.
   * @param area area the event happens in.
   * @param lockEvents whether to lock events in the holder based on this event.
   * @throws InstantiationException
   * @throws IllegalAccessException 
   */
  public void createEvent (EventHolder holder, Random random, int date,
          Area area, boolean lockEvents) throws InstantiationException, 
          IllegalAccessException
  {
    Event event;
    Event parent;
    EventObject obj;
    EventMapper cause;
    
    if (canCreateEvent ())
    {      
      // 1. Fill out event type, date, lock, Location, and Holder
      event = new Event ();
      event.setType (this.type);
      event.setDate (date);
      event.setLock (Event.UNLOCKED);
      event.setLocation (area.getX (), area.getY ());
      event.setHolder (holder);
      event.setSettled (lockEvents);
      
      // 2. Fill vars from dependencies
      for (String id : this.type.getDependenciesIds ())
      {
        for (Map<String, String> dep : this.type.getDependencies (id))
        {
          parent = this.parents.get (id).get 
                  (random.nextInt (this.parents.get (id).size ()));
          this.parents.get (id).remove (parent);
          if (parent.getLockDate () != Event.UNLOCKED)
          {
            event.setLock (parent.getLockDate ());
          }
          
          for (Entry<String, String> var : dep.entrySet ())
          {
            event.addVariable (var.getKey (), 
                    parent.getVariable (var.getValue ()));
          }
        }
      } 
      
      // 3. Initialize remaining variables
      for (String var : this.type.getVariables ())
      {
        if (null == event.getVariable (var))
        {
          obj = EventObjectType.getByType (this.type.getVariableType (var))
                  .getObject (random);
          obj.setEvent (event);
          event.addVariable (var, obj);
        }
      }
      
      // 4. Modify event
      if (lockEvents)
      {
        for (String change : this.type.getChangesIds ())
        {
          for (Entry<String, String> modifiers : 
                  this.type.getChanges (change).entrySet ())
          {
            event.getVariable (change).modify (event.getVariable 
                    (modifiers.getKey ()), modifiers.getValue ());
          }
        }
        
        // create causes
        // create causes
        for (String causeId : this.type.getCausesIds ())
        {
          for (Map.Entry<String, String> chances : 
                  this.type.getCauses (causeId).entrySet ())
          {
            try
            {
              if (random.nextInt (100) <
                      Integer.valueOf (chances.getKey ().split ("%")[0]))
              {
                try
                {
                  cause = new EventMapper ();
                  cause.setType (holder.getType (causeId));
                  cause.addParent (event);
                  String[] timeRange = chances.getValue ().split("-");
                  cause.createEvent (holder, random, date
                          + (random.nextInt 
                          (Integer.valueOf (timeRange[1]) - 
                          Integer.valueOf(timeRange[0]) + 1) + 
                          Integer.valueOf(timeRange[0])), 
                          area, false);
                }
                catch (InstantiationException e)
                {
                  LOGGER.log (Level.WARNING, "Failed to create an event", 
                          e);
                }
                catch (IllegalAccessException e)
                {
                  LOGGER.log (Level.WARNING, "Failed to create an event",
                          e);
                }
                catch (IllegalArgumentException e)
                {
                  LOGGER.log (Level.SEVERE, 
                          "No new events could be created!", e);
                }
              }
            }
            catch (NumberFormatException ex)
            {
              LOGGER.log (Level.SEVERE, 
                          "Number misformat", ex);
            }
          }
        }
      }
      
      // 5. Add to holder
      holder.addEvent (event);
      if (lockEvents)
      {
        holder.lockFromEvent (event);
      }
    }
  }
  
  /**
   * Determine whether this mapper is able to be turned into an event.
   * 
   * @return whether this mapper can create an event.
   */
  public boolean canCreateEvent ()
  {
    boolean canCreate = true;
    
    for (String id : this.type.getDependenciesIds ())
    {
      if (null == this.parents.get(id) || 
              this.type.getDependencies (id).size () > 
              this.parents.get (id).size ())
      {
        canCreate = false;
      }
    }
    
    return canCreate;
  }

  public void setType (EventType type)
  {
    this.type = type;
  }
  
  public EventType getType ()
  {
    return this.type;
  }
  
  /**
   * Add the given event as a parent to this mapper.
   * 
   * @param event event to add as a parent for this mapper.
   */
  public void addParent (Event event)
  {
    String[] dependencies = this.type.getDependenciesIds ();
    List<Event> parentSet;
    
    for (String dep : dependencies)
    {
      if (event.getType ().countsAs (dep))
      {
        parentSet = this.parents.get (dep);
        if (null == parentSet)
        {
          parentSet = new ArrayList<Event> ();
          this.parents.put (dep, parentSet);
        }
        parentSet.add (event);
      }
    }
  }
}
