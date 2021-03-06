package halemaster.ee.quest;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.item.Reward;
import halemaster.ee.localization.Localizer;
import halemaster.ee.world.Biome;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityGenerator;
import halemaster.ee.world.entity.Monster;
import halemaster.ee.world.entity.Statistic;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * @name Offer
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 */
public class Offer 
{
  public static final String WORLD_SEPARATOR = ";";
  public static final String OFFER_SEPARATOR = ",";
  public static final String OFFER_FOLDER = "/offers";
  public static final String OFFER_CURRENT = "currentOffer";
  public static final String OFFER_LIST = "possibleOffers";
  public static final String TITLE_KEY = "quest.title.";
  public static final String DESCRIPTION_KEY = "quest.description.";
  public static final int WORLD_NAME = 0;
  public static final int UNIQUE_ID = 1;
  public static final int ONLY_OFFER = 0;
  public static final int MAX_KILL = 20;
  public static final int MAX_ITEM = 20;
  public static final int QUEST_LEVEL = 2;
  private static Map<String, Map<String, Offer>> loadedOffers = 
          new HashMap<String, Map<String, Offer>>();
  
  private transient Entity npc;
  private String offerId;
  private String npcName;
  private String templateId;
  private Reward reward;
  private Requirement requirements;
  private int offerRate;
  private Map<String, Integer> offered = new HashMap<String, Integer> ();

  public Entity getNpc ()
  {
    return npc;
  }

  public String getOfferId ()
  {
    return offerId;
  }

  public Reward getReward ()
  {
    return reward;
  }

  public Requirement getRequirements ()
  {
    return requirements;
  }

  public int getOfferRate ()
  {
    return offerRate;
  }
  
  public int getOfferAmount (String playerName)
  {
    if (null == this.offered.get (playerName))
    {
      this.offered.put (playerName, 0);
    }
    return this.offered.get (playerName);
  }
  
  public String getNpcName ()
  {
    return this.npcName;
  }
  
  /**
   * Finish the quest for the given player
   * @param playerName name of the player
   * @return the reward for the quest
   */
  public Reward finishQuest (String playerName)
  {
    if (this.offered.containsKey (playerName))
    {
      this.offered.put (playerName, this.offered.get (playerName) + 1);
    }
    else
    {
      this.offered.put (playerName, 1);
    }
    
    return this.reward;
  }
  
  /**
   * Get the localized title of the offer.
   * Vars are {npc}{turnIn}{itemName,amount...}{killName,amount...}{talk...}
   * @return localized title
   */
  public String getTitle ()
  {
    String[] itemNames = this.requirements.getItemNames ();
    String[] killNames = this.requirements.getKillNames ();
    String[] talkNames = this.requirements.getTalkNames ();
    String[] variables = new String[1 + itemNames.length * 2 + 
            killNames.length * 2 + talkNames.length];
    
    variables[0] = this.npcName;
    for (int i = 0; i < itemNames.length; i++)
    {
      variables[i * 2 + 1] = Localizer.getString(itemNames[i]);
      variables[i * 2 + 2] = String.valueOf (this.requirements.getItemCount 
              (itemNames[i]));
    }
    
    for (int i = 0; i < killNames.length; i++)
    {
      variables[i * 2 + 1 + itemNames.length * 2] = Localizer.getString
              (killNames[i]);
      variables[i * 2 + 2 + itemNames.length * 2] = String.valueOf (this
              .requirements.getKillCount (killNames[i]));
    }
    
    for (int i = 0; i < talkNames.length; i++)
    {
      variables[i + 1 + itemNames.length * 2 + killNames.length * 2] = 
              talkNames[i];
    }
    
    return Localizer.getString (TITLE_KEY + this.templateId, 
            (Object[]) variables);
  }
  
  /**
   * Get the localized description of the offer.
   * Vars are {npc}{turnIn}{itemName,amount...}{killName,amount...}{talk...}
   * @return localized description
   */
  public String getDescription ()
  {
    String[] itemNames = this.requirements.getItemNames ();
    String[] killNames = this.requirements.getKillNames ();
    String[] talkNames = this.requirements.getTalkNames ();
    String[] variables = new String[1 + 1 + itemNames.length * 2 + 
            killNames.length * 2 + talkNames.length];
    
    variables[0] = this.npcName;
    for (int i = 0; i < itemNames.length; i++)
    {
      variables[i * 2 + 1] = Localizer.getString(itemNames[i]);
      variables[i * 2 + 2] = String.valueOf (this.requirements.getItemCount 
              (itemNames[i]));
    }
    
    for (int i = 0; i < killNames.length; i++)
    {
      variables[i * 2 + 1 + itemNames.length * 2] = Localizer.getString
              (killNames[i]);
      variables[i * 2 + 2 + itemNames.length * 2] = String.valueOf (this
              .requirements.getKillCount (killNames[i]));
    }
    
    for (int i = 0; i < talkNames.length; i++)
    {
      variables[i + 1 + itemNames.length * 2 + killNames.length * 2] = 
              talkNames[i];
    }
    
    return Localizer.getString (DESCRIPTION_KEY + this.templateId, 
            (Object[]) variables);
  }
  
  /**
   * Get the offer with the given id if in memory.
   * @param id id of offer
   * @param npc if not null, load using this npc if needed
   * @return Offer with the given id
   */
  public static Offer getOffer (String id, Entity npc)
  {
    String world = id.split (WORLD_SEPARATOR)[WORLD_NAME];
    String unique = id.split (WORLD_SEPARATOR)[UNIQUE_ID];
    Map<String, Offer> worldOffers = loadedOffers.get (world);
    Offer offer = null;
    
    if (null != worldOffers)
    {
      offer = worldOffers.get (unique);
    }
    
    if (null == offer)
    {
      if (null != npc)
      {
        offer = loadOffer (id, npc);
      }
      else // temporary load! not held in map!
      {
        try
        {
          offer = Json.getFromFile (new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + world + OFFER_FOLDER + "/" + 
                unique), Offer.class)[ONLY_OFFER];
        }
        catch (IOException e)
        {
          // log
        }
      }
    }
    
    return offer;
  }

  /**
   * Load the given Offer for the entity.
   * @param id id of offer to load
   * @param entity entity to load offer into
   * @return Offer loaded. This is stored into recently loaded offers as well.
   */
  public static Offer loadOffer (String id, Entity entity)
  {
    String world = id.split (WORLD_SEPARATOR)[WORLD_NAME];
    String unique = id.split (WORLD_SEPARATOR)[UNIQUE_ID];
    Map<String, Offer> worldOffers = loadedOffers.get (world);
    Offer offer;
    
    if (null == worldOffers)
    {
      worldOffers = new HashMap<String, Offer>();
      loadedOffers.put (world, worldOffers);
    }
    
    offer = worldOffers.get (unique);
    
    if (null == offer)
    {
      try
      {
        offer = Json.getFromFile (new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + world + OFFER_FOLDER + "/" + 
                unique), Offer.class)[ONLY_OFFER];
        offer.npc = entity;
        worldOffers.put (unique, offer);
      }
      catch (IOException e)
      {
        // log
      }
    }
    
    return offer;
  }

  /**
   * Save the offer to file.
   * @param offer offer to save
   */
  public static void saveOffer (Offer offer)
  {
    String world = offer.offerId.split (WORLD_SEPARATOR)[WORLD_NAME];
    String unique = offer.offerId.split (WORLD_SEPARATOR)[UNIQUE_ID];
    
    new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + world + OFFER_FOLDER).mkdirs ();
    
    try
    {
      Json.saveJson (new File (Game.HOME + Game.GAME_FOLDER + 
              Game.WORLD_FOLDER + "/" + world + OFFER_FOLDER + "/" + 
              unique), offer);
    }
    catch (IOException e)
    {
      // log
    }
  }

  /**
   * Create a brand new offer
   * @param template template to base offer on
   * @param npc npc who offers
   * @param biome biome the npc is at
   * @param otherCreatures other entities nearby the npc, sans any players
   * @param worldName name of the world this offer is on
   * @param random random generator to use
   * @return the offer created
   */
  public static Offer newOffer (QuestTemplate template, Entity npc, Biome biome,
          List<Entity> otherCreatures, String worldName, Random random)
  {
    Offer offer = new Offer ();
    Requirement reqs = new Requirement ();
    List<Monster> monsters = Monster.getForBiome (biome);
    int count;
    
    offer.npc = npc;
    offer.npcName = npc.getName ();
    offer.templateId = template.getId ();
    offer.offerRate = template.getOfferRate ();
    offer.reward = Reward.getReward (npc.getStat (Statistic.LEVEL) +
            QUEST_LEVEL);
    
    // create kill requirements
    for (Entry<String, Integer[]> kill : template.getKill ().entrySet ())
    {
      for (Integer amount : kill.getValue ())
      {
        count = amount;
        if (count <= 0)
        {
          count = random.nextInt (MAX_KILL) + 1;
        }
        if (QuestTemplate.ANY.equals (kill.getKey ()))
        {
          reqs.addKill (monsters.get (random.nextInt (monsters.size ()))
                  .getName (), count);
        }
        else
        {
          reqs.addKill (kill.getKey (), count);
        }
      }
    }
    
    for (Entry<String, Integer[]> item : template.getItem ().entrySet ())
    {
      for (Integer amount : item.getValue ())
      {
        count = amount;
        if (count <= 0)
        {
          count = random.nextInt (MAX_ITEM) + 1;
        }
        if (QuestTemplate.ANY.equals (item.getKey ()))
        {
          reqs.addItem (Reward.getStackable (npc.getStat (Statistic.LEVEL))
                  .getName (),count);
        }
        else
        {
          reqs.addItem (item.getKey (), count);
        }
      }
    }
    
    // create talking requirements
    List<String> jobs = new ArrayList<String> ();
    Set<Entity> possibles = new HashSet<Entity> ();
    if (null != template.getTalk ()
            .get (npc.getData (EntityGenerator.NPC_CAREER)))
    {
      jobs.addAll (Arrays.asList(template.getTalk ()
              .get (npc.getData (EntityGenerator.NPC_CAREER))));
    }
    if (null != template.getTalk ().get (QuestTemplate.ANY))
    {
      jobs.addAll (Arrays.asList(template.getTalk ().get (QuestTemplate.ANY)));
    }
    
    for (int index = 0; index < jobs.size (); index++)
    {
      for (int i = 0; i < otherCreatures.size (); i++)
      {
        if (otherCreatures.get (i) == npc)
        {
          // can't talk to self!
        }
        else if (jobs.get (index).equals (QuestTemplate.ANY) && 
                null != otherCreatures.get (i)
                .getData (EntityGenerator.NPC_CAREER))
        {
          possibles.add (otherCreatures.get (i));
        }
        else if (jobs.get (index).equals (otherCreatures.get (i)
                .getData (EntityGenerator.NPC_CAREER)))
        {
          possibles.add (otherCreatures.get (i));
        }
      }
    }
    
    List<Entity> possibleList = new ArrayList<Entity> ();
    
    for (Entity ent : possibles)
    {
      possibleList.add (ent);
    }
    
    while (possibleList.size() > 0 &&
            reqs.getTalkNames ().length < template.getTalkCount ())
    {
      Entity talkTo = possibleList.get (random.nextInt (possibleList.size ()));
      reqs.addTalk (talkTo.getName ());
      possibleList.remove (talkTo);
    }
    
    offer.requirements = reqs;
    
    // set up id
    offer.offerId = worldName
            + WORLD_SEPARATOR + npc.getName () + template.getId ();
    for (String items : reqs.getItemNames ())
    {
      offer.offerId += items;
    }
    for (String kills : reqs.getKillNames ())
    {
      offer.offerId += kills;
    }
    for (String talk : reqs.getTalkNames ())
    {
      offer.offerId += talk;
    }
    
    Map<String, Offer> worldOffers = loadedOffers.get (worldName);
    if (null == worldOffers)
    {
      worldOffers = new HashMap<String, Offer> ();
      loadedOffers.put (worldName, worldOffers);
    }
    
    worldOffers.put (offer.offerId.split (WORLD_SEPARATOR)[1], offer);
    
    saveOffer (offer);
    
    return getOffer (offer.offerId, null);
  }

  /**
   * Clears all offers, saving them one by one
   */
  public static void clearOffers ()
  {
    for (Map<String, Offer> world : loadedOffers.values ())
    {
      for (Offer offer : world.values ())
      {
        saveOffer (offer);
      }
    }
    
    loadedOffers.clear ();
  }
}
