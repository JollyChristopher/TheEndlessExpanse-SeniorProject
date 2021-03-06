package halemaster.ee.quest;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.item.ItemSet;
import halemaster.ee.world.entity.Entity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @name Quest
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 */
public class Quest 
{
  public static final String PLAYER_SEPARATOR = Offer.WORLD_SEPARATOR;
  public static final String QUEST_FOLDER = "/quests";
  private String questId;
  private String offerId;
  private Requirement finishedRequirements;

  public String getQuestId ()
  {
    return questId;
  }

  public void setQuestId (String questId)
  {
    this.questId = questId;
  }

  public String getOfferId ()
  {
    return offerId;
  }

  public void setOfferId (String offerId)
  {
    this.offerId = offerId;
  }
  
  public Requirement getFinishedRequirements ()
  {
    return this.finishedRequirements;
  }
  
  /**
   * Determine if the quest is done or not
   * @return true if quest is done
   */
  public boolean isDone ()
  {
    return this.finishedRequirements.checkItemEmpty () && 
            this.finishedRequirements.checkKillEmpty () && 
            this.finishedRequirements.checkTalkEmpty ();
  }
  
  /**
   * Whenever the quest owner kills an entity, this should be called with that
   * entity.
   * @param entity entity that was killed.
   */
  public void onKill (Entity entity)
  {
    if (this.finishedRequirements.hasKill (entity.getName ()))
    {
      this.finishedRequirements.subtractKill (entity.getName (), 1);
    }
  }
  
  /**
   * Whenever the quest owner talks to an entity, this should be called with
   * that entity
   * @param entity entity that was talked to.
   */
  public void onTalk (Entity entity)
  {
    if (this.finishedRequirements.hasTalk (entity.getName ()))
    {
      this.finishedRequirements.removeTalk (entity.getName ());
    }
  }
  
  /**
   * Whenever the quest owner picks up an item, either through buying, finding,
   * or removing from equiptment, this should be called with that particular
   * stack (not the stack that this is merged with later!)
   * @param item item stack that was picked up
   */
  public void onPickup (ItemSet item)
  {
    if (this.finishedRequirements.hasItem (item.getName ()))
    {
      this.finishedRequirements.subtractItem (item.getName (), 
              item.getAmount ());
    }
  }

  /**
   * Whenever the quest owner loses an item, either through selling, dropping,
   * or adding to equiptment, this should be called with that particular
   * stack (not the stack that it came from)
   * @param item item stack that was lost
   */
  public void onLose (ItemSet item)
  {
    if (this.finishedRequirements.hasItem (item.getName ()))
    {
      this.finishedRequirements.addItem (item.getName (), 
              item.getAmount ());
    }
  }

  /**
   * Load the quest with the given id from file
   * @param id id of quest to load
   * @return the loaded quest
   */
  public static Quest loadQuest (String id)
  {
    Quest quest = new Quest ();
    String player = id.split (PLAYER_SEPARATOR)[0];
    String world = id.split (PLAYER_SEPARATOR)[1];
    String unique = id.split (PLAYER_SEPARATOR)[2];
    
    try
    {
      quest = Json.getFromFile (new File (Game.HOME + Game.GAME_FOLDER + 
              Game.PLAYER_FOLDER + "/" + player + QUEST_FOLDER +
              "/" + world + "/" + unique), Quest.class)[0];
    }
    catch (IOException e)
    {
      // log
    }
    
    return quest;
  }
  
  /**
   * Load all quest of the given player/world combination
   * @param player player to load for
   * @param world world to load from
   * @return 
   */
  public static List<Quest> loadFrom (String player, String world)
  {
    List<Quest> quests = new ArrayList<Quest> ();
    File folder = new File (Game.HOME + Game.GAME_FOLDER + 
              Game.PLAYER_FOLDER + "/" + player + QUEST_FOLDER +
              "/" + world);
    if (folder.isDirectory ())
    {
      for (File file : folder.listFiles ())
      {
        quests.add (loadQuest (player + PLAYER_SEPARATOR + world + 
                PLAYER_SEPARATOR + file.getName ()));
      }
    }
    
    return quests;
  }

  /**
   * Save the quest to file
   * @param quest quest to save
   */
  public static void saveQuest (Quest quest)
  {
    String player = quest.questId.split (PLAYER_SEPARATOR)[0];
    String world = quest.questId.split (PLAYER_SEPARATOR)[1];
    String unique = quest.questId.split (PLAYER_SEPARATOR)[2];
    
    new File (Game.HOME + Game.GAME_FOLDER + 
              Game.PLAYER_FOLDER + "/" + player + QUEST_FOLDER +
              "/" + world).mkdirs ();
    
    try
    {
      Json.saveJson (new File (Game.HOME + Game.GAME_FOLDER + 
              Game.PLAYER_FOLDER + "/" + player + QUEST_FOLDER +
              "/" + world + "/" + unique), quest);
    }
    catch (IOException e)
    {
      // log
    }
  }

  /**
   * Create a new quest for a given player and offer
   * @param playerName player that is getting quest
   * @param offer offer to use for the quest
   * @return the new quest.
   */
  public static Quest newQuest (String playerName, Offer offer)
  {
    Quest quest = new Quest ();
    
    quest.offerId = offer.getOfferId ();
    quest.finishedRequirements = offer.getRequirements ().copy ();
    quest.questId = playerName + PLAYER_SEPARATOR + quest.offerId;
    
    return quest;
  }
  
  /**
   * Deletes the given quest from file
   * @param quest quest to delete.
   */
  public static void deleteQuest (Quest quest)
  {
    String player = quest.questId.split (PLAYER_SEPARATOR)[0];
    String world = quest.questId.split (PLAYER_SEPARATOR)[1];
    String unique = quest.questId.split (PLAYER_SEPARATOR)[2];
    File deleteFile = new File (Game.HOME + Game.GAME_FOLDER + 
              Game.PLAYER_FOLDER + "/" + player + QUEST_FOLDER +
              "/" + world + "/" + PLAYER_SEPARATOR + unique);
    
    deleteFile.delete ();
  }
}
