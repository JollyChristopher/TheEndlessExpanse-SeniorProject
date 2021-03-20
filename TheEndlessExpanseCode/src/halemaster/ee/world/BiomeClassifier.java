package halemaster.ee.world;

import halemaster.ee.Json;
import halemaster.ee.world.terrain.tile.BiomeGeneration;
import halemaster.ee.world.terrain.tile.Tile;
import halemaster.ee.world.terrain.tile.TiledType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name BiomeClassifier
 * 
 * @version 0.0.0
 * 
 * @date Oct 30, 2013
 */
public class BiomeClassifier 
{
  public static final Biome DEFAULT = new Biome ("Ocean", 
          "0000ff00", (byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE, 
          Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE,
          new BiomeGeneration (new Tile[] {new Tile (new TiledType("block", 0),
          0, 0, new String[]{"Textures/terrain/basic/ocean.png"})}, new int[] {0}));
  private static BiomeClassifier singleton = new BiomeClassifier ();
  private Map<String, Biome> biomes = new HashMap<String, Biome> ();
  
  public BiomeClassifier ()
  {
    setBiome (DEFAULT);
  }
  
  /**
   * Load the JSON file of biomes into the current classifier.
   * 
   * @param biomes file to load into the classifier.
   * @throws IOException on failure to save/load from the file.
   */
  public void loadBiomeFile (File biomeFile) throws IOException
  {
    Biome[] biomesInFile = Json.getFromFile (biomeFile, Biome.class);
    
    for (Biome biome : biomesInFile)
    {
      setBiome (biome);
    }
  }
  
  /**
   * Return the biome with the given name
   * 
   * @param name name of the biome
   * @return the biome found
   */
  public Biome getBiomeByName (String name)
  {
    return this.biomes.get (name);
  }
  
  /**
   * get a list of all biomes in the classifier
   * @return list of all biomes in the classifier
   */
  public List<Biome> getAllBiomes ()
  {
    List<Biome> allBiomes = new ArrayList<Biome> ();
    
    for (Biome biome : this.biomes.values ())
    {
      allBiomes.add (biome);
    }
    
    return allBiomes;
  }
  
  /**
   * Set the biome of the given id to the biome given.
   * 
   * @param biome biome to set into biome array.
   */
  private void setBiome (Biome biome)
  {
    this.biomes.put (biome.getName (), biome);
  }
  
  /**
   * load the biomes from the given path on the singleton classifier.
   * 
   * @param biomes path to load from.
   * @throws IOException failure to save/load from file.
   */
  public static void loadBiomes (String biomes) throws IOException
  {
    loadBiomes (new File (biomes));
  }
  
  /**
   * load the biomes from the given file on the singleton classifier.
   * 
   * @param biomes file to load from.
   * @throws IOException failure to save/load from file.
   */
  public static void loadBiomes (File biomes) throws IOException
  {
    singleton.loadBiomeFile (biomes);
  }
  
  /**
   * Return the Biome witht he given name from the singleton classifier
   * @param name name of biome
   * @return Biome with the name
   */
  public static Biome getBiome (String name)
  {
    return singleton.getBiomeByName (name);
  }
  
  /**
   * Get all the biomes of the singleton
   * @return list of all biomes
   */
  public static List<Biome> getBiomes ()
  {
    return singleton.getAllBiomes ();
  }
}
