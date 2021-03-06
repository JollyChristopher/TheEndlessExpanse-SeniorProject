package halemaster.ee.world.micro;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.Sprite;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import halemaster.ee.world.terrain.structure.Architecture;
import halemaster.ee.world.terrain.structure.ChanceTile;
import halemaster.ee.world.terrain.structure.Room;
import halemaster.ee.world.terrain.structure.Structure;
import halemaster.ee.world.terrain.tile.BiomeGeneration;
import halemaster.ee.world.terrain.tile.BiomeTile;
import halemaster.ee.world.terrain.tile.TileStyle.Direction;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * @name AreaGenerator
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public class AreaGenerator 
{
  public static final String AREA_FOLDER = "/tiles";
  public static final int AREA_SIZE = 64;
  public static final float TILE_WIDTH = Sprite.DEFAULT_SIZE * 0.99f;
  public static final float TILE_HEIGHT = TILE_WIDTH;
  
  /**
   * Generate an area based upon the given random, biome, and whether there is a
   * city
   * 
   * @param biome biome to generate
   * @param random random to use
   * @param hasCity whether there is a city
   * @return the biome tiles that generate
   */
  public static BiomeTile[][] generate (Biome biome, Random random,
          boolean hasCity)
  {
    BiomeTile[][] tiles = new BiomeTile[AREA_SIZE][AREA_SIZE];
    BiomeGeneration generation = biome.getGeneration ();
    Set<Area> taken = new HashSet<Area>();
    int index;
    
    for (int x = 0; x < tiles.length; x++)
    {
      for (int y = 0; y < tiles[x].length; y++)
      {
        tiles[x][y] = new BiomeTile();
        tiles[x][y].setBiomeId (biome.getName ());
        tiles[x][y].setStructureId (BiomeTile.NONE);
      }
    }
    
    for (int x = 0; x < AREA_SIZE; x++)
    {
      for (int y = 0; y < AREA_SIZE; y++)
      {
        for (int id : generation.getBase ())
        {
          tiles[x][y].addTileId (id, false);
        }
        
        if (hasCity)
        {
          index = 0;
          for (Structure struct : generation.getCity ())
          {
            if (random.nextInt ((int) 10000) < struct.getChance () * 100)
            {
              spawnStructure (tiles, struct.getArch (), random, new Area (x, y),
                      taken, BiomeTile.getStructId (index, true));
            }
            index++;
          }
        }
        
        if (null != generation.getStructures ())
        {
          index = 0;
          for (Structure struct : generation.getStructures ())
          {
            if (random.nextInt ((int) 10000) < struct.getChance () * 100)
            {
              spawnStructure (tiles, struct.getArch (), random, new Area (x, y), 
                      taken, BiomeTile.getStructId (index, false));
            }
            index++;
          }
        }
      }
    }
    
    return tiles;
  }
  
  /**
   * Attempt to spawn the givens structure at the given location.
   * 
   * @param tiles tiles to spawn onto
   * @param struct structure to spawn
   * @param random random to use
   * @param loc location to start at (upper left)
   * @param taken which areas have already had structures spawned on them
   */
  private static void spawnStructure (BiomeTile[][] tiles, Architecture struct,
          Random random, Area loc,  Set<Area> taken, byte structId)
  {
    boolean canPlace = true;
    boolean placed;
    float chanceWeight;
    Map<Room, Map<Direction, Set<Room>>> doorMatrix = new 
            HashMap<Room, Map<Direction, Set<Room>>>();
    
    if (loc.getX () + struct.getSize ().getWidth () > tiles.length ||
            loc.getY () + struct.getSize ().getHeight () > tiles[0].length)
    {
      canPlace = false;
    }
    
    for (int y = 0; canPlace && y < struct.getSize ().getHeight (); y++)
    {
      if (taken.contains (new Area (loc.getX (), y + loc.getY ())))
      {
        canPlace = false;
      }
    }
    
    if (canPlace)
    {
      if (null == struct.getFloorPlan ())
      {
        for (int x = 0; x < struct.getSize ().getWidth (); x++)
        {
          for (int y = 0; y < struct.getSize ().getHeight (); y++)
          {
            taken.add (new Area (x + loc.getX (), y + loc.getY ()));
            chanceWeight = 100;
            placed = false;
            
            for (int i = 0; !placed && i < struct.getLayout ()[x * 
                    struct.getSize ().getHeight () + y].size (); i++)
            {
              ChanceTile chanceTile = struct.getLayout ()[x * 
                    struct.getSize ().getWidth () + y].get (i);
              if (random.nextInt ((int) chanceWeight) < chanceTile.getChance ())
              {
                tiles[x + loc.getX ()][y + loc.getY ()]
                        .addTileId (chanceTile.getTile (), true);
                placed = true;
              }
              chanceWeight -= chanceTile.getChance ();
            }
            tiles[x + loc.getX ()][y + loc.getY ()].setStructureId 
                        (structId);
          }
        }
      }
      else
      {
        // precompute door matrix
        Room[] rooms = struct.getFloorPlan ().getRooms ();
        for (int mainRoom = 0; mainRoom < rooms.length; mainRoom++)
        {
          for (int otherRoom = 0; otherRoom < rooms.length; otherRoom++)
          {
            for (int mainDoor = 0; mainDoor < rooms[mainRoom].getDoors ()
                    .length; mainDoor++)
            {
              for (int otherDoor = 0; otherDoor < rooms[otherRoom].getDoors ()
                      .length; otherDoor++)
              {
                if (0 == rooms[mainRoom].getDoors ()[mainDoor].getY ())
                {
                  if ((struct.getSize ().getHeight () / 
                          struct.getFloorPlan ().getSize ().getHeight ()) - 1 == 
                          rooms[otherRoom].getDoors ()[otherDoor].getY ())
                  {
                    if (rooms[mainRoom].getDoors ()[mainDoor].getX () == 
                            rooms[otherRoom].getDoors ()[otherDoor].getX ())
                    {
                      Map<Direction, Set<Room>> directional = 
                              doorMatrix.get (rooms[mainRoom]);
                      if (null == directional)
                      {
                        directional = new EnumMap<Direction, Set<Room>>
                                (Direction.class);
                        doorMatrix.put (rooms[mainRoom], directional);
                      }
                      
                      Set<Room> roomSet = directional.get (Direction.NORTH);
                      if (null == roomSet)
                      {
                        roomSet = new HashSet<Room> ();
                        directional.put (Direction.NORTH, roomSet);
                      }
                      
                      roomSet.add (rooms[otherRoom]);
                    }
                  }
                }
                else if (0 == rooms[mainRoom].getDoors ()[mainDoor].getX ())
                {
                  if ((struct.getSize ().getWidth () / 
                          struct.getFloorPlan ().getSize ().getWidth ()) - 1 == 
                          rooms[otherRoom].getDoors ()[otherDoor].getX ())
                  {
                    if (rooms[mainRoom].getDoors ()[mainDoor].getY () == 
                            rooms[otherRoom].getDoors ()[otherDoor].getY ())
                    {
                      Map<Direction, Set<Room>> directional = 
                              doorMatrix.get (rooms[mainRoom]);
                      if (null == directional)
                      {
                        directional = new EnumMap<Direction, Set<Room>>
                                (Direction.class);
                        doorMatrix.put (rooms[mainRoom], directional);
                      }
                      
                      Set<Room> roomSet = directional.get (Direction.WEST);
                      if (null == roomSet)
                      {
                        roomSet = new HashSet<Room> ();
                        directional.put (Direction.WEST, roomSet);
                      }
                      
                      roomSet.add (rooms[otherRoom]);
                    }
                  }
                }
                else if ((struct.getSize ().getHeight () / 
                        struct.getFloorPlan ().getSize ().getHeight ()) - 1 == 
                        rooms[mainRoom].getDoors ()[mainDoor].getY ())
                {
                  if (0 == rooms[otherRoom].getDoors ()[otherDoor].getY ())
                  {
                    if (rooms[mainRoom].getDoors ()[mainDoor].getX () == 
                            rooms[otherRoom].getDoors ()[otherDoor].getX ())
                    {
                      Map<Direction, Set<Room>> directional = 
                              doorMatrix.get (rooms[mainRoom]);
                      if (null == directional)
                      {
                        directional = new EnumMap<Direction, Set<Room>>
                                (Direction.class);
                        doorMatrix.put (rooms[mainRoom], directional);
                      }
                      
                      Set<Room> roomSet = directional.get (Direction.SOUTH);
                      if (null == roomSet)
                      {
                        roomSet = new HashSet<Room> ();
                        directional.put (Direction.SOUTH, roomSet);
                      }
                      
                      roomSet.add (rooms[otherRoom]);
                    }
                  }
                }
                else if ((struct.getSize ().getWidth () / 
                        struct.getFloorPlan ().getSize ().getWidth ()) - 1 == 
                        rooms[mainRoom].getDoors ()[mainDoor].getX ())
                {
                  if (0 == rooms[otherRoom].getDoors ()[otherDoor].getX ())
                  {
                    if (rooms[mainRoom].getDoors ()[mainDoor].getY () == 
                            rooms[otherRoom].getDoors ()[otherDoor].getY ())
                    {
                      Map<Direction, Set<Room>> directional = 
                              doorMatrix.get (rooms[mainRoom]);
                      if (null == directional)
                      {
                        directional = new EnumMap<Direction, Set<Room>>
                                (Direction.class);
                        doorMatrix.put (rooms[mainRoom], directional);
                      }
                      
                      Set<Room> roomSet = directional.get (Direction.EAST);
                      if (null == roomSet)
                      {
                        roomSet = new HashSet<Room> ();
                        directional.put (Direction.EAST, roomSet);
                      }
                      
                      roomSet.add (rooms[otherRoom]);
                    }
                  }
                }
              }
            }
          }
        }
        
        // use precomputed map to spawn rooms
        Set<Area> takenRooms = new HashSet<Area>();
        Queue<Entry<Area, Area>> roomQueue =
                new ArrayDeque<Entry<Area, Area>>();
        List<Room> possibleRooms = new ArrayList<Room>();
        
        for (Room room : rooms)
        {
          if (null != doorMatrix.get (room))
          {
            if (null != doorMatrix.get (room).get (Direction.EAST))
            {
              possibleRooms.add (room);
            }
            else if (null != doorMatrix.get (room).get (Direction.SOUTH))
            {
              possibleRooms.add (room);
            }
          }
        }
        
        Room spawn = possibleRooms.get (random.nextInt (possibleRooms.size ()));
        spawnStructure (tiles, spawn.getLayout (), random, loc, taken, structId);
        takenRooms.add (new Area (0, 0));
        if (null != doorMatrix.get (spawn).get (Direction.EAST))
        {
          if (struct.getFloorPlan ().getSize ().getWidth () > 1)
          {
            roomQueue.add (new AbstractMap.SimpleEntry<Area, Area>
                    (new Area(0,0), new Area(1, 0)));
            takenRooms.add (new Area (1, 0));
          }
        }
        if (null != doorMatrix.get (spawn).get (Direction.SOUTH))
        {
          if (struct.getFloorPlan ().getSize ().getHeight () > 1)
          {
            roomQueue.add (new AbstractMap.SimpleEntry<Area, Area>
                    (new Area(0,0), new Area(0, 1)));
            takenRooms.add (new Area (1, 0));
          }
        }
        
        while (!roomQueue.isEmpty ())
        {
          Entry<Area, Area> next = roomQueue.poll ();
          Direction dir = Direction.EAST;
          Set<Direction> possibleNext = new HashSet<Direction>();
          
          if (next.getKey ().getX () > next.getValue ().getX ())
          {
            dir = Direction.EAST;
          }
          else if (next.getKey ().getX () < next.getValue ().getX ())
          {
            dir = Direction.WEST;
          }
          else if (next.getKey ().getY () > next.getValue ().getY ())
          {
            dir = Direction.SOUTH;
          }
          else if (next.getKey ().getY () < next.getValue ().getY ())
          {
            dir = Direction.NORTH;
          }
          
          if (!takenRooms.contains (new Area (next.getValue ().getX () + 1, 
                  next.getValue ().getY ())))
          {
            if (struct.getFloorPlan ().getSize ().getWidth () > 
                    next.getValue ().getX () + 1)
            {
              possibleNext.add (Direction.EAST);
            }
          }
          if (!takenRooms.contains (new Area (next.getValue ().getX () - 1, 
                  next.getValue ().getY ())))
          {
            if (0 < next.getValue ().getX ())
            {
              possibleNext.add (Direction.WEST);
            }
          }
          if (!takenRooms.contains (new Area (next.getValue ().getX (), 
                  next.getValue ().getY () + 1)))
          {
            if (struct.getFloorPlan ().getSize ().getHeight () > 
                    next.getValue ().getY () + 1)
            {
              possibleNext.add (Direction.SOUTH);
            }
          }
          if (!takenRooms.contains (new Area (next.getValue ().getX (), 
                  next.getValue ().getY () - 1)))
          {
            if (0 < next.getValue ().getY ())
            {
              possibleNext.add (Direction.NORTH);
            }
          }
          
          possibleRooms = new ArrayList<Room>();
          if (possibleNext.isEmpty ())
          {
            for (Room room : rooms)
            {
              if (null != doorMatrix.get (room))
              {
                if (null != doorMatrix.get (room).get (dir))
                {
                  possibleRooms.add (room);
                }
              }
            }
          }
          else
          {
            for (Room room : rooms)
            {
              if (null != doorMatrix.get (room))
              {
                if (null != doorMatrix.get (room).get (dir))
                {
                  boolean hasDoorway = false;
                  
                  for (Direction possibleDir : possibleNext)
                  {
                    if (null != doorMatrix.get (room).get (possibleDir))
                    {
                      hasDoorway = true;
                    }
                  }
                  
                  if (hasDoorway)
                  {
                    possibleRooms.add (room);
                  }
                }
              }
            }
          }
          
          spawn = possibleRooms.get (random.nextInt (possibleRooms.size ()));
          spawnStructure (tiles, spawn.getLayout (), random, 
                  new Area (loc.getX () + next.getValue ().getX () * 
                  (struct.getSize ().getWidth () / 
                  struct.getFloorPlan ().getSize ().getWidth ()), loc.getY () +
                  next.getValue ().getY () * (struct.getSize ().getHeight () / 
                  struct.getFloorPlan ().getSize ().getHeight ())), taken,
                  structId);
          for (Direction possibleDir : possibleNext)
          {
            if (null != doorMatrix.get (spawn).get (possibleDir))
            {
              Area location = new Area (next.getValue ().getX () + possibleDir
                      .getX () - 1, next.getValue ().getY () + possibleDir
                      .getY () - 1);
              roomQueue.add (new AbstractMap.SimpleEntry<Area, Area>
                      (new Area(0,0), location));
              takenRooms.add (location);
            }
          }
        }
      }
    }
  }
  
  /**
   * Save the tiles at the given area for the given world
   * 
   * @param tiles tiles to save
   * @param location location to save
   * @param world world to save at
   */
  public static void saveTiles (BiomeTile[][] tiles, Area location, 
          String world) throws IOException
  {
    File terrainFolder = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER + "/" + world + PlayerState.LOCATION_FOLDER + 
            AREA_FOLDER);
    File areaFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            AREA_FOLDER + "/" + location.getX () + "," + location.getY () +
            "." + MacroTerrainGenerator.TERRAIN_EXT);
    BiomeHolder holder = new BiomeHolder();
    
    holder.setTiles (tiles);
    
    if (!terrainFolder.exists ())
    {
      terrainFolder.mkdirs ();
    }
    
    Json.saveJson (areaFile, holder);
  }
  
  /**
   * load the tiles for the location of the world.
   * 
   * @param location location to load
   * @param world world to load
   * @return tiles for the location in the world
   */
  public static BiomeTile[][] loadTiles (Area location, String world) throws
          IOException
  {
    File areaFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            AREA_FOLDER + "/" + location.getX () + "," + location.getY () +
            "." + MacroTerrainGenerator.TERRAIN_EXT);
    BiomeHolder[] tiles;
    
    tiles = Json.getFromFile (areaFile, BiomeHolder.class);
    
    return tiles[0].getTiles ();
  }
}