package halemaster.ee.state.start;

import halemaster.ee.state.Menu;

/**
 * @name MenuStart
 * 
 * @version 0.0.0
 * 
 * @date Oct 23, 2013
 */
public class MenuStart extends Menu
{
  public static final String FILE = "Interface/menu/startup.xml";
  public static final String ID = "start";
  
  public MenuStart ()
  {
    super (FILE, ID);
  }
  
  /**
   * Called when the play button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean world ()
  {
    changeMenu (new MenuWorld ());
    return true;
  }
  
  /**
   * Called when the options button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean options ()
  {    
    changeMenu (new MenuOptions ());
    return true;
  }
}
