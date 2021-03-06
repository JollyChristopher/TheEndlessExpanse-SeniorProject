package halemaster.ee.NPC.faction;

import halemaster.ee.Game;
import halemaster.ee.TestAll;
import halemaster.ee.state.Menu;
import halemaster.ee.world.faction.DiplomaticGroup;
import halemaster.ee.world.faction.Faction;
import halemaster.ee.world.faction.FactionHandler;
import halemaster.ee.world.faction.Settlement;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestFaction
 * 
 * @version 0.0.0
 * 
 * @date Nov 10, 2013
 */
public class TestFaction 
{
  @Test
  public void changeDiplomacy ()
  {
    Faction faction = new Faction ("test", null);
    Faction otherFaction = new Faction ("test2", null);
    
    Assert.assertNull (faction.getDiplomacy (otherFaction));   
    Assert.assertNull (otherFaction.getDiplomacy (faction));   
    faction.changeDiplomacy (otherFaction, 0);
    Assert.assertEquals (DiplomaticGroup.NEUTRAL, 
            faction.getDiplomacy (otherFaction));
    Assert.assertEquals (DiplomaticGroup.NEUTRAL, 
            otherFaction.getDiplomacy (faction));
    
    faction.changeDiplomacy (otherFaction, DiplomaticGroup.FRIENDLY.getLow ());
    Assert.assertEquals (DiplomaticGroup.FRIENDLY, 
            otherFaction.getDiplomacy (faction));
    
    try
    {
      faction.changeDiplomacy (null, 0);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
  
  @Test
  public void getDiplomacy ()
  {
    Faction faction = new Faction ("test", null);
    Faction otherFaction = new Faction ("test2", null);
    
    Assert.assertNull (faction.getDiplomacy (otherFaction));   
    Assert.assertNull (otherFaction.getDiplomacy (faction));   
    faction.changeDiplomacy (otherFaction, 0);
    Assert.assertEquals (DiplomaticGroup.NEUTRAL, 
            faction.getDiplomacy (otherFaction));
    Assert.assertEquals (DiplomaticGroup.NEUTRAL, 
            otherFaction.getDiplomacy (faction));
    
    try
    {
      faction.getDiplomacy (null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
  
  @Test
  public void addSettlement ()
  {
    Faction faction = new Faction ("test", null);
    
    try
    {
      faction.addSettlement (0, 0, 0, null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
    Assert.assertEquals (0, faction.getSettlements ().length);
    faction.addSettlement (0, 0, 0, "hello");
    Assert.assertEquals (1, faction.getSettlements ().length);
    faction.addSettlement (0, 0, 0, "hello");
    Assert.assertEquals (1, faction.getSettlements ().length);
    faction.addSettlement (0, 0, 0, "test");
    Assert.assertEquals (2, faction.getSettlements ().length);
  }
  
  @Test
  public void getSettlements ()
  {
    Faction faction = new Faction ("test", null);
    Settlement[] settlements;
    
    for (int i = 0; i < 20; i++)
    {
      Assert.assertEquals (i, faction.getSettlements ().length);
      faction.addSettlement (0, 0, i, "name" + i);
    }
  }
  
  @Test
  public void save ()
  {
    Faction faction = new Faction ("test", null);
    Faction otherFaction = new Faction ("test2", null);
    File testFolder = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + TestAll.TEST_WORLD);
    
    faction.addSettlement (0, 0, 0, "testSettlement");
    faction.changeDiplomacy (otherFaction, 0);
    
    try
    {
      faction.saveFaction ("test");
      
      faction.saveFaction (null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
    catch (IOException e)
    {
      Assert.fail ("Shouldn't throw IOException");
    }
    
    Menu.emptyDirectory (testFolder);
    testFolder.delete ();
  }
  
  @Test
  public void load ()
  {
    Faction faction = new Faction ("test", null);
    Faction otherFaction = new Faction ("test2", null);
    FactionHandler handler = new FactionHandler ();
    File testFolder = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + TestAll.TEST_WORLD);
    
    faction.changeDiplomacy (otherFaction, 0);
    
    try
    {
      faction.saveFaction ("test");
      
      Faction.load ("test", handler);
    }
    catch (IOException e)
    {
      Assert.fail ("Shouldn't throw IOException");
    }
    
    Assert.assertEquals (faction.getName (), 
            handler.getFactions ()[0].getName ());
    Assert.assertEquals (faction.getDiplomacy (otherFaction), 
            handler.getFaction (faction.getName ())
            .getDiplomacy (otherFaction));
    
    Menu.emptyDirectory (testFolder);
    testFolder.delete ();
  }
  
  @Test
  public void staticLoader ()
  {
    File testFolder = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + TestAll.TEST_WORLD);
    Faction.resetFactions ();
    
    try
    {
      Assert.assertNull (Faction.getFaction (TestAll.TEST_WORLD,"test"));
      Faction.addFaction (TestAll.TEST_WORLD, "test");
      Assert.assertNotNull (Faction.getFaction (TestAll.TEST_WORLD,"test"));
      Assert.assertEquals ("test", Faction.getFactions (TestAll.TEST_WORLD)[0].getName ());
      Faction.addFaction (TestAll.TEST_WORLD,"test");
      Assert.assertEquals (1, Faction.getFactions (TestAll.TEST_WORLD).length);
      Faction.getFaction (TestAll.TEST_WORLD,"test").saveFaction ("test");
      Assert.assertEquals (1, Faction.getFactions (TestAll.TEST_WORLD).length);      
      Faction.load (TestAll.TEST_WORLD);
      Assert.assertEquals (1, Faction.getFactions (TestAll.TEST_WORLD).length);
    }
    catch (IOException e)
    {
      Assert.fail ("Shouldn't throw IOException");
    }

    Menu.emptyDirectory (testFolder);
    testFolder.delete ();
  }
}
