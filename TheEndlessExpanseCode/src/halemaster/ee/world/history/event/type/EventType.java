package halemaster.ee.world.history.event.type;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * @name EventType
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventType 
{
  private String id;
  private Map<String, String> variables;
  private Map<String, Map<String, String>> changes;
  private Map<String, List<Map<String, String>>> dependencies;
  private Map<String, List<Map<String, String>>> lockouts;
  private Map<String, Map<String, String>> counts;
  private Map<String, Map<String, String>> causes;
  private transient EventTypeHolder holder;
  
  public EventTypeHolder getHolder ()
  {
    return holder;
  }
  
  public void setHolder (EventTypeHolder holder)
  {
    this.holder = holder;
  }

  public String getId ()
  {
    return id;
  }
  
  public void setId (String id)
  {
    this.id = id;
  }
  
  public String[] getVariables ()
  {
    String[] variableNames = new String[0];
    
    variableNames = this.variables.keySet ().toArray (variableNames);
    
    return variableNames;
  }
  
  public String getVariableType (String name)
  {
    return this.variables.get (name);
  }
  
  public String[] getChangesIds ()
  {
    String[] ids = new String[0];
    
    ids = this.changes.keySet ().toArray (ids);
    
    return ids;
  }
  
  public Map<String, String> getChanges (String id)
  {
    return this.changes.get (id);
  }
  
  public String[] getDependenciesIds ()
  {
    String[] ids = new String[0];
    
    ids = this.dependencies.keySet ().toArray (ids);
    
    return ids;
  }
  
  public List<Map<String, String>> getDependencies (String id)
  {
    return this.dependencies.get (id);
  }
  
  public String getCountedDependency (EventType type)
  {
    List<Map<String, String>> dep = this.dependencies.get (type.getId ());
    String[] countsAs = type.getCountsIds ();
    String countAs = type.getId ();
    
    for (int i = 0; null == dep && i < countsAs.length; i++)
    {
      dep = this.dependencies.get (countsAs[i]);
      if (null != dep)
      {
        countAs = countsAs[i];
      }
    }
    
    return countAs;
  }
  
  public boolean countsAs (String id)
  {
    boolean countAs = false;
    Queue<EventType> gens = new ArrayDeque<EventType>();
    EventType current;
    gens.add (this);
    
    while (!countAs && !gens.isEmpty ())
    {
      current = gens.poll ();
      if (current.getId ().equals (id))
      {
        countAs = true;
      }
      else
      {
        for (String next : current.getCountsIds ())
        {
          if (null != this.holder.getType (next))
          {
            gens.add (this.holder.getType (next));
          }
        }
      }
    }
    
    return countAs;
  }
  
  public List<Map<String, String>> getDependencies (EventType type)
  {
    List<Map<String, String>> dep = new ArrayList<Map<String, String>>();
    List<Map<String, String>> tempDep = this.dependencies.get (type.getId ());
    String[] countsAs = type.getCountsIds ();
    Map<String, String> copy;
    
    if (null != tempDep)
    {
      for (Map<String, String> singleDep : tempDep)
      {
        copy = new HashMap<String, String>();
        for (Entry<String, String> entry : singleDep.entrySet ())
        {
          copy.put (entry.getKey (), entry.getValue ());
        }
        dep.add (copy);
      }
    }
    
    for (int i = 0; null == tempDep && i < countsAs.length; i++)
    {
      tempDep = this.dependencies.get (countsAs[i]);
      if (null != tempDep)
      {
        for (Map<String, String> singleDep : tempDep)
        {
          copy = new HashMap<String, String>();
          for (Entry<String, String> entry : singleDep.entrySet ())
          {
            copy.put (entry.getKey (), type.getCounts 
                    (countsAs[i]).get (entry.getValue ()));
          }
          dep.add (copy);
        }
      }
    }
    
    return dep;
  }
  
  public String[] getLockoutsIds ()
  {
    String[] ids = new String[0];
    
    ids = this.lockouts.keySet ().toArray (ids);
    
    return ids;
  }
  
  public String locksOut (EventType event)
  {
    String lock = null;
    for (String locks : this.lockouts.keySet ())
    {
      if (event.countsAs (locks))
      {
        lock = locks;
      }
    }
    
    return lock;
  }
  
  public final List<Map<String, String>> getLockouts (String id)
  {
    return this.lockouts.get (id);
  }
  
  public final List<Map<String, String>> getLockouts (EventType type)
  {
    for (String lockId : this.lockouts.keySet ())
    {
      if (type.countsAs (lockId))
      {
        return this.lockouts.get (lockId);
      }
    }
    return null;
  }
  
  public String[] getCountsIds ()
  {
    String[] ids = new String[0];
    
    ids = this.counts.keySet ().toArray (ids);
    
    return ids;
  }
  
  public Map<String, String> getCounts (String id)
  {
    return this.counts.get (id);
  }
  
  public String[] getCausesIds ()
  {
    String[] ids = new String[0];
    
    ids = this.causes.keySet ().toArray (ids);
    
    return ids;
  }
  
  public Map<String, String> getCauses (String id)
  {
    return this.causes.get (id);
  }
}
