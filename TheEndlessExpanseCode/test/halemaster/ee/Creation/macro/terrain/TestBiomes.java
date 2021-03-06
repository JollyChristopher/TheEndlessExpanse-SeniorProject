package halemaster.ee.Creation.macro.terrain;

import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestBiomes
 * 
 * @version 0.0.0
 * 
 * @date Oct 31, 2013
 */
public class TestBiomes 
{
  @Test
  public void loadBiome ()
  {
    File biome = new File ("test/halemaster/ee/Creation/macro/terrain/biome1.json");
    
    try
    {
      BiomeClassifier.loadBiomes (biome);
    }
    catch (IOException e)
    {
      Assert.fail ("Should not have thrown exception");
    }
  }
  
  @Test
  public void failBiome ()
  {
    Biome biome = null;
    
    biome = BiomeClassifier.getBiome ("stuff");
    
    Assert.assertNull (biome);
  }
  
  @Test
  public void nullFile ()
  {
    try
    {
      BiomeClassifier.loadBiomes ((String) null);
      Assert.fail ("Should have thrown NullPointerException");
    }
    catch (IOException e)
    {
      Assert.fail ("Should not have thrown IOException");
    }
    catch (NullPointerException e)
    {
      
    }
    
    try
    {
      BiomeClassifier.loadBiomes ((File) null);
      Assert.fail ("Should have thrown NullPointerException");
    }
    catch (IOException e)
    {
      Assert.fail ("Should not have thrown IOException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
}
