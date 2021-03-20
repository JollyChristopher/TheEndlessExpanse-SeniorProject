package halemaster.ee.quest;

import halemaster.ee.Json;
import halemaster.ee.item.ItemTemplate;
import halemaster.ee.item.Reward;
import halemaster.ee.world.Biome;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityGenerator;
import halemaster.ee.world.entity.Statistic;
import halemaster.ee.world.history.event.Event;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name QuestTemplate
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 */
public class QuestTemplate 
{
  public static final String ANY = "any";
  public static final String SELF = "self";
  public static final String QUEST_FOLDER = "assets/Quests";
  private static QuestTemplate[] loadedTemplates = null;
  private static final Logger LOGGER = 
          Logger.getLogger (QuestTemplate.class.getName ());
  
  private String id;
  private Map<String, Integer[]> kill;
  private Map<String, Integer[]> item;
  private Map<String, String[]>talk;
  private String[] jobs;
  private String[] biomes;
  private String[] history;
  private String[] chain;
  private int offerRate;
  private int talkCount;

  public String getId ()
  {
    return id;
  }

  public Map<String, Integer[]> getKill ()
  {
    return kill;
  }

  public Map<String, Integer[]> getItem ()
  {
    return item;
  }

  public Map<String, String[]> getTalk ()
  {
    return talk;
  }

  public String[] getJobs ()
  {
    return jobs;
  }

  public String[] getBiomes ()
  {
    return biomes;
  }

  public String[] getHistory ()
  {
    return history;
  }

  public String[] getChain ()
  {
    return chain;
  }

  public int getOfferRate ()
  {
    return offerRate;
  }

  public int getTalkCount ()
  {
    return talkCount;
  }
  
  /**
   * Check whether the string exists in the given set
   * @param set set to look in
   * @param against string to look for
   * @return whether the string exists in the set
   */
  private static boolean checkRegular (Collection<String> set, String against)
  {
    boolean found = true;
    
    if (set.size () > 0)
    {
      found = set.contains (ANY);
      found |= set.contains (against);
    }
    
    return found;
  }
  
  /**
   * Look for one of the given againsts in the given set
   * @param set set to look in
   * @param against set to find at least one of
   * @return if any string from against can be found in set, true
   */
  private static boolean checkNested (Collection<String> set, 
          Collection<String> against)
  {
    boolean found = false;
    Iterator<String> eachItem = against.iterator ();
    
    while (!found && eachItem.hasNext ())
    {
      found = checkRegular (set, eachItem.next ());
    }
    
    return found;
  }
  
  /**
   * Check to make sure that all values from findValues are in lookIn
   * @param findValues set to find all values from
   * @param lookIn where to look for values
   * @return whether all of set is in against
   */
  private static boolean checkAll (Collection<String> findValues, 
          Collection<String> lookIn)
  {
    boolean found = true;
    String next;
    Iterator<String> eachItem = findValues.iterator ();
    
    while (found && eachItem.hasNext ())
    {
      next = eachItem.next ();
      if (!ANY.equals (next))
      {
        found = checkRegular (lookIn, next);
      }
    }
    
    return found;
  }
  
  /**
   * Get the possible quest templates for the given information.
   * @param entity npc who would offer the quest
   * @param history history relating to that npc
   * @param biome biome the npc comes from
   * @param otherEntities other entities near the npc
   * @return list of quest templates that can be used.
   */
  public static List<QuestTemplate> possibleQuests (Entity entity,
          Set<Event> history, Biome biome, List<Entity> otherEntities)
  {
    List<QuestTemplate> quests = new ArrayList<QuestTemplate> ();
    Set<String> historyFind = new HashSet<String> ();
    Set<String> otherCareers = new HashSet<String> ();
    Set<String> otherNames = new HashSet<String> ();
    Set<String> stackNames = new HashSet<String> ();
    List<ItemTemplate> stackItems = Reward.getStackables (entity.getStat 
            (Statistic.LEVEL));
    
    if (null == loadedTemplates)
    {
      loadTemplates ();
    }
    
    if (null != history)
    {
      for (Event event : history)
      {
        historyFind.add (event.getType ().getId ());
      }
    }
    
    for (ItemTemplate stack : stackItems)
    {
      stackNames.add (stack.getNames ()[0]);
    }
    
    for (Entity other : otherEntities)
    {
      if (other != entity)
      {
        otherCareers.add (other.getData (EntityGenerator.NPC_CAREER));
        otherNames.add (other.getName ());
      }
    }
    
    for (QuestTemplate template : loadedTemplates)
    {
      String[] talkSet = template.talk.get (entity.getData 
              (EntityGenerator.NPC_CAREER));
      if (null == talkSet)
      {
        talkSet = new String[0];
      }
      
      if (checkRegular (Arrays.asList (template.jobs), entity.getData 
              (EntityGenerator.NPC_CAREER)) && // check job
              checkAll (Arrays.asList (template.history), 
              historyFind) && // check history
              checkRegular (Arrays.asList (template.biomes), 
              biome.getName ()) && // check biomes
              checkNested (Arrays.asList 
              (talkSet), otherCareers) && // check talk
              checkAll (template.kill.keySet (), otherNames) && // check kill
              checkAll (template.item.keySet (), stackNames)) // check items
      {
        quests.add (template);
      }
    }
    
    return quests;
  }
  
  /**
   * reload all templates from Quests folder
   */
  public static void loadTemplates ()
  {
    File questFolder = new File (QUEST_FOLDER);
    
    for (File quest : questFolder.listFiles ())
    {
      try
      {
        loadedTemplates = Json.getFromFile (quest, QuestTemplate.class);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Could not load quest file", e);
      }
    }
  }
}
