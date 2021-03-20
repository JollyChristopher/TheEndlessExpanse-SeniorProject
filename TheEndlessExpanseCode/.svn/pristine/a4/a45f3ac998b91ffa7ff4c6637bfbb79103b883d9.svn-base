package halemaster.ee.world.history.event.type;

import halemaster.ee.world.history.event.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name EventTypeHolder
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventTypeHolder 
{
  private Map<String, EventType> types = new HashMap<String, EventType>();
  
  public EventType getType (String id)
  {
    return this.types.get (id);
  }
  
  public void addType (EventType type)
  {
    this.types.put (type.getId (), type);
    type.setHolder (this);
  }
  
  /**
   * Given the events, create an array of EventMappers that can possibly be
   * spawned.
   * 
   * @param events events to find mappers for.
   * @return array of possible event mappers.
   */
  public EventMapper[] generateMappers (Event[] events)
  {
    EventMapper[] mappers = new EventMapper[0];
    EventMapper temp;
    String[] typeNames;
    List<EventMapper> allEvents = new ArrayList<EventMapper>();
    List<EventMapper> dependentEvents = new ArrayList<EventMapper> ();
    Collection<EventType> allTypes = this.types.values ();
    
    for (EventType type : allTypes)
    {
      temp = new EventMapper();
      temp.setType (type);
      typeNames = type.getDependenciesIds ();
      
      if (null == typeNames || 0 == typeNames.length)
      {
        allEvents.add (temp);
      }
      else
      {
        dependentEvents.add (temp);
      }
    }
    
    for (Event event : events)
    {
      for (EventMapper mapper : dependentEvents)
      {
        mapper.addParent (event);
      }
    }
    
    
    for (EventMapper possible : dependentEvents)
    {
      if (possible.canCreateEvent ())
      {
        allEvents.add (possible);
      }
    }
    
    mappers = allEvents.toArray (mappers);
    
    return mappers;
  }
}
