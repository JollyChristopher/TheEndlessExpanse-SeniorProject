package halemaster.ee.world.history.event.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.reflections.Reflections;

/**
 * @name EventObjectType
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventObjectType 
{
  private static Map<String, EventObjectType> types = null;
  private Class<? extends EventObject> objectType;
  private List<EventObject> objects = new ArrayList<EventObject>();
  
  private EventObjectType (Class<? extends EventObject> objectType) throws
          InstantiationException, IllegalAccessException
  {
    this.objectType = objectType;
  }
  
  public final String getType () throws InstantiationException, 
          IllegalAccessException
  {
    return this.objectType.newInstance ().getType ();
  }
  
  /**
   * Get the object with the given id, or create a new one if passed null.
   * 
   * @param id id of object to get.
   * @return the object with the value equal to the id.
   * @throws InstantiationException
   * @throws IllegalAccessException 
   */
  public EventObject getObject (String id) throws InstantiationException, 
          IllegalAccessException
  {
    EventObject obj = null;
    synchronized (this)
    {
      for (EventObject temp : this.objects)
      {
        if (temp.getValue ().equals (id))
        {
          obj = temp;
        }
      }
    }
    
    return obj;
  }
  
  public EventObject getSetObject (String value) throws InstantiationException, 
          IllegalAccessException
  {
    EventObject obj = this.objectType.newInstance ();
    obj.setValue (value);
    
    synchronized (this)
    {
      this.objects.add (obj);
    }
    
    return obj;
  }
  
  /**
   * Get a new object with the random given.
   * 
   * @param random random to use in creating the object
   * @return the new object.
   * @throws InstantiationException
   * @throws IllegalAccessException 
   */
  public EventObject getObject (Random random) throws InstantiationException, 
          IllegalAccessException
  {
    EventObject obj = this.objectType.newInstance ();
    
    obj.init(random);
    synchronized (this)
    {
      this.objects.add (obj);
    }
    
    return obj;
  }
  
  /**
   * Get an object type by the given string.
   * 
   * @param type type to get object type of.
   * @return the object type with the given name.
   * @throws InstantiationException
   * @throws IllegalAccessException 
   */
  public static EventObjectType getByType (String type) throws
          InstantiationException, IllegalAccessException
  {
    if (null == types)
    {
      types = new HashMap<String, EventObjectType>();
      Reflections reflections = new Reflections
              ("halemaster.ee.world.history.event.structures.types");

      Set<Class<? extends EventObject>> allClasses = 
        reflections.getSubTypesOf(EventObject.class);
      
      Iterator<Class<? extends EventObject>> it = allClasses.iterator ();
      while (it.hasNext ())
      {
        Class<? extends EventObject> temp = it.next ();
        EventObjectType obj = new EventObjectType(temp);
        types.put (obj.getType(), obj);
      }
    }
    
    return types.get (type);
  }
}
