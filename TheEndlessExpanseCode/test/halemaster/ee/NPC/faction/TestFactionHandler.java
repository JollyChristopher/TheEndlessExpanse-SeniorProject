package halemaster.ee.NPC.faction;

import halemaster.ee.world.faction.FactionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestFactionHandler
 * 
 * @version 0.0.0
 * 
 * @date Nov 10, 2013
 */
public class TestFactionHandler 
{
  @Test
  public void addFaction ()
  {
    FactionHandler handler = new FactionHandler ();
    
    Assert.assertEquals (0, handler.getFactions ().length);
    handler.addFaction ("test");
    Assert.assertEquals (1, handler.getFactions ().length);
    
    try
    {
      handler.addFaction ((String) null);
    }
    catch (NullPointerException e)
    {
      Assert.fail ("Should not throw NullPointer");
    }
  }
  
  @Test
  public void getFactionByName ()
  {
    FactionHandler handler = new FactionHandler ();
    
    Assert.assertEquals (0, handler.getFactions ().length);
    handler.addFaction ("test");
    Assert.assertEquals (1, handler.getFactions ().length);
    
    Assert.assertNull (handler.getFaction (null));
    Assert.assertNull (handler.getFaction ("nothing"));
    Assert.assertNotNull (handler.getFaction ("test"));
  }
  
  @Test
  public void getFactions ()
  {
    FactionHandler handler = new FactionHandler ();
    
    Assert.assertEquals (0, handler.getFactions ().length);
    handler.addFaction ("test");
    Assert.assertEquals (1, handler.getFactions ().length);
    
    Assert.assertEquals ("test", handler.getFactions ()[0].getName ());
  }
}
