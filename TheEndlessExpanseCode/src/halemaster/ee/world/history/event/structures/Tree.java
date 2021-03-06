package halemaster.ee.world.history.event.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @name Tree
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class Tree <key, type>
{
  TreeMap<key, type> tree = new TreeMap<key, type>();
  
  /**
   * Add the object with the given type and key to the tree.
   * 
   * @param theKey the key to add to the tree
   * @param theType the object to add to the tree.
   */
  public void put (key theKey, type theType)
  {
    this.tree.put (theKey, theType);
  }
  
  /**
   * remove the key and object from the tree.
   * Return all values at the given key.
   * 
   * @param theKey the key to find the object at.
   * @return all values at the given key before removal.
   */
  public type remove (key theKey)
  {
    return this.tree.remove (theKey);
  }
  
  /**
   * Get all the values from the given key range.
   * 
   * @param key1 lower bound to search for.
   * @param key2 upper bound to search for.
   * @return array of values within the range.
   */
  public List<type> get (key key1, key key2)
  {
    List<type> objects = new ArrayList<type>();
    SortedMap<key, type> subMap = 
            this.tree.subMap (key1, true, key2, true);
    
    for (Entry<key, type> entry : subMap.entrySet ())
    {
      objects.add (entry.getValue ());
    }
    
    return objects;
  }
  
  public List<type> getAll ()
  {
    List<type> objects = new ArrayList<type>();
    
    for (Entry<key, type> entry : this.tree.entrySet ())
    {
      objects.add (entry.getValue ());
    }
    
    return objects;
  }
  
  public List<key> getAllKeys ()
  {
    List<key> objects = new ArrayList<key>();
    
    for (Entry<key, type> entry : this.tree.entrySet ())
    {
      objects.add (entry.getKey ());
    }
    
    return objects;
  }
  
  /**
   * Get the values associated with the particular key value
   * 
   * @param theKey the key value to get.
   * @return the values associated with the key value
   */
  public type get (key theKey)
  {
    type value = null;
    List<type> values = get (theKey, theKey);
    if (null != values && values.size () > 0)
    {
      value = values.get (0);
    }
    return value;
  }
}
