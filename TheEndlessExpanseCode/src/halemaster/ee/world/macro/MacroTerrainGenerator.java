package halemaster.ee.world.macro;

import halemaster.ee.Game;
import halemaster.ee.localization.Localizer;
import halemaster.ee.state.Loader;
import halemaster.ee.state.Menu;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.Config;
import halemaster.ee.world.NoiseCannon;
import halemaster.ee.world.faction.Faction;
import halemaster.ee.world.history.HistoryGenerator;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.history.event.EventIO;
import halemaster.ee.world.history.event.type.EventType;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @name MacroTerrainGenerator
 * 
 * @version 0.0.0
 * 
 * @date Oct 30, 2013
 */
public class MacroTerrainGenerator implements Loader
{
  public static final String TERRAIN_EXT = "wld";
  public static final String TERRAIN_FILE = "terrain." + TERRAIN_EXT;
  public static final String SPAWN_POINT = "spawn";
  public static final byte MINIMUM_HABITUALITY = 9;
  public static final int LOWER_SIZE = 300;
  public static final int UPPER_SIZE = 500;
  public static final int SLOPE = 10;
  public static final int DIVISOR = 20;
  public static final int ELEVATION = 0;
  public static final int TEMPERATURE = 1;
  public static final int PRECIPITATION = 2;
  public static final int COUNT = 0;
  public static final int RADIUS = 1;
  public static final int AMOUNT = 2;
  public static final int STRENGTH = 3;
  public static final int LOWER = 0;
  public static final int UPPER = 1;
  public static final int[][][] CANNON_PARAMETERS = {
    {{2,5},{25,40},{25,50},{60,80}},
    {{25,50},{25,50},{1,10},{-80,80}}, // TEMPERATURE
    {{25,50},{25,50},{25,50},{25,70}}  // PRECIPITATION
  };
  public static final int TIME_INCREMENT = 4;
  public static final int TIME_MAX = 4000;
  public static final int TIME_MIN = 2000;
  public static final int[] MAX_EVENTS = {0,0,0,0,0,1,1,2,3};
  public static final String SEED_KEY = "seed";
  public static final String SIZE_KEY = "size";
  private static final Logger LOGGER = Logger.getLogger 
          (MacroTerrainGenerator.class.getName ());
  private static final double PRO_LANDMASS = .25;
  private static final double PRO_TEMP = .45;
  private static final double PRO_RAIN = .7;
  private static final double PRO_IND_BIOME = .25;
  private static final double PRO_WRITE = .95;
  private static final double PRO_WRITE_AFTER = .95 * 2;
  private static final double PROGRESS_PERCENT = .5;
  private Random generator;
  private long seed;
  private String name;
  private NoiseCannon cannon;
  private double percent = 0;
  private Double upperPercent = null;
  private String message = "Starting world generation...";
  HistoryGenerator history = null;
  
  public MacroTerrainGenerator (String seed, String name)
  {
    long seedValue = 0;
    
    for (int i = 0; i < seed.length (); i++)
    {
      // ensure seed values are different based on order of chars in String.
      seedValue += seed.charAt (i) * (i + 1);
    }
    
    this.name = name;
    setupSeed (seedValue);
  }
  
  public MacroTerrainGenerator (long seed, String name)
  {
    this.name = name;
    setupSeed (seed);
  }
  
  private void setupSeed (long seed)
  {    
    this.seed = seed;
  }
  
  /**
   * Generate a world with the given name and save it to disk in the worlds
   * folder.
   * 
   * @param name name of the world to generate.
   */
  public void generateWorld ()
  {
    byte[][] elevation;
    byte[][] precipitation;
    byte[][] temperature;
    Biome[][] biomes;
    String biomeId;
    List<Biome> possibleBiomes;
    ArrayList<String> ids;
    Biome tempBiome;
    int size;
    EventHolder historyHolder;
    EventTypeHolder typeHolder = new EventTypeHolder ();
    EventType[] types;
    EventHolder basicEvents;
    BufferedImage map;
    File saveFile = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER +
            "/" + this.name + "/" + TERRAIN_FILE);
    File imageFile = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER 
            + "/" + this.name + "/map.png");
    File historyBook = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER + "/" + this.name + "/history.txt");
    File typeFolder = new File (EventIO.EVENT_FOLDER + EventIO.GLOBAL_FOLDER);
    File worldDirectory =
            new File (Game.HOME + 
            Game.GAME_FOLDER + Game.WORLD_FOLDER + "/" + this.name);
    
    try
    {
      BufferedWriter writer = new BufferedWriter (new FileWriter (saveFile));

      this.generator = new Random (this.seed);

      size = this.generator.nextInt (UPPER_SIZE - LOWER_SIZE + 1) + LOWER_SIZE;
    
      try
      {
        Config.pushValue (this.name, SEED_KEY, String.valueOf(this.seed));
        Config.pushValue (this.name, SIZE_KEY, String.valueOf(size));
      }
      catch (IOException e)
      {
        LOGGER.log (Level.SEVERE, "Could not save config", e);
      }

      elevation = new byte[size][size];
      precipitation = new byte[size][size];
      temperature = new byte[size][size];
      biomes = new Biome[size][size];
      map = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR); 

      for (int i = 0; i < size; i++)
      {
        Arrays.fill (elevation[i], (byte) -100);
        Arrays.fill (precipitation[i], Byte.MIN_VALUE);
      }
      
      this.upperPercent = PRO_LANDMASS;
      this.message = Localizer.getString ("game.world.create.land");

      this.cannon = new NoiseCannon (this.generator);
      this.cannon.setSlope (1, SLOPE);

      // Generate Height
      this.cannon.setxBorder (LOWER_SIZE / 8); // no land near edges
      this.cannon.setyBorder (LOWER_SIZE / 8); // no land near edges
      this.cannon.setBlastCount (CANNON_PARAMETERS[ELEVATION][COUNT][LOWER], 
              CANNON_PARAMETERS[ELEVATION][COUNT][UPPER]);
      this.cannon.setBlastRadius (CANNON_PARAMETERS[ELEVATION][RADIUS][LOWER], 
              CANNON_PARAMETERS[ELEVATION][RADIUS][UPPER]);
      this.cannon.setParticleCount (CANNON_PARAMETERS[ELEVATION][AMOUNT][LOWER], 
              CANNON_PARAMETERS[ELEVATION][AMOUNT][UPPER]);
      this.cannon.setParticleStrength (CANNON_PARAMETERS[ELEVATION][STRENGTH][LOWER], 
              CANNON_PARAMETERS[ELEVATION][STRENGTH][UPPER]);

      elevation = this.cannon.generateNoise (elevation);
      this.message = Localizer.getString ("game.world.create.temp");
      
      // initialize temperature map based on height map.
      for (int x = 0; x < elevation.length; x++)
      {
        for (int y = 0; y < elevation[x].length; y++)
        {
          if ((isArctic (elevation.length, x, y)))
          {
            temperature[x][y] = (byte) -100; // Arctic temperatures
          }
          else if (elevation[x][y] > 0)
          {
            temperature[x][y] = (byte) (90 - elevation[x][y]);
          }
          else
          {
            temperature[x][y] = (byte) (elevation[x][y] + 20);
          }
        }
      }

      // Generate Temp
      this.cannon.setSlope (1, SLOPE);
      this.cannon.setxBorder (0); // can be cold anywhere
      this.cannon.setyBorder (0); // can be cold anywhere
      this.cannon.setBlastCount (CANNON_PARAMETERS[TEMPERATURE][COUNT][LOWER], 
              CANNON_PARAMETERS[TEMPERATURE][COUNT][UPPER]);
      this.cannon.setBlastRadius (CANNON_PARAMETERS[TEMPERATURE][RADIUS][LOWER], 
              CANNON_PARAMETERS[TEMPERATURE][RADIUS][UPPER]);
      this.cannon.setParticleCount (CANNON_PARAMETERS[TEMPERATURE][AMOUNT][LOWER], 
              CANNON_PARAMETERS[TEMPERATURE][AMOUNT][UPPER]);
      this.cannon.setParticleStrength (CANNON_PARAMETERS[TEMPERATURE][STRENGTH][LOWER], 
              CANNON_PARAMETERS[TEMPERATURE][STRENGTH][UPPER]);
      
      this.percent = PRO_LANDMASS;
      this.upperPercent = PRO_TEMP;

      temperature = this.cannon.generateNoise (temperature);
      this.message = Localizer.getString ("game.world.create.rain");

      // Generate Precip
      this.cannon.setSlope (1, SLOPE);
      this.cannon.setxBorder (0); // can rain anywhere
      this.cannon.setyBorder (0); // can rain anywhere
      this.cannon.setBlastCount (CANNON_PARAMETERS[PRECIPITATION][COUNT][LOWER], 
              CANNON_PARAMETERS[PRECIPITATION][COUNT][UPPER]);
      this.cannon.setBlastRadius (CANNON_PARAMETERS[PRECIPITATION][RADIUS][LOWER], 
              CANNON_PARAMETERS[PRECIPITATION][RADIUS][UPPER]);
      this.cannon.setParticleCount (CANNON_PARAMETERS[PRECIPITATION][AMOUNT][LOWER], 
              CANNON_PARAMETERS[PRECIPITATION][AMOUNT][UPPER]);
      this.cannon.setParticleStrength (CANNON_PARAMETERS[PRECIPITATION]
              [STRENGTH][LOWER], CANNON_PARAMETERS[PRECIPITATION][STRENGTH][UPPER]);
      
      this.percent = PRO_TEMP;
      this.upperPercent = PRO_RAIN;

      precipitation = this.cannon.generateNoise (precipitation);
      
      this.cannon = null;
      this.percent = PRO_RAIN;
      this.message = Localizer.getString ("game.world.create.biome");

      possibleBiomes = BiomeClassifier.getBiomes ();
      possibleBiomes.remove (BiomeClassifier.DEFAULT);

      // assign biomes to squares
      for (int x = 0; x < size; x++)
      {
        for (int y = 0; y < size; y++)
        {          
          // create list of biomes possible
          ids = new ArrayList<String> ();
          for (Biome possibleBiome : possibleBiomes)
          {
            if (elevation[x][y] >= possibleBiome.getLowerElevation () &&
                    elevation[x][y] <= possibleBiome.getUpperElevation () &&
                    precipitation[x][y] >= possibleBiome.getLowerPrecipitation ()
                    && precipitation[x][y] <= 
                    possibleBiome.getUpperPrecipitation () && temperature[x][y] >=
                    possibleBiome.getLowerTemperature () && temperature[x][y] <=
                    possibleBiome.getUpperTemperature ())
            {
              ids.add (possibleBiome.getName ());
            }
          }

          // choose a biome from possible list
          if (0 == ids.size ())
          {
            biomeId = BiomeClassifier.DEFAULT.getName ();
          }
          else
          {
            biomeId = ids.get (this.generator.nextInt (ids.size ()));
          }
          tempBiome = BiomeClassifier.getBiome (biomeId);
          if (null != tempBiome.getColorString ())
          {
            map.setRGB (x, y, (tempBiome.getRed () << 16) + 
                  (tempBiome.getGreen () << 8) +
                  (tempBiome.getBlue ()));
          }
          else
          {
            map.setRGB (x, y, 0);
          }
          biomes[x][y] = tempBiome;
          writer.write (biomeId);
          writer.newLine ();
          
          this.percent += PRO_IND_BIOME / (size * size);
        }
      }
      
      this.percent = PRO_WRITE;
      this.message = Localizer.getString ("game.world.create.file");
      
      writer.close ();
      
      ImageIO.write(map, "bmp", imageFile);
      
      for (File typeFile : typeFolder.listFiles ())
      {
        types = EventIO.getTypes (typeFile);
        for (EventType type : types)
        {
          typeHolder.addType (type);
        }
      }
      
      basicEvents = new EventHolder (this.name, biomes);
      
      this.history = new HistoryGenerator ();
      
      this.history.setTypes (typeHolder);
      this.history.setIncrement (TIME_INCREMENT);
      this.history.setUntil (this.generator.nextInt 
              (TIME_MAX - TIME_MIN + 1) + TIME_MIN);
      this.history.setGrouping (MAX_EVENTS);
      this.history.setTop (0);
      this.history.setBottom (size);
      this.history.setLeft (0);
      this.history.setRight (size);
      this.history.setRandom (this.generator);
      this.history.setLocation (Area.ANYWHERE);
      
      historyHolder = this.history.generate (basicEvents);
      
      this.history = null;
      
      this.percent = PRO_WRITE_AFTER;
      this.message = Localizer.getString ("game.world.create.file");
      
      EventIO.saveEvents (historyHolder, this.name);
      
      writer = new BufferedWriter (new FileWriter (historyBook));
      
      Event[] events = historyHolder.getEvents (0, Integer.MAX_VALUE, 0, size, 
              0, size);
      for (Event event : events)
      {
        writer.write (event.getDescription ());
        writer.newLine ();
      }
      
      writer.close ();
      
      if (setSpawnPoint (biomes))
      {
        Faction.save (this.name);
      }
      else
      {
        Menu.emptyDirectory (worldDirectory);
      }
      
      this.percent = Loader.DONE;
      this.message = Localizer.getString ("game.world.create.done");
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Failed to make world!", e);
    }
  }
  
  /**
   * Determine if the given spot is able to spawn as arctic.
   * This must be on a square matrix
   * 
   * @param canvas canvas it comes from
   * @param x x location
   * @param y y location
   * @return whether the location can be in the arctic.
   */
  public boolean isArctic (int width, int x, int y)
  {
    int distance  = (int) Math.sqrt ((double) ((x - width / 2) * (x - width / 2)
            + (y - width / 2) * (y - width / 2)));
    int gaussDis = (int) Math.sqrt ((double) ((0 - width / 2) * (0 - width / 2)
            + (0 - width / 2) * (0 - width / 2)));
    boolean isFarEnough = (distance > (width / DIVISOR * (DIVISOR - 1)) / 2);
    boolean gaussed = Math.abs (this.generator.nextGaussian () * (gaussDis - 
            distance)) < (width / DIVISOR);
    
    return  isFarEnough && gaussed;
  } 
  
  /**
   * Based upon the biome array, set an initial spawnpoint for players for the
   * world
   * 
   * @param biomes biomes of the world
   */
  public boolean setSpawnPoint (Biome[][] biomes)
  {
    Area area;
    List<Area> cities;
    
    cities = Faction.settlementLocations (this.name);
    
    try
    {
      area = cities.get (this.generator.nextInt (cities.size ()));
      Config.pushValue (this.name, SPAWN_POINT, area.toString ());
    }
    catch (IOException e)
    {
      LOGGER.log (Level.WARNING, "Unable to set spawn point", e);
      return false;
    }
    catch (IllegalArgumentException e)
    {
      this.message = "ERROR NO CITIES";
      LOGGER.log (Level.SEVERE, "No Cities generated, cannot generate world", e);
      return false;
    }
    
    return true;
  }
  
  /**
   * Create a new Generator to generate a world with the given name and seed.
   * 
   * @param name name of the world to generate.
   * @param seed seed of the world to generate.
   */
  public static MacroTerrainGenerator worldGenerator (String name, String seed)
  {
    return new MacroTerrainGenerator (seed, name);
  }

  /**
   * Get the percent done of this generator. If the noise cannon exists, we 
   * multiply our current percent by it to get the percent, otherwise, just
   * return current percent.
   * 
   * @return 
   */
  public synchronized double getPercentage ()
  {
    double progress = this.percent;
    
    if (null != this.cannon)
    {
      progress = this.percent + (this.upperPercent - this.percent) 
              * this.cannon.getProgress ();
    }
    progress *= PROGRESS_PERCENT;
    
    if (null != this.history)
    {
      progress += this.history.getPercentage () * PROGRESS_PERCENT;
    }
    
    return progress;
  }

  public synchronized String getMessage ()
  {
    if (null == this.history)
    {
      return this.message;
    }
    else
    {
      return this.history.getMessage ();
    }
  }
  
  public String getName ()
  {
    return "TEE-MacroTerrain-" + this.name;
  }

  public void run ()
  {
    this.percent = Loader.START;
    generateWorld ();
    this.percent = Loader.DONE / PROGRESS_PERCENT;
  }
}
