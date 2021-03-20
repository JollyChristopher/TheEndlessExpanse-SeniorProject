package halemaster.ee.world.history.event.structures.types;

import halemaster.ee.Game;
import halemaster.ee.localization.NameGen;
import halemaster.ee.state.Hud;
import halemaster.ee.world.Area;
import halemaster.ee.world.faction.DiplomaticGroup;
import halemaster.ee.world.faction.Faction;
import halemaster.ee.world.faction.Settlement;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.structures.EventObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @name EventFaction
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventFaction implements EventObject
{
  public static final Logger LOGGER = 
          Logger.getLogger (EventFaction.class.getName ());
  public static final String ID = "faction";
  public static final String BUILD = "BUILD";
  public static final String FALL = "FALL";
  public static final String DIPLOMACY_BETTER = "DIPLOMACY_BETTER";
  public static final String DIPLOMACY_ENEMY = "DIPLOMACY_ENEMY";
  public static final String DIPLOMACY_WORSE = "DIPLOMACY_WORSE";
  public static final String NEW_LEADER = "NEW_LEADER";
  public static final String NEW_SETTLEMENT = "NEW_SETTLEMENT";
  public static final String FALL_SETTLEMENT = "FALL_SETTLEMENT";
  public static final String REBUILD_SETTLEMENT = "REBUILD_SETTLEMENT";
  public static final int INCREASE = 100;
  public static final int DECREASE = -100;
  private String name = "";
  private Event event;
  private Random random;
  
  public void setEvent (Event event)
  {
    this.event = event;
  }
  
  public void init (Random random)
  {
    this.random = random;
  }

  public String getValue ()
  {
    return this.name;
  }
  
  public String getPrint ()
  {
    return this.name;
  }
  
  public void setValue (String value)
  {
    this.name = value;
  }
  
  public String getType ()
  {
    return ID;
  }

  public void modify (EventObject modifier, String type)
  {
    /*
     * BUILD - self
     * FALL - self
     * DIPLOMACY_BETTER - other faction
     * DIPLOMACY_ENEMY - other faction
     * DIPLOMACY_WORSE - other faction
     * NEW_LEADER - person
     * NEW_SETTLEMENT - settlement
     * FALL_SETTLEMENT - settlement
     * REBUILD_SETTLEMENT - settlment
     */
    if (BUILD.equals (type) && modifier == this)
    {
      try
      {
        this.name = NameGen.getName (this.random, ID);
        try
        {
          while (null != Faction.getFaction (this.event.getHolder ()
                  .getWorldName (), this.name))
          {
            this.name = NameGen.getName (this.random, ID);
          }
        }
        catch (IOException e)
        {

        }
        
        Faction.addFaction (this.event.getHolder ().getWorldName (), this.name);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to add faction", e);
      }
    }
    else if (FALL.equals (type) && modifier == this)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        for (Settlement settle : currentFaction.getSettlements ())
        {
          removeSettlementTag (settle);
        }
        Faction.removeFaction (this.event.getHolder ().getWorldName (),
                this.name);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to delete faction", e);
      }
    }
    else if (DIPLOMACY_BETTER.equals (type) && modifier instanceof EventFaction)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        Faction otherFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), modifier.getPrint ());
        
        if (null == currentFaction || null == otherFaction)
        {
          throw new IOException ("Couldn't find one of the factions");
        }
        
        currentFaction.modifyDiplomacy (otherFaction, INCREASE);
        otherFaction.modifyDiplomacy (currentFaction, INCREASE);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to increase relations", e);
      }
    }
    else if (DIPLOMACY_WORSE.equals (type) && modifier instanceof EventFaction)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        Faction otherFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), modifier.getPrint ());
        
        currentFaction.modifyDiplomacy (otherFaction, DECREASE);
        otherFaction.modifyDiplomacy (currentFaction, DECREASE);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to decrease relations", e);
      }
    }
    else if (DIPLOMACY_ENEMY.equals (type) && modifier instanceof EventFaction)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        Faction otherFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), modifier.getPrint ());
        
        currentFaction.changeDiplomacy (otherFaction, 
                DiplomaticGroup.ENEMY.getLow ());
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to create enemies", e);
      }
    }
    else if (NEW_LEADER.equals (type) && modifier instanceof EventPerson)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        
        currentFaction.changeLeader (modifier.getPrint ());
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to appoint a new leader", e);
      }
    }
    else if (NEW_SETTLEMENT.equals (type) && modifier instanceof
            EventSettlement)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        EventSettlement settle = (EventSettlement) modifier;
        
        String temp = NameGen.getName (this.random, EventSettlement.ID);
        try
        {
          while (null != Faction.getFaction (this.event.getHolder ()
                  .getWorldName (), this.name).getSettlement (temp))
          {
            temp = NameGen.getName (this.random, EventSettlement.ID);
          }
        }
        catch (IOException e)
        {

        }
        settle.setName (temp);
        
        currentFaction.addSettlement (settle.getCenter ().getX (), 
                settle.getCenter ().getY (), settle.getSize (), 
                settle.getPrint ());
        settle.setFaction (this.name);
        // new icon
        addSettlementTag (settle);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to create settlement", e);
      }
    }
    else if (FALL_SETTLEMENT.equals (type) && modifier instanceof
            EventSettlement)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        EventSettlement settle = (EventSettlement) modifier;
        
        currentFaction.removeSettlement (settle.getPrint ());
        // remove icon
        removeSettlementTag (settle);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to destroy settlement", e);
      }
    }
    else if (REBUILD_SETTLEMENT.equals (type) && modifier instanceof
            EventSettlement)
    {
      try
      {
        Faction currentFaction = Faction.getFaction (this.event.getHolder ()
                .getWorldName (), this.name);
        EventSettlement settle = (EventSettlement) modifier;
        
        currentFaction.addSettlement (settle.getCenter ().getX (), 
                settle.getCenter ().getY (), settle.getSize (), 
                settle.getPrint ());
        // add icon
        addSettlementTag (settle);
      }
      catch (IOException e)
      {
        LOGGER.log (Level.WARNING, "Failed to rebuild settlement", e);
      }
    }
  }
  
  public void addSettlementTag (EventSettlement settle) throws IOException
  {
    File tagFolder = new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + this.event.getHolder ()
                .getWorldName () + Hud.TAG_FOLDER);
    File imageFile = new File (tagFolder.getPath () + "/" + settle
            .getCenter ().toString () + Hud.TAG_SEPARATOR + 
            settle.getPrint () + ".png");
    BufferedImage townImage = ImageIO.read (new File ("assets/Interface/"
            + "Images/tag.png"));
    tagFolder.mkdirs ();

    ImageIO.write(townImage, "png", imageFile);
  }
  
  public void removeSettlementTag (Settlement settle)
  {
    File tagFolder = new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + this.event.getHolder ()
                .getWorldName () + Hud.TAG_FOLDER);
    File imageFile;

    for (Area area : settle.getAreas ())
    {
      imageFile = new File (tagFolder.getPath () + "/" + area.toString () + 
              Hud.TAG_SEPARATOR + settle.getName () + ".png");
      if (imageFile.exists ())
      {
        imageFile.delete ();
      }
    }
  }
  
  public void removeSettlementTag (EventSettlement settle)
  {
    File tagFolder = new File (Game.HOME + Game.GAME_FOLDER + 
                Game.WORLD_FOLDER + "/" + this.event.getHolder ()
                .getWorldName () + Hud.TAG_FOLDER);
    File imageFile = new File (tagFolder.getPath () + "/" + settle
            .getCenter ().toString () + Hud.TAG_SEPARATOR + 
            settle.getPrint () + ".png");

    imageFile.delete ();
  }
}
