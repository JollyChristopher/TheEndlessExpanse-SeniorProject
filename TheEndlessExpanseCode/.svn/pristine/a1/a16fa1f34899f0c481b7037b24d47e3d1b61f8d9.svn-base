package halemaster.ee.Creation.macro.history;

import halemaster.ee.Game;
import halemaster.ee.TestAll;
import halemaster.ee.state.Menu;
import halemaster.ee.world.Config;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestConfig
 * 
 * @version 0.0.0
 * 
 * @date Jan 23, 2014
 */
public class TestConfig 
{
  @Test
  public void newConfig ()
  {
    try
    {
      Config config = new Config (TestAll.TEST_WORLD);
      File temp = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER + 
            "/" + TestAll.TEST_WORLD);
    
      temp.mkdirs ();
      
      try
      {
        config.getValue (null);
        config.pushValue (null, null);
        config.pushValue ("test", null);
      }
      catch (NullPointerException e)
      {
        Assert.fail ("Should not throw NullPointerException");
      }
      Assert.assertNull ("Should be null", config.getValue ("test"));
      config.pushValue ("test", "stuff");
      Assert.assertEquals ("Did not get correct value!", "stuff", 
              config.getValue ("test"));
      Menu.emptyDirectory (temp);
      temp.delete ();
    }
    catch (IOException e)
    {
      Assert.fail ("Should not throw IOException");
    }
  }
  
  @Test
  public void staticConfig ()
  {
    File temp = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER + 
            "/" + TestAll.TEST_WORLD);
    temp.mkdirs ();
    
    try
    {      
      try
      {
        Config.getValue (null, null);
        Config.getValue (TestAll.TEST_WORLD, null);
        Config.pushValue (TestAll.TEST_WORLD, null, null);
        Config.pushValue (TestAll.TEST_WORLD, "test", null);
      }
      catch (NullPointerException e)
      {
        Assert.fail ("Should not throw NullPointerException");
      }
      
      try
      {
        Config.pushValue (null, null, null);
        Assert.fail ("Should throw IOException");
      }
      catch (NullPointerException e)
      {
        Assert.fail ("Should not throw NullPointerException");
      }
      catch (IOException e)
      {
        
      }
      
      Assert.assertNull ("Should be null", Config.getValue (TestAll.TEST_WORLD,
              "test"));
      Config.pushValue (TestAll.TEST_WORLD, "test", "stuff");
      Assert.assertEquals ("Did not get correct value!", "stuff", 
              Config.getValue (TestAll.TEST_WORLD, "test"));
    }
    catch (IOException e)
    {
      Assert.fail ("Should not throw IOException");
    }
    
    Menu.emptyDirectory (temp);
    temp.delete ();
  }
}
