package halemaster.ee.world.history;

import halemaster.ee.state.Loader;
import halemaster.ee.world.Area;
import halemaster.ee.world.Config;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.history.event.EventSet;
import halemaster.ee.world.history.event.type.EventMapper;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name HistoryGenerator
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class HistoryGenerator implements Loader
{
  public static final int EVENT_SWITCH = 4000;
  public static final int SEASON_COUNT = 4;
  public static final String TIME_KEY = "time";
  private static final Logger LOGGER = Logger.getLogger 
          (HistoryGenerator.class.getName ());
  private EventTypeHolder types;
  private List<EventTypeHolder> reference = new ArrayList<EventTypeHolder>();
  private int increment;
  private int until;
  private int[] grouping;
  private int top, bottom, left, right;
  private Random random;
  private Area location;
  private EventHolder givenBasis;
  private EventHolder createdEvents = null;
  private double percent = 0.0;
  private String eventDisplay = null;

  public int getIncrement ()
  {
    return increment;
  }

  public void setIncrement (int increment)
  {
    this.increment = increment;
  }

  public int getUntil ()
  {
    return until;
  }

  public void setUntil (int until)
  {
    this.until = until;
  }

  public int[] getGrouping ()
  {
    return grouping;
  }

  public void setGrouping (int[] grouping)
  {
    this.grouping = grouping;
  }

  public int getTop ()
  {
    return top;
  }

  public void setTop (int top)
  {
    this.top = top;
  }

  public int getBottom ()
  {
    return bottom;
  }

  public void setBottom (int bottom)
  {
    this.bottom = bottom;
  }

  public int getLeft ()
  {
    return left;
  }

  public void setLeft (int left)
  {
    this.left = left;
  }

  public int getRight ()
  {
    return right;
  }

  public void setRight (int right)
  {
    this.right = right;
  }

  public Random getRandom ()
  {
    return random;
  }

  public void setRandom (Random random)
  {
    this.random = random;
  }

  public Area getLocation ()
  {
    return location;
  }

  public void setLocation (Area location)
  {
    this.location = location;
  }
  
  public synchronized EventHolder getCreated ()
  {
    return this.createdEvents;
  }

  public EventHolder getBasis ()
  {
    return givenBasis;
  }

  public void setBasis (EventHolder givenBasis)
  {
    this.givenBasis = givenBasis;
  }
  
  /**
   * Generate events with the given parameters.
   * 
   * @return the event holder generated into.
   */
  public EventHolder generate (final EventHolder basis)
  {
    Event[] tempEvents;
    Event[] spawnEvents;
    Event[] basisEvents;
    EventMapper mapper;
    EventMapper[] tempMappers;
    EventHolder creation = new EventHolder (basis.getWorldName (), 
            basis.getWorld ());
    EventSet mapperSet;
    
    creation.addTypeHolder (this.types);
    for (EventTypeHolder ref : this.reference)
    {
      creation.addTypeHolder (ref);
    }
    
    this.percent = 0.0;
    
    try
    {
      Config.pushValue (basis.getWorldName (), TIME_KEY, 
              String.valueOf(this.until));
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not save config", e);
    }
    
    for (int currentTime = 0; currentTime <= this.until; 
            currentTime += this.increment)
    {
      tempEvents = creation.getEvents (currentTime - this.increment + 1,
              currentTime, this.left, this.right, this.top, this.bottom);
      
      for (Event temp : tempEvents)
      {
        if (!temp.isSettled ())
        {
          if (temp.getLockDate () != Event.UNLOCKED)
          {
            creation.removeEvent (temp);
          }
          else
          {
            // cause changes
            for (String change : temp.getType ().getChangesIds ())
            {
              for (Map.Entry<String, String> modifiers : 
                      temp.getType ().getChanges (change).entrySet ())
              {
                temp.getVariable (change).modify (temp.getVariable 
                        (modifiers.getKey ()), modifiers.getValue ());
              }
            }
            // create causes
            for (String causeId : temp.getType ().getCausesIds ())
            {
              for (Map.Entry<String, String> chances : 
                      temp.getType ().getCauses (causeId).entrySet ())
              {
                try
                {
                  if (this.random.nextInt (100) <
                          Integer.valueOf (chances.getKey ().split ("%")[0]))
                  {
                    try
                    {
                      mapper = new EventMapper ();
                      mapper.setType (creation.getType (causeId));
                      mapper.addParent (temp);
                      String[] timeRange = chances.getValue ().split("-");
                      mapper.createEvent (creation, this.random, temp.getDate ()
                              + (this.random.nextInt 
                              (Integer.valueOf (timeRange[1]) - 
                              Integer.valueOf(timeRange[0]) + 1) + 
                              Integer.valueOf(timeRange[0])), 
                              temp.getLocation (), false);
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
            
            creation.lockFromEvent (temp);
          }
          temp.setSettled (true);
        }
      }
      for (int eventCount = this.grouping[this.random.nextInt 
              (this.grouping.length)]; 
              eventCount > 0; eventCount--)
      {
        basisEvents = basis.getEvents (0, currentTime, 
             this.left, this.right, this.top, this.bottom, false);
        spawnEvents = creation.getEvents (0, currentTime, 
             this.left, this.right, this.top, this.bottom, false);
        tempEvents = new Event[basisEvents.length + spawnEvents.length];
        System.arraycopy (basisEvents, 0, tempEvents, 0, basisEvents.length);
        System.arraycopy (spawnEvents, 0, tempEvents, basisEvents.length,
                spawnEvents.length);
        tempMappers = this.types.generateMappers (tempEvents);
        mapperSet = new EventSet ();
        for (EventMapper temp : tempMappers)
        {
          mapperSet.add (temp);
        }
        try
        {
          mapperSet.getMapper (this.random).createEvent 
                  (creation, this.random, currentTime, this.location, true);
        }
        catch (InstantiationException e)
        {
          LOGGER.log (Level.WARNING, "Failed to create an event", e);
        }
        catch (IllegalAccessException e)
        {
          LOGGER.log (Level.WARNING, "Failed to create an event", e);
        }
        catch (IllegalArgumentException e)
        {
          LOGGER.log (Level.WARNING, "No new events could be created!", e);
        }
      }
      
      this.percent = ((double) currentTime) / ((double) this.until);
      if (0 == currentTime % EVENT_SWITCH || this.eventDisplay == null)
      {
        Event[] messageEvents = creation.getEvents (0, currentTime, left, right, 
                top, bottom);
        if (null != messageEvents && 0 < messageEvents.length)
        {
          this.eventDisplay = messageEvents[this.random.nextInt 
                  (messageEvents.length)].getDescription ();
        }
      }
    }
    
    tempEvents = creation.getEvents (this.until + 1, Integer.MAX_VALUE, 
              this.left, this.right, this.top, this.bottom);
      
    for (Event temp : tempEvents)
    {
      creation.removeEvent (temp);
    }
    
    return creation;
  }

  public EventTypeHolder getTypes ()
  {
    return types;
  }

  public void setTypes (EventTypeHolder types)
  {
    this.types = types;
  }

  public EventTypeHolder[] getReferences ()
  {
    EventTypeHolder[] refs = new EventTypeHolder[0];
    refs = this.reference.toArray (refs);
    return refs;
  }

  public void addReference (EventTypeHolder reference)
  {
    this.reference.add (reference);
  }

  public double getPercentage ()
  {
    return this.percent - 0.001;
  }

  public String getMessage ()
  {
    return this.eventDisplay;
  }
  
  public String getName ()
  {
    return "TEE-HistoryGeneration-" + this.givenBasis.getWorldName () + "-" + 
            this.location;
  }

  public void run ()
  {
    this.createdEvents = this.generate (givenBasis);
  }
}
