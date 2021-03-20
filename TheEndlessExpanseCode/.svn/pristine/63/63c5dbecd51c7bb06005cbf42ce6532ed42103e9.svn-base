package halemaster.ee.NPC.faction;

import halemaster.ee.world.Area;
import halemaster.ee.world.faction.Settlement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestSettlement
 * 
 * @version 0.0.0
 * 
 * @date Nov 10, 2013
 */
public class TestSettlement 
{
  @Test
  public void addAreas ()
  {
    Settlement settlement = new Settlement ("test", null);
    
    Assert.assertEquals (0, settlement.getAreas ().length);
    settlement.addArea (0, 10);
    Assert.assertEquals (1, settlement.getAreas ().length);
    Assert.assertEquals (0, settlement.getAreas ()[0].getX ());
    Assert.assertEquals (10, settlement.getAreas ()[0].getY ());
    
    settlement = new Settlement ("test", null);
    
    for (int i = 0; i < 20; i++)
    {
      settlement.addArea (i / 2, i / 2);
    }
    
    Assert.assertEquals (10, settlement.getAreas ().length);
  }
  
  @Test
  public void getAreas ()
  {
    Settlement settlement = new Settlement ("test", null);
    
    for (int i = 0; i < 20; i++)
    {
      for (int j = 0; j < 20; j++)
      {
        Assert.assertEquals (i * 20 + j, settlement.getAreas ().length);
        settlement.addArea (i, j);
      }
    }
    
    Assert.assertEquals (20 * 20, settlement.getAreas ().length);
  }
  
  @Test
  public void removeAreas ()
  {
    Settlement settlement = new Settlement ("test", null);
    Area area;
    
    Assert.assertEquals (0, settlement.getAreas ().length);
    settlement.addArea (0, 10);
    Assert.assertEquals (1, settlement.getAreas ().length);
    area = settlement.removeArea (0, 10);
    Assert.assertEquals (0, settlement.getAreas ().length);
    Assert.assertEquals (0, area.getX ());
    Assert.assertEquals (10, area.getY ());
    
    for (int i = 0; i < 20; i++)
    {
      settlement.addArea (i, i / 2);
    }
    
    Assert.assertEquals (20, settlement.getAreas ().length);
    Assert.assertNull (settlement.removeArea (0, 10));
    Assert.assertEquals (20, settlement.getAreas ().length);
  }
}
