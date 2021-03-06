package halemaster.ee.UI.GUI;

import halemaster.ee.localization.Localizer;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestLocalizer
 * 
 * @version 0.0.0
 * 
 * @date Oct 15, 2013
 */
public class TestLocalizer 
{
  @Before
  public void setup ()
  {
    Localizer.changeLanguage (Locale.US);
  }
  
  @Test
  public void mainBundle ()
  {
    Assert.assertEquals ("Should be able to get localized", 
            "The Endless Expanse", Localizer.getString ("game.title"));
    
    try
    {
      Localizer.getString (null);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }    
  }
  
  @Test
  public void testBundle ()
  {
    Assert.assertEquals ("Should be able to get localized", "Test",
            Localizer.getString ("halemaster.ee.UI.GUI.Test", "test.one"));
    Assert.assertEquals ("Should not be changed.", "test.not.exist", 
            Localizer.getString ("halemaster.ee.UI.GUI.Test", 
            "test.not.exist"));
    try
    {
      Localizer.getString ("halemaster.ee.UI.GUI.Test", (String) null);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }
  }
  
  @Test
  public void changeLanguage ()
  {
    Localizer.changeLanguage (Locale.FRENCH);
    Assert.assertEquals ("Should be able to get localized", "Testu",
            Localizer.getString ("halemaster.ee.UI.GUI.Test", "test.one"));
  }
  
  @Test
  public void nullBundle ()
  {
    try
    {
      Localizer.getString (null, null);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }
    try
    {
      Localizer.getString (null, "test.not.exist");
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }
  }
}
