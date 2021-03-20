package halemaster.ee.NPC.faction;

import halemaster.ee.world.faction.DiplomaticGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestDiplomaticGroup
 * 
 * @version 0.0.0
 * 
 * @date Nov 10, 2013
 */
public class TestDiplomaticGroup 
{
  @Test
  public void getGroup ()
  {
    for (DiplomaticGroup group : DiplomaticGroup.values ())
    {
      Assert.assertEquals (group, DiplomaticGroup.getGroup (group.getLow () +
              (Math.abs (group.getHigh () - group.getLow ()) / 2)));
    }
  }
}
