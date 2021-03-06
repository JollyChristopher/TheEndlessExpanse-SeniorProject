package halemaster.ee.world.entity;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.Area;
import halemaster.ee.world.micro.WorldHolder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @name EntityHolder
 * 
 * @version 0.0.0
 * 
 * @date Feb 11, 2014
 */
public class EntityHolder 
{
  public static final String ENTITY_FOLDER = "/entities";
  public static final String ENTITY_EXT = "npc";
  
  private ConcurrentHashMap<Area, List<Entity>> entities = 
          new ConcurrentHashMap<Area, List<Entity>>();
  private ConcurrentLinkedQueue<Area> removeAreas =
          new ConcurrentLinkedQueue<Area> ();
  private WorldHolder holder;
  
  public EntityHolder (WorldHolder holder)
  {
    this.holder = holder;
  }
  
  public WorldHolder getHolder ()
  {
    return this.holder;
  }
  
  /**
   * Get the entities at the given location
   * @param x x to get entities at
   * @param y y to get entities at
   * @return 
   */
  public List<Entity> getEntities (int x, int y)
  {
    return this.entities.get (new Area (x, y));
  }
  
  /**
   * Get all entities in the holder
   * @return all entities in the holder
   */
  public List<Entity> getAllEntities ()
  {
    List<Entity> allEntities = new ArrayList<Entity> ();
    
    for (List<Entity> entitySet : this.entities.values ())
    {
      for (Entity entity : entitySet)
      {
        allEntities.add (entity);
      }
    }
    
    return allEntities;
  }
  
  /**
   * Get all areas in the holder
   * @return all areas in the holder
   */
  public List<Area> getAreas ()
  {
    List<Area> allAreas = new ArrayList<Area> ();
    
    for (Area area : this.entities.keySet ())
    {
      allAreas.add (area);
    }
    
    return allAreas;
  }
  
  /**
   * Add the given entities to the holder
   * @param entities entities to add 
   */
  public void addEntity (Entity ... entities)
  {
    List<Entity> entitySet;
    for (Entity entity : entities)
    {
      entitySet = this.entities.get (entity.getLocation ());
      if (null == entitySet)
      {
        entitySet = new ArrayList<Entity> ();
        this.entities.put (entity.getLocation (), entitySet);
      }
      
      entitySet.add (entity);
      entity.initialize (this.holder.getPlayerState ().getGame (), this);
      
      this.holder.getPlayerState ().getHud ().addChatUser (entity);
    }
  }
  
  /**
   * Remove the given entities from the holder
   * @param entities entities to remove
   */
  public void removeEntity (Entity ... entities)
  {
    List<Entity> entitySet;
    for (Entity entity : entities)
    {
      entitySet = this.entities.get (entity.getLocation ());
      if (null != entitySet)
      {
        entitySet.remove (entity);
        if (0 == entitySet.size ())
        {
          this.removeAreas.add (entity.getLocation ());
        }
        entity.unload (this.holder.getPlayerState ().getGame ());
      }
      
      this.holder.getPlayerState ().getHud ().removeChatUser (entity);
    }
  }
  
  /**
   * Move the given entity to a different location
   * @param entity entity to move
   * @param loc location to move entity
   */
  public void moveEntity (Entity entity, Area loc)
  {
    List<Entity> entitySet;
    entitySet = this.entities.get (entity.getLocation ());
    if (null != entitySet)
    {
      entitySet.remove (entity);
      if (0 == entitySet.size ())
      {
        this.removeAreas.add (entity.getLocation ());
      }
    }
    
    entity.setLocation (loc);
    entitySet = this.entities.get (entity.getLocation ());
    if (null == entitySet)
    {
      entitySet = new ArrayList<Entity> ();
      this.entities.put (entity.getLocation (), entitySet);
    }

    entitySet.add (entity);
  }
  
  /**
   * Remove all entities at the given location
   * @param x x coordinate to remove at
   * @param y y coordinate to remove at
   * @return list of entities at that location
   */
  public Entity[] removeEntitiesAt (int x, int y)
  {
    List<Entity> removed;
    List<Entity> addBack = new ArrayList<Entity>();
    Entity[] removedArray = null;
    
    removed = this.entities.get (new Area (x, y));
    if (null != removed)
    {
      for (int i = 0; i < removed.size (); i++)
      {
        Entity temp = removed.get (i);
        if (EntityType.getById (temp.getType ()).equals (EntityType.PLAYER))
        {
          removed.remove (temp);
          i--;
          addBack.add (temp);
        }
      }
      
      if (addBack.size () > 0)
      {
        this.entities.put (new Area (x, y), addBack);
      }
      else
      {
        this.removeAreas.add (new Area (x, y));
      }
      
      removedArray = new Entity[removed.size ()];
      for (int i = 0; i < removedArray.length; i++)
      {
        removedArray[i] = removed.get (i);
        removed.get (i).unload (this.holder.getPlayerState ().getGame ());
        this.holder.getPlayerState ().getHud ().removeChatUser (removed.get (i));
      }
    }
    
    return removedArray;
  }
  
  /**
   * update the entity holder
   * @param tpf delta since last update
   */
  public void update (float tpf)
  {
    int removal = this.removeAreas.size ();
    
    for (int i = 0; i < removal; i++)
    {
      Area location = this.removeAreas.poll ();
      this.entities.remove (location);
    }
    
    for (Entity entity : getAllEntities ())
    {
      entity.update (tpf);
    }
  }
  
  /**
   * Save the entities to file
   * @param world world to save in
   * @param x x coordinate
   * @param y y coordinate
   */
  public void saveEntities (String world, int x, int y)
  {
    File areaFolder = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            ENTITY_FOLDER);
    File npcFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            ENTITY_FOLDER + "/" + x + "," + y + "." + ENTITY_EXT);
    List<Entity> entityList, saveList;
    Entity[] saveEntities;
    
    areaFolder.mkdirs ();
    entityList = this.entities.get (new Area (x, y));
    
    if (null != entityList)
    {
      saveList = new ArrayList<Entity>();
      for (Entity entity : entityList)
      {
        if (EntityType.getById (entity.getType ()).getLocation ()
                .equals (EntityType.DEFAULT_LOCATION))
        {
          saveList.add (entity);
        }
      }
      
      saveEntities = new Entity[saveList.size ()];
      for (int i = 0; i < saveEntities.length; i++)
      {
        saveEntities[i] = saveList.get (i);
        saveEntities[i].prepForSaving ();
      }

      try
      {
        Json.saveJson (npcFile, saveEntities);
      }
      catch (IOException e)
      {
        // log
      }
    }
    else if (npcFile.exists ())
    {
      npcFile.delete ();
    }
  }
  
  /**
   * Load entities from the location
   * @param world world to load from
   * @param x x coordinate to load
   * @param y y coordiante to load
   * @return array of entities at the given location
   */
  public static Entity[] loadEntities (String world, int x, int y)
  {
    Entity[] entities = null;
    File npcFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            ENTITY_FOLDER + "/" + x + "," + y + "." + ENTITY_EXT);
    
    try
    {
      entities = Json.getFromFile (npcFile, Entity.class);
    }
    catch (IOException e)
    {
      // log
    }
    
    return entities;
  }
}
