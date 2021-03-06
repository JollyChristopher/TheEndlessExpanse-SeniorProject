package halemaster.ee.world.entity;

import halemaster.ee.quest.Offer;
import halemaster.ee.quest.QuestTemplate;
import halemaster.ee.world.Area;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.terrain.tile.BiomeTile;
import halemaster.ee.world.terrain.tile.TileStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @name EntityGenerator
 * 
 * @version 0.0.0
 * 
 * @date Feb 11, 2014
 */
public class EntityGenerator 
{
  public static final String NPC_STRING = "npc.create";
  public static final String GROW = "grow";
  public static final String NPC_AREA = "NPC_AREA";
  public static final int CAREER_SIZE = 3;
  public static final String NPC_VAR = "npc";
  public static final String NPC_CAREER = "job";
  public static final int MAX_MONSTERS = 32;
  public static final int NPC_WEIGHT = 4;
  public static final float LEVEL_PER = 0.5f;
  
  /**
   * Generate all entities that would exist in the area.
   * @param events events for the world
   * @param location location to generate
   * @param tiles tiles in the location
   * @param random random to use to generate
   * @return array of entities, which can then be used for anything
   */
  public static Entity[] generate (EventHolder events, Area location, 
          BiomeTile[][] tiles, Random random, Entity nearestPlayer)
  {
    Entity[] returnEntities;
    Entity temp;
    List<Entity> npcs = new ArrayList<Entity>();
    List<Monster> possibleMonsters;
    String npcType;
    String[] npcSplit;
    Map<String, Set<Event>> npcEvents = new HashMap<String, Set<Event>>();
    int level = nearestPlayer.getStat (Statistic.LEVEL) + (int) (LEVEL_PER * 
            nearestPlayer.getLocation ().distance (location));
    
    // add all npcs from events
    for (Event event : events.getEvents (0, Integer.MAX_VALUE, location.getX (), 
            location.getX (), location.getY (), location.getY (), false))
    {
      if (event.getType ().getId ().startsWith (NPC_STRING))
      {
        // is an npc that is not locked out
        npcSplit = event.getType ().getId ().split ("\\.");
        if (CAREER_SIZE > npcSplit.length)
        {
          npcType = "";
        }
        else
        {
          npcType = npcSplit[CAREER_SIZE - 1];
          if (GROW.equals(npcType))
          {
            npcType = "";
          }
        }
        
        temp = new Entity ();
        temp.setLocation (location);
        temp.setData (NPC_CAREER, npcType);
        Monster npc = Monster.getForName ("npc_" + npcType);
        temp.setImages (npc.getImages (random.nextInt (npc.getImageCount ())));
        temp.setAnimation (npc.getInitialSprite ());
        temp.setName (event.getVariable ("npc").getPrint ());
        temp.setSpeed (npc.getSpeed ());
        temp.setType (EntityType.NPC.getId ());
        Area locationForEntity = getEntityLocation (tiles, random, 
                npc.getStructureForBiome (NPC_AREA));
        temp.setX (locationForEntity.getX ());
        temp.setY (locationForEntity.getY ());
        for (String ai : npc.getAi ())
        {
          temp.addAI (ai);
        }
        
        temp.setLevel (level);
        temp.setPower ((int) (level * npc.getPowerBase ()));
        temp.setAgility ((int) (level * npc.getAgilityBase ()));
        temp.setIntellect ((int) (level * npc.getIntellectBase ()));
        temp.setCurrentHp (temp.getStat (Statistic.HEALTH));
        
        npcs.add (temp);
      }
      else if (null != event.getVariable (NPC_VAR))
      {
        Set<Event> eventList = npcEvents.get (event.getVariable (NPC_VAR)
                .getPrint ());
        if (null == eventList)
        {
          eventList = new HashSet<Event> ();
          npcEvents.put (event.getVariable (NPC_VAR).getPrint (), eventList);
        }
        
        eventList.add (event);
      }
    }
    
    // spawn monsters
    possibleMonsters = Monster.getForBiome (BiomeClassifier.getBiome 
            (tiles[0][0].getBiomeId ()));
    if (null != possibleMonsters && npcs.size () * NPC_WEIGHT < MAX_MONSTERS)
    {
      returnEntities = new Entity[npcs.size () + random.nextInt (MAX_MONSTERS -
            npcs.size () * NPC_WEIGHT)];
    }
    else
    {
      returnEntities = new Entity[npcs.size ()];
    }
    
    for (int i = 0; i < npcs.size (); i ++)
    {
      returnEntities[i] = npcs.get (i);
    }
    
    for (int i = npcs.size (); i < returnEntities.length; i++)
    {
      Monster monster = possibleMonsters.get (random.nextInt 
              (possibleMonsters.size ()));
      Area locationForEntity = getEntityLocation (tiles, random, monster
              .getStructureForBiome (BiomeClassifier.getBiome (tiles[0][0]
              .getBiomeId ()).getName ()));
      while (null == locationForEntity && possibleMonsters.size () > 0)
      {
        possibleMonsters.remove (monster);
        monster = possibleMonsters.get (random.nextInt 
                (possibleMonsters.size ()));
        locationForEntity = getEntityLocation (tiles, random, monster
                .getStructureForBiome (BiomeClassifier.getBiome (tiles[0][0]
                .getBiomeId ()).getName ()));
      }
      if (null != locationForEntity)
      {
        temp = new Entity ();
        temp.setLocation (location);
        temp.setImages (monster.getImages (random.nextInt (monster.getImageCount ())));
        temp.setAnimation (monster.getInitialSprite ());
        temp.setName (monster.getName ());
        temp.setSpeed (monster.getSpeed ());
        temp.setType (EntityType.MONSTER.getId ());
        temp.setX (locationForEntity.getX ());
        temp.setY (locationForEntity.getY ());
        temp.setDefaultAttack (Statistic.valueOf (monster.getDefaultAttack ()));
        if (null != monster.getAttackAnimation ())
        {
          temp.setDefaultAttackAnimation (monster.getAttackAnimation ());
        }
        for (String ai : monster.getAi ())
        {
          temp.addAI (ai);
        }

        temp.setLevel (level);
        temp.setPower ((int) (level * monster.getPowerBase ()));
        temp.setAgility ((int) (level * monster.getAgilityBase ()));
        temp.setIntellect ((int) (level * monster.getIntellectBase ()));
        temp.setCurrentHp (temp.getStat (Statistic.HEALTH));

        returnEntities[i] = temp;
      }
    }
    
    for (int i = 0; i < npcs.size (); i ++)
    {
      String offerIds = "";
      List<QuestTemplate> possibleQuests = QuestTemplate.possibleQuests 
              (returnEntities[i], npcEvents.get (returnEntities[i].getName ()), 
              BiomeClassifier.getBiome (tiles[0][0].getBiomeId ()), 
              Arrays.asList(returnEntities));
      
      for (QuestTemplate template : possibleQuests)
      {
        Offer offer = Offer.newOffer (template, returnEntities[i], 
                BiomeClassifier.getBiome (tiles[0][0].getBiomeId ()), 
                Arrays.asList(returnEntities), events.getWorldName (), random);
        offerIds += offer.getOfferId () + Offer.OFFER_SEPARATOR;
      }
      
      returnEntities[i].setData (Offer.OFFER_LIST, offerIds);
    }
    
    return returnEntities;
  }
  
  private static Area getEntityLocation (BiomeTile[][] tiles, Random random,
          int[] structureId)
  {
    Area found = null;
    boolean noPlace;
    List<Area> possible = new ArrayList<Area>();
    
    for (int x = 0; x < tiles.length; x++)
    {
      for (int y = 0; y < tiles[x].length; y++)
      {
        noPlace = false;
        for (int i = 0; i < tiles[x][y].getTileIds ().length; i++)
        {
          if (BiomeClassifier.getBiome (tiles[x][y].getBiomeId ())
                  .getGeneration ().getTiles ()[tiles[x][y].getTileIds ()[i]]
                  .blocks (TileStyle.Direction.ABOVE))
          {
            noPlace = true;
          }
        }
        if (!noPlace)
        {
          noPlace = true;
          for (int i = 0; noPlace && i < structureId.length; i++)
          {
            if (structureId[i] == tiles[x][y].getStructureId ())
            {
              noPlace = false;
            }
          }
        }
        
        if (!noPlace)
        {
          possible.add (new Area (x, y));
        }
      }
    }
    
    if (possible.size () > 0)
    {
      found = possible.get (random.nextInt (possible.size ()));
    }
    
    return found;
  }
}
