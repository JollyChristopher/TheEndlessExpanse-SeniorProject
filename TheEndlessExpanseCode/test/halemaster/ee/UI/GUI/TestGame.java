package halemaster.ee.UI.GUI;

import com.jme3.app.state.AppState;
import halemaster.ee.Game;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestGame
 * 
 * @version 0.0.0
 * 
 * @date Oct 15, 2013
 */
public class TestGame 
{
  public static final Logger LOGGER = Logger.getAnonymousLogger ();
  
  private Game game;
  
  @Before
  public void setup ()
  {
    game = new Game ();
    game.setShowSettings (false);
  }
  
  @After
  public void teardown ()
  {
    game.stop ();
  }
  
  @Test
  public void handleError ()
  {    
    try
    {
      game.handleError (null, null);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }
    
    game.start ();
    game.handleError (null, null);
    game.handleError ("Message", null);
    game.handleError (null, new Exception ("Message"));
    game.handleError ("Message", new Exception ("Message"));
  }
  
  @Test
  public void addRemoveAppState ()
  {
    game.start ();
    AppState state = new MockAppState ();
    
    Assert.assertFalse (game.getStateManager ().hasState (state));
    game.addAppState (state);
    Assert.assertTrue (game.getStateManager ().hasState (state));
    game.removeAppState (state);
    Assert.assertFalse (game.getStateManager ().hasState (state));
    
    try
    {
      game.addAppState (null);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
    }
    game.removeAppState (null);
  }
}
