package halemaster.ee.world.micro;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jme3.math.Vector2f;
import halemaster.ee.Game;
import halemaster.ee.Sprite;
import halemaster.ee.item.ItemSet;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.Config;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityHolder;
import halemaster.ee.world.history.HistoryGenerator;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.history.event.EventIO;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import halemaster.ee.world.terrain.tile.BiomeTile;
import halemaster.ee.world.terrain.tile.Tile;
import halemaster.ee.world.terrain.tile.Tile.TileDirection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name WorldHolder
 * 
 * @version 0.0.0
 * 
 * @date Jan 25, 2014
 */
public class WorldHolder implements Runnable
{
  public static final int THREAD_COUNT = 1;
  public static final int TIME_INCREMENT = 6;
  public static final int[] MAX_EVENTS = {0,0,1,1,2,3};
  public static final long SLEEP_TIME = 100;
  public static final int NEAR_RADIUS = 5;
  public static final Logger LOGGER = 
          Logger.getLogger (WorldHolder.class.getName ());
  
  private ConcurrentHashMap<String, Entity> players = 
          new ConcurrentHashMap<String, Entity>();
  private ConcurrentHashMap<Area, BiomeTile[][]> currentAreas =
          new ConcurrentHashMap<Area, BiomeTile[][]>();
  private Map<Area, Map<Area, List<Sprite>>> terrain = 
          new HashMap<Area, Map<Area, List<Sprite>>>();
  private EventHolder currentEvents;
  private EventTypeHolder possibleEvents;
  private EntityHolder entities = new EntityHolder (this);
  private List<GenerationSequence> currentGenerators =
          new ArrayList<GenerationSequence> ();
  private ExecutorService runners = Executors.newFixedThreadPool (THREAD_COUNT, 
          new ThreadFactoryBuilder().setNameFormat("TEE-generator-%d").build());
  private volatile boolean running = true;
  private PlayerState playerState;
  private Map<ItemSet, Sprite> groundItems = new HashMap<ItemSet, Sprite> ();

  public WorldHolder (PlayerState player,
          EventTypeHolder types, EventHolder events)
  {
    this.possibleEvents = types;
    this.currentEvents = events;
    this.playerState = player;
  }
  
  public void addGroundItemNearEntity (ItemSet groundItem, Entity entity)
  {
    Random random = new Random ();
    int x = entity.getExactLocation ().getX () + random.nextInt (NEAR_RADIUS * 
            2) - NEAR_RADIUS;
    int y = entity.getExactLocation ().getY () + random.nextInt (NEAR_RADIUS *
            2) - NEAR_RADIUS;
    
    addGroundItem (groundItem, x, y);
  }
  
  public void addGroundItem (ItemSet groundItem, int x, int y)
  {
    Sprite itemSprite = new Sprite (groundItem.getName () + x + "," + y, 
            this.playerState.getGame ().getAssetManager (), 0f, 
            Entity.ENTITY_LAYER - 1);
    itemSprite.move (x * AreaGenerator.TILE_WIDTH, y * 
            AreaGenerator.TILE_HEIGHT);
    itemSprite.addAnimation ("default", new String[]{groundItem.getItem ()
            .getImage ()});
    itemSprite.setAnimation ("default");
    this.playerState.getGame ().attachSprite (itemSprite, Area.ANYWHERE);
    this.groundItems.put (groundItem, itemSprite);
  }
  
  public void update (float tpf)
  {
    this.entities.update (tpf);
    List<ItemSet> pickups = new ArrayList<ItemSet> ();
    for (Entry<ItemSet, Sprite> grounders : this.groundItems.entrySet ())
    {
      Vector2f groundDist = new Vector2f (grounders.getValue ().getImage ()
              .getLocalTranslation ().x, grounders.getValue ().getImage ()
              .getLocalTranslation ().z);
      if (groundDist.distance (this.playerState.getPlayer ()
              .getAbsoluteLocation ()) <= AreaGenerator.TILE_WIDTH)
      {
        pickups.add (grounders.getKey ());
      }
    }
    
    for (ItemSet pick : pickups)
    {
      this.playerState.getGame ().detachSprite (this.groundItems.remove (pick),
              Area.ANYWHERE);
      if (!this.playerState.getPlayer ().getInventory ().addItem (pick))
      {
        addGroundItemNearEntity (pick, this.playerState.getPlayer ());
      }
    }
  }
  
  public EventHolder getEvents ()
  {
    return this.currentEvents;
  }
  
  public PlayerState getPlayerState ()
  {
    return this.playerState;
  }
  
  public void setPlayer (Entity player)
  {
    if (null != player)
    {
      this.players.remove (player.getName ());
      this.players.put (player.getName (), player);
    }
  }

  public EntityHolder getEntityManager ()
  {
    return entities;
  }
  
  public void stop ()
  {
    this.running = false;
  }
  
  public BiomeTile[][] getTiles (Area location)
  {
    return this.currentAreas.get (location);
  }
  
  /**
   * Get tile based on absolute location
   * @param location location of tile
   * @return tile
   */
  public BiomeTile getTile (Area location)
  {
    Area spot = new Area ((int) 
            (location.getX () / AreaGenerator.AREA_SIZE), (int) 
            (location.getY () / AreaGenerator.AREA_SIZE));
    BiomeTile[][] tiles = this.currentAreas.get (spot);
    
    if (null != tiles)
    {
      return tiles[(int) (location.getX () % AreaGenerator.AREA_SIZE)][(int) 
            (location.getY () % AreaGenerator.AREA_SIZE)];
    }
    else
    {
      return null;
    }
  }
  
  private boolean isGenerated (Area location)
  {
    boolean generated = false;
    
    if (new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + this.currentEvents.getWorldName ()
            + PlayerState.LOCATION_FOLDER + AreaGenerator.AREA_FOLDER + "/" + 
            location.getX () + "," + location.getY () +
            "." + MacroTerrainGenerator.TERRAIN_EXT).exists ())
    {
      generated = true;
    }
    
    return generated;
  }
  
  private boolean isLoaded (Area location)
  {
    return this.currentAreas.containsKey (location);
  }
  
  private Area findGenerationLocation ()
  {
    Area area = null;
    Area temp, nextTemp;
    Queue<Area> next = new ArrayDeque<Area> ();
    Set<Area> unusable = new HashSet<Area> ();
    Set<Area> generating = new HashSet<Area> ();
    
    for (GenerationSequence seq : this.currentGenerators)
    {
      generating.add (seq.getHistory ().getLocation ());
    }
    
    for (Entry<String, Entity> player : this.players.entrySet ())
    {
      next.add (player.getValue ().getLocation ());
    }
    
    while (null == area && !next.isEmpty ())
    {
      temp = next.poll ();
      
      if (isGenerated (temp) || generating.contains (temp))
      {
        unusable.add (temp);
        
        nextTemp = new Area (temp.getX (), temp.getY () + 1);
        if (!unusable.contains (nextTemp))
        {
          next.add (nextTemp);
        }
        nextTemp = new Area (temp.getX (), temp.getY () - 1);
        if (!unusable.contains (nextTemp))
        {
          next.add (nextTemp);
        }
        nextTemp = new Area (temp.getX () + 1, temp.getY ());
        if (!unusable.contains (nextTemp))
        {
          next.add (nextTemp);
        }
        nextTemp = new Area (temp.getX () - 1, temp.getY ());
        if (!unusable.contains (nextTemp))
        {
          next.add (nextTemp);
        }
      }
      else
      {
        area = temp;
      }
    }
    
    return area;
  }
  
  private List<Area> getLoaded ()
  {
    List<Area> loaded = new ArrayList<Area>();
    
    for (Entry<Area, BiomeTile[][]> loadedAreas : this.currentAreas.entrySet ())
    {
      loaded.add (loadedAreas.getKey ());
    }
    
    return loaded;
  }
  
  /**
   * Load the given location to the system.
   * 
   * @param location location to load
   */
  public void loadLocation (Area location) throws IOException, 
          InstantiationException, IllegalAccessException
  {
    List<Sprite> tileSprites;
    Sprite sprite;
    BiomeTile[][] tiles;
    Map<Area, List<Sprite>> sprites = new HashMap<Area, List<Sprite>>();
    Tile tile;
    Biome biome;
    Entity[] entity;
    TileDirection dir;
    boolean left, up, right, down;
    
    EventIO.getEvents (this.currentEvents.getWorldName (), 
                  this.currentEvents, location.getX (), location.getX (), 
                  location.getY (), location.getY ());
    this.currentAreas.put (location, AreaGenerator.loadTiles (location, 
            this.currentEvents.getWorldName ()));
    
    this.terrain.put (location, sprites);
    tiles = this.currentAreas.get (location);
    biome = BiomeClassifier.getBiome (tiles[0][0].getBiomeId ());
    for (int x = 0; x < tiles.length; x++)
    {
      for (int y = 0; y < tiles[x].length; y++)
      {
        tileSprites = new ArrayList<Sprite>();
        for (Integer id : tiles[x][y].getTileIds ())
        {
          tile = biome.getGeneration ().getTiles ()[id];
          left = up = right = down = false;
          
          // determine tile direction of nearby tiles
          if (x > 0)
          {
            for (Integer neighborId : tiles[x - 1][y].getTileIds ())
            {
              if (neighborId == id)
              {
                left = true;
              }
            }
          }
          if (y > 0)
          {
            for (Integer neighborId : tiles[x][y - 1].getTileIds ())
            {
              if (neighborId == id)
              {
                up = true;
              }
            }
          }
          if (x < tiles.length - 1)
          {
            for (Integer neighborId : tiles[x + 1][y].getTileIds ())
            {
              if (neighborId == id)
              {
                right = true;
              }
            }
          }
          if (y < tiles[x].length - 1)
          {
            for (Integer neighborId : tiles[x][y + 1].getTileIds ())
            {
              if (neighborId == id)
              {
                down = true;
              }
            }
          }
          
          dir = TileDirection.getFromDirection (left, up, right, down);
          
          sprite = new Sprite (tiles[x][y].getBiomeId () + ";" + id + ";" + 
                  dir.name (), this.playerState.getGame ().getAssetManager (), 
                  tile.getSpeed (), tile.getLayer ());
          sprite.addAnimation (Tile.TILE_ANIMATION, tile.getImages (dir));
          sprite.setAnimation (Tile.TILE_ANIMATION);
          tileSprites.add (sprite);
          sprite.move (location.getX () * AreaGenerator.AREA_SIZE * 
                  AreaGenerator.TILE_WIDTH + x * AreaGenerator.TILE_WIDTH, 
                  location.getY () * AreaGenerator.AREA_SIZE * 
                  AreaGenerator.TILE_HEIGHT + y * AreaGenerator.TILE_HEIGHT);
          this.playerState.getGame ().attachSprite (sprite, location);
        }
        sprites.put (new Area (x, y), tileSprites);
      }
    }
    
    entity = EntityHolder.loadEntities (this.currentEvents.getWorldName (), 
            location.getX (), location.getY ());
    if (null != entity)
    {
      this.entities.addEntity (entity);
    }
  }
  
  /**
   * Unload the given location from the system.
   * 
   * @param location location to unload
   */
  public void unloadLocation (Area location)
  {    
    this.currentEvents.removeEvents (0, Integer.MAX_VALUE, location.getX (),
                  location.getX (), location.getY (), location.getY ());
    this.currentAreas.remove (location);
    
    for (Entry<Area, List<Sprite>> spriteList : 
            this.terrain.get (location).entrySet ())
    {
      for (Sprite sprite : spriteList.getValue ())
      {
        this.playerState.getGame ().detachSprite (sprite, location);
      }
    }
    
    this.entities.saveEntities (this.currentEvents.getWorldName (), 
            location.getX (), location.getY ());
    this.entities.removeEntitiesAt (location.getX (), location.getY ());
  }
  
  public void run ()
  {
    HistoryGenerator createdEvents;
    HistoryGenerator newEvents;
    Area area;
    Set<Area> loadLocations;
    Entity[] tempEntities;
    Entity closestPlayer;
    
    while (this.running)
    {
      // look for finished generators, remove them from the map, and then add
      // the areas from the generated map. Save events
      for (int i = 0; i < this.currentGenerators.size (); i++)
      {
        if (this.currentGenerators.get (i).isDone ())
        {
          createdEvents = this.currentGenerators.get (i).getHistory ();
          try
          {
            for (Event event : this.currentEvents.getEvents (0, Integer.valueOf 
                    (Config.getValue (this.currentEvents.getWorldName (), 
                    HistoryGenerator.TIME_KEY)), createdEvents.getLeft (),
                    createdEvents.getRight (), createdEvents.getTop (), 
                    createdEvents.getBottom ()))
            {
              createdEvents.getCreated ().addEvent (event);
            }
            EventIO.saveEvents (createdEvents.getCreated (), 
                    this.currentEvents.getWorldName (),
                  createdEvents.getTop (), 
                  createdEvents.getBottom (), 
                  createdEvents.getLeft (), 
                  createdEvents.getRight ());
            area = new Area (createdEvents.getLeft (), 
                    createdEvents.getTop ());
            AreaGenerator.saveTiles (this.currentGenerators.get (i).getArea (), 
                    area, this.currentEvents.getWorldName ());
            tempEntities = this.currentGenerators.get (i).getEntities ();
            this.entities.addEntity (tempEntities);
            this.entities.saveEntities (this.currentEvents.getWorldName (), 
                    area.getX (), area.getY ());
            this.entities.removeEntitiesAt (area.getX (), area.getY ());
          }
          catch (IOException e)
          {
            LOGGER.log (Level.WARNING, "Could not save events", e);
          }
          this.currentGenerators.remove (i);
          i--;
        }
      }
      
      // look for areas that need to be loaded and load them, causing us to
      // generate if need be
      loadLocations = new HashSet<Area> ();
      for (Entry<String, Entity> player : this.players.entrySet ())
      {
        area = new Area (player.getValue ().getLocation ().getX () - 1, 
                player.getValue ().getLocation ().getY () - 1);
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX () - 1, 
                player.getValue ().getLocation ().getY ());
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX () - 1, 
                player.getValue ().getLocation ().getY () + 1);
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX (), 
                player.getValue ().getLocation ().getY () - 1);
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX (), 
                player.getValue ().getLocation ().getY ());
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX (), 
                player.getValue ().getLocation ().getY () + 1);
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX () + 1, 
                player.getValue ().getLocation ().getY () - 1);
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX () + 1, 
                player.getValue ().getLocation ().getY ());
        loadLocations.add (area);
        area = new Area (player.getValue ().getLocation ().getX () + 1, 
                player.getValue ().getLocation ().getY () + 1);
        loadLocations.add (area);
      }
      
      // load those in set if not already loaded
      for (Area load : loadLocations)
      {
        if (!isLoaded (load))
        {
          try
          {
            loadLocation (load);
          }
          catch (IOException e)
          {
            LOGGER.log (Level.WARNING, "Could not load events", e);
          }
          catch (InstantiationException e)
          {
            LOGGER.log (Level.WARNING, "Could not load events", e);
          }
          catch (IllegalAccessException e)
          {
            LOGGER.log (Level.WARNING, "Could not load events", e);
          }
          
          boolean alreadyGenerating = false;
          
          for (GenerationSequence seq : this.currentGenerators)
          {
            if (seq.getHistory ().getLocation ().equals (load))
            {
              alreadyGenerating = true;
            }
          }
          
          if (!isGenerated(load) && !alreadyGenerating)
          {
            try
            {
              EventIO.getEvents (this.currentEvents.getWorldName (), 
                  this.currentEvents, load.getX (), load.getX (), 
                  load.getY (), load.getY ());
              newEvents = new HistoryGenerator ();
              newEvents.setBasis (this.currentEvents);
              newEvents.setTypes (this.possibleEvents);
              newEvents.setIncrement (TIME_INCREMENT);
              newEvents.setGrouping (MAX_EVENTS);
              newEvents.setUntil (Integer.valueOf (Config.getValue 
                      (this.currentEvents.getWorldName (), 
                      HistoryGenerator.TIME_KEY)));
              newEvents.setTop (load.getY ());
              newEvents.setBottom (load.getY ());
              newEvents.setLeft (load.getX ());
              newEvents.setRight (load.getX ());
              newEvents.setLocation (load);
              long seed = Long.valueOf (Config.getValue 
                      (this.currentEvents.getWorldName (), 
                      MacroTerrainGenerator.SEED_KEY)) + 
                      load.getX () + load.getY () *
                      this.currentEvents.getWorldSize ();
              newEvents.setRandom (new Random (seed));
              for (EventTypeHolder ref : this.currentEvents.getTypeHolders ())
              {
                newEvents.addReference (ref);
              }
              closestPlayer = null;
              
              for (Entity player : this.players.values ())
              {
                if (null == closestPlayer)
                {
                  closestPlayer = player;
                }
                else if (player.getLocation ().distance (newEvents
                        .getLocation ()) < closestPlayer.getLocation ()
                        .distance (newEvents.getLocation ()))
                {
                  closestPlayer = player;
                }
              }
              
              GenerationSequence sequence = new GenerationSequence (newEvents,
                      closestPlayer);
              this.currentGenerators.add (sequence);
              this.runners.execute (sequence);
            }
            catch (IOException e)
            {
              LOGGER.log (Level.WARNING, "Could not generate area", e);
            }
            catch (InstantiationException e)
            {
              LOGGER.log (Level.WARNING, "Could not generate area", e);
            }
            catch (IllegalAccessException e)
            {
              LOGGER.log (Level.WARNING, "Could not generate area", e);
            }
          }
        }
      }
      
      // look for areas that need to be unloaded and unload them
      for (Area unload : getLoaded ())
      {
        if (!loadLocations.contains (unload))
        {
          unloadLocation (unload);
        }
      }
      
      try
      {
        Thread.sleep (SLEEP_TIME);
      }
      catch (InterruptedException e)
      {
        LOGGER.log (Level.INFO, "Thread was interrupted", e);
      }
    }
    
    this.runners.shutdown ();
    try
    {
      this.runners.awaitTermination (10, TimeUnit.MINUTES);
    }
    catch (InterruptedException e)
    {
      LOGGER.log (Level.WARNING, "Runner was interrupted", e);
    }
    
    // save off remaining locations that needed generation
    for (int i = 0; i < this.currentGenerators.size (); i++)
    {
      if (this.currentGenerators.get (i).isDone ())
      {
        createdEvents = this.currentGenerators.get (i).getHistory ();
        try
        {
          EventIO.saveEvents (createdEvents.getCreated (), 
                  this.currentEvents.getWorldName (),
                createdEvents.getTop (), 
                createdEvents.getBottom (), 
                createdEvents.getLeft (), 
                createdEvents.getRight ());
          area = new Area (createdEvents.getTop (), 
                  createdEvents.getLeft ());
          AreaGenerator.saveTiles (this.currentGenerators.get (i).getArea (), 
                    area, this.currentEvents.getWorldName ());
        }
        catch (IOException e)
        {
          LOGGER.log (Level.WARNING, "Could not save events", e);
        }
        this.currentGenerators.remove (i);
        i--;
      }
    }
  }
}
