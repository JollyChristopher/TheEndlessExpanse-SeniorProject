package halemaster.ee.Creation.macro.history;

import halemaster.ee.world.Area;
import halemaster.ee.world.history.event.Event;
import halemaster.ee.world.history.event.type.EventType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestEvent
 * 
 * @version 0.0.0
 * 
 * @date Jan 23, 2014
 */
public class TestEvent 
{
  private Event testEvent;
  private EventType testType;
  
  @Before
  public void setup ()
  {
    testType = new EventType ();
    testType.setId ("test");
    
    testEvent = new Event ();
    testEvent.setDate (10);
    testEvent.setHolder (null);
    testEvent.setLocation (Area.ANYWHERE.getX (), Area.ANYWHERE.getY ());
    testEvent.setLock (Event.UNLOCKED);
    testEvent.setSettled (true);
    testEvent.setType (testType);
  }
  
  @Test
  public void getDescription ()
  {
    Assert.assertEquals ("Messages do Not match", "event.test", 
            testEvent.getDescription ());
  }
  
  @Test
  public void shake ()
  {
    try
    {
      testEvent.shake ();
      testEvent.unShake ();
      testEvent.shake ();
      Assert.fail ("Should throw NullPointerException");
    }
    catch (InstantiationException e)
    {
      Assert.fail ("Should not throw InstantiationException");
    }
    catch (IllegalAccessException e)
    {
      Assert.fail ("Should not throw IllegalAccessException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
  
  @Test
  public void variables ()
  {
    try
    {
      testEvent.getVariable ("test");
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
}
