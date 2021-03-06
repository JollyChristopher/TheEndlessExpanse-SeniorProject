package halemaster.ee.Creation.macro.terrain;

import halemaster.ee.Game;
import halemaster.ee.TestAll;
import halemaster.ee.state.Menu;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestWorldGenerator
 * 
 * @version 0.0.0
 * 
 * @date Nov 3, 2013
 */
public class TestWorldGenerator 
{
  @Test
  public void basicWorld ()
  {
    File temp = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER + 
            "/" + TestAll.TEST_WORLD);
    
    temp.mkdirs ();
    
    try
    {
      BiomeClassifier.loadBiomes (Game.BIOME_FOLDER + "/mainBiomes");
    
    
      MacroTerrainGenerator gen = MacroTerrainGenerator.worldGenerator 
              (TestAll.TEST_WORLD, "seed");
      Thread thread = new Thread (gen);
      thread.start ();
    
      thread.join ();
      
      Assert.assertTrue (new File (Game.HOME + Game.GAME_FOLDER + 
              Game.WORLD_FOLDER + "/" + TestAll.TEST_WORLD + "/terrain.wld")
              .exists ());
      Menu.emptyDirectory (temp);
      temp.delete ();
      Assert.assertFalse (temp.exists ());
    }
    catch (InterruptedException e)
    {
      Assert.fail ("Should not throw exception");
    }
    catch (IOException e)
    {
      Assert.fail ("Should not throw exception");
    }
  }
}
