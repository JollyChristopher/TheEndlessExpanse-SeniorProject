package halemaster.ee.GameState.Player;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
})

/**
 * @name TestAllPlayer
 * 
 * @version 0.0.0
 * 
 * @date Oct 21, 2013
 */
public class TestAllPlayer 
{
  /*
   * Player is a hand tested only! The trickery with the Input Manager and
   * Camera makes this impossible to unit test (Unit Testing was attempted to
   * be constructed, but the Input Manager seems to be the same one over and
   * over, meaning that previous tests fail future ones. Also, the updates only
   * seem to happen on the next run of a test.) Therefore, test the Player
   * Manager by hand, regularly (as would be expected!).
   */
}
