package halemaster.ee.state.start;

import halemaster.ee.state.Menu;

/**
 * @name MenuSplash
 * 
 * @version 0.0.0
 * 
 * @date Mar 19, 2014
 */
public class MenuSplash  extends Menu
{
  public static final String FILE = "Interface/menu/splashScreen.xml";
  public static final String ID = "splash";
  
  public MenuSplash ()
  {
    super (FILE, ID);
  }
  
  /**
   * Called when the screen is clicked or timer runs out
   * 
   * @return whether the click is consumed.
   */
  public boolean endSplash ()
  {
    changeMenu (new MenuStart ());
    return true;
  }
}
