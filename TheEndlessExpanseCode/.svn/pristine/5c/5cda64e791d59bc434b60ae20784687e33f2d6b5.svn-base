package halemaster.ee.quest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @name Requirement
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 */
public class Requirement 
{
  private Map<String, Integer> kill = new HashMap<String, Integer>();
  private Map<String, Integer> items = new HashMap<String, Integer>();
  private Set<String> npcToTalkTo = new HashSet<String>();

  /**
   * Get all names of creatures that need to be killed
   * @return names of creatures to be killed
   */
  public String[] getKillNames ()
  {
    String[] killNames = new String[this.kill.size ()];
    int index = 0;
    
    for (String killName : this.kill.keySet ())
    {
      killNames[index] = killName;
      index++;
    }
    
    return killNames;
  }
  
  /**
   * Get the amount of creatures that need to be killed for the given name
   * @param name name of creature to be killed
   * @return amount that needs to be killed
   */
  public int getKillCount (String name)
  {
    return this.kill.get (name);
  }
  
  /**
   * Check if we have fulfilled the kill requirement
   * @return true if there are no kill values greater than 0.
   */
  public boolean checkKillEmpty ()
  {
    boolean isEmpty = true;
    
    for (String killName : getKillNames ())
    {
      if (0 < getKillCount (killName))
      {
        isEmpty = false;
      }
    }
    
    return isEmpty;
  }
  
  /**
   * Add the given value to the number of kills needed for a given name
   * @param name name for kill values
   * @param value value to add
   */
  public void addKill (String name, int value)
  {
    Integer killCount = this.kill.get (name);
    
    if (null == killCount)
    {
      killCount = 0;
    }
    
    this.kill.put (name, killCount + value);
  }
  
  /**
   * Subtract the given value to the number of kills needed for a given name
   * @param name name for kill values
   * @param value value to subtract
   */
  public void subtractKill (String name, int value)
  {
    addKill (name, -value);
  }
  
  /**
   * Find whether the given name is contained in this requirement
   * @param name name to kill
   * @return whether the kill is required
   */
  public boolean hasKill (String name)
  {
    return this.kill.containsKey (name);
  }
  
  /**
   * Get all item names from this requirement
   * @return names of items to obtain
   */
  public String[] getItemNames ()
  {
    String[] itemNames = new String[this.items.size ()];
    int index = 0;
    
    for (String itemName : this.items.keySet ())
    {
      itemNames[index] = itemName;
      index++;
    }
    
    return itemNames;
  }
  
  /**
   * Get the amount of items need for a given name
   * @param name name of item to get
   * @return amount of item of that name to get
   */
  public int getItemCount (String name)
  {
    return this.items.get (name);
  }
  
  /**
   * Check if we have any needed items
   * @return true if all item values are less than or equal to 0
   */
  public boolean checkItemEmpty ()
  {
    boolean isEmpty = true;
    
    for (String itemName : getItemNames ())
    {
      if (0 < getItemCount (itemName))
      {
        isEmpty = false;
      }
    }
    
    return isEmpty;
  }
  
  /**
   * Add the amount of the given item name to the requirement
   * @param name name of item
   * @param value amount to add
   */
  public void addItem (String name, int value)
  {
    Integer itemCount = this.items.get (name);
    
    if (null == itemCount)
    {
      itemCount = 0;
    }
    
    this.items.put (name, itemCount + value);
  }
  
  /**
   * Subtract the given item amount from the requirement
   * @param name name of item
   * @param value amount to subtract
   */
  public void subtractItem (String name, int value)
  {
    addItem (name, -value);
  }
  
  /**
   * Check if the item name is required
   * @param name name of item
   * @return whether the item is in the requirement
   */
  public boolean hasItem (String name)
  {
    return this.items.containsKey (name);
  }
  
  /**
   * Get the names of all npcs to talk to
   * @return names of npcs to talk to.
   */
  public String[] getTalkNames ()
  {
    String[] talkNames = new String[this.npcToTalkTo.size ()];
    int index = 0;
    
    for (String talk : this.npcToTalkTo)
    {
      talkNames[index] = talk;
      index++;
    }
    
    return talkNames;
  }
  
  /**
   * Check if we need to talk to any npcs
   * @return if we need to talk to any npcs
   */
  public boolean checkTalkEmpty ()
  {
    return this.npcToTalkTo.isEmpty ();
  }
  
  /**
   * Add a new npc to talk to
   * @param name name of npc
   */
  public void addTalk (String name)
  {
    this.npcToTalkTo.add (name);
  }
  
  /**
   * Remove an npc to talk to
   * @param name name of npc
   */
  public void removeTalk (String name)
  {
    this.npcToTalkTo.remove (name);
  }
  
  /**
   * Check whether we need to talk to an npc of the given name
   * @param name name of npc to talk to
   * @return whether the npc is needed to talk to
   */
  public boolean hasTalk (String name)
  {
    return this.npcToTalkTo.contains (name);
  }
  
  /**
   * Make an exact copy of the Requirement with its own values that are a copy
   * of those in this Requirement
   * @return copy of this requirement
   */
  public Requirement copy ()
  {
    Requirement req = new Requirement ();
    
    for (String item : getItemNames ())
    {
      req.addItem (item, getItemCount (item));
    }
    
    for (String killName : getKillNames ())
    {
      req.addKill (killName, getKillCount (killName));
    }
    
    for (String talk : getTalkNames ())
    {
      req.addTalk (talk);
    }
    
    return req;
  }
}
