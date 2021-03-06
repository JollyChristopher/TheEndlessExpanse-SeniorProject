package halemaster.ee.world.history.event;

import halemaster.ee.world.history.event.type.EventMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Halemaster
 */
public class EventSet
{
  public static final int POWER = 32;
  private List<EventMapper> mappers = new ArrayList<EventMapper> ();
  private int totalValues = 0;
  
  public void add (EventMapper mapper)
  {
    mappers.add (mapper);
    totalValues += Math.pow (POWER, mapper.getType ().getDependenciesIds ()
            .length);
  }
  
  public EventMapper getMapper (Random random)
  {
    EventMapper mapper = null;
    int index = 0;
    int value = random.nextInt (totalValues);
    
    while (mapper == null)
    {
      value -= Math.pow (POWER, this.mappers.get (index).getType ()
              .getDependenciesIds ().length);
      if (0 > value)
      {
        mapper = this.mappers.get (index);
      }
      index++;
    }
    
    return mapper;
  }
}
