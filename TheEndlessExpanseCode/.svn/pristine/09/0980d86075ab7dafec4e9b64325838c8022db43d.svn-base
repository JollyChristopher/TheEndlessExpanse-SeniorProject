package halemaster.ee.world;

import halemaster.ee.Game;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @name NoiseCannon
 * 
 * @version 0.0.0
 * 
 * @date Jan 24, 2014
 */
public class Config
{
  private static Map<String, Config> configs = new HashMap<String, Config>();
  
  /**
   * Get a value from a world config file
   * 
   * @param world world to get value from
   * @param key key of the value to get
   * @return the value from the config
   * @throws IOException 
   */
  public static String getValue (String world, String key) throws IOException
  {
    Config config = configs.get (world);
    if (null == config)
    {
      config = new Config (world);
      configs.put (world, config);
    }
    
    return config.getValue (key);
  }
  
  /**
   * Push a value into a world config file.
   * 
   * @param world world to push value into
   * @param key key of value to push
   * @param value value to push
   * @throws IOException 
   */
  public static void pushValue (String world, String key, String value)
          throws IOException
  {    
    Config config = configs.get (world);
    if (null == config)
    {
      config = new Config (world);
      configs.put (world, config);
    }
    
    config.pushValue (key, value);
  }
  
  public static final String SEPARATOR = "=";
  private Map <String, String> valueMap = new HashMap<String, String> ();
  private String world;
  
  public Config (String world) throws IOException
  {
    File config = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER 
            + "/" + world + "/config");
    String next;
    this.world = world;
    
    try
    {
      BufferedReader configReader = new BufferedReader (new FileReader (config));

      while (configReader.ready ())
      {
        next = configReader.readLine ();
        this.valueMap.put (next.split (SEPARATOR)[0], next.split (SEPARATOR)[1]);
      }

      configReader.close ();
    }
    catch (FileNotFoundException e)
    {
      // no problem!
    }
  }
  
  /**
   * Get a value from this Config file
   * 
   * @param key key to obtain
   * @return value corresponding to the key value
   */
  public String getValue (String key)
  {
    return this.valueMap.get (key);
  }
  
  /**
   * push a value into this Config file
   * 
   * @param key key to push into this config file
   * @param value value to push into this config file
   * @throws IOException 
   */
  public void pushValue (String key, String value) throws IOException
  {
    File config = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER 
            + "/" + world + "/config");
    BufferedWriter configWriter = new BufferedWriter (new FileWriter (config));

    this.valueMap.put (key, value);

    for (Entry<String, String> entry : this.valueMap.entrySet ())
    {
      configWriter.write (entry.getKey () + SEPARATOR + entry.getValue ());
      configWriter.newLine ();
    }

    configWriter.close ();
  }
}
