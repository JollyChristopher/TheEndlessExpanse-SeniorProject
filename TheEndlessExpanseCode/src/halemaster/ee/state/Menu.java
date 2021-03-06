package halemaster.ee.state;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import halemaster.ee.Game;
import halemaster.ee.localization.Localizer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name StartupState
 * 
 * @version 0.0.0
 * 
 * @date Oct 22, 2013
 */
public abstract class Menu extends AbstractAppState implements ScreenController
{
  public static final Logger LOGGER = Logger.getLogger (Menu.class.getName ());
  private Game game;
  private AppStateManager manager;
  private Nifty nifty;
  private Screen screen;
  private NiftyJmeDisplay niftyDisplay = null;
  private String file;
  private String id;
  private Menu previous = null;
  
  public Menu (String file, String id)
  {
    this.file = file;
    this.id = id;
  }
  
  /**
   * Empties all files and directories out of the given directory.
   * 
   * @param directory the directory to empty.
   */
  public static void emptyDirectory (File directory)
  {
    if (directory.isDirectory ())
    {
      for (File file : directory.listFiles ())
      {
        emptyDirectory (file);
        file.delete ();
      }
    }
    
    LOGGER.log (Level.FINEST, "{0} emptied", directory.toString ());
  }
  
  /**
   * Initialize this app state.
   * 
   * @param manager the manager to initialize with this app state.
   * @param app the application to initialize with this app state.
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    super.initialize (manager, app);
    
    this.game = (Game) app;
    this.manager = manager;

    this.niftyDisplay = new NiftyJmeDisplay(
            this.game.getAssetManager (), this.game.getInputManager (), 
            this.game.getAudioRenderer (), this.game.getGuiViewPort ());
    this.nifty = this.niftyDisplay.getNifty();
    this.nifty.fromXml(this.file, this.id, this);
    this.game.getGuiViewPort ().addProcessor(this.niftyDisplay);
  }
  
  /**
   * Perform any cleanup operations necessary.
   */
  @Override
  public void cleanup ()
  {
    this.game.getGuiViewPort ().removeProcessor (this.niftyDisplay);
    super.cleanup ();
  }
  
  /**
   * Bind the nifty and screen to this menu.
   * 
   * @param nifty the nifty to bind.
   * @param screen the screen to bind.
   */
  public void bind(Nifty nifty, Screen screen) 
  {
    this.nifty = nifty;
    this.screen = screen;
  }
 
  /**
   * Called when the screen is started.
   */
  public void onStartScreen()
  {
  }
 
  /**
   * Called when the screen is removed.
   */
  public void onEndScreen() 
  {
  }
  
  /**
   * Change the current menu to a different menu.
   * 
   * @param menu the menu to change to.
   */
  public void changeMenu (Menu menu)
  {
    menu.previous = this;
    this.game.addAppState (menu);
    this.game.removeAppState (this);
  }
  
  /**
   * Start the custom effect of the given id
   * @param id id of the element
   * @param key key of the effect in the element
   */
  public void startEffect(final String id, final String key) 
  {
    Element element = this.nifty.getCurrentScreen().findElementByName(id);
    element.startEffect(EffectEventId.onCustom, null, key);
  }
  
  /**
   * return to the previous Menu, if there is one.
   */
  public void back ()
  {
    if (null != this.previous)
    {
      this.game.addAppState (this.previous);
      this.game.removeAppState (this);
    }
  }
  
  public String localize (String key)
  {
    return Localizer.getString (key);
  }
  
  public Menu getPrevious ()
  {
    return this.previous;
  }

  public Game getGame ()
  {
    return game;
  }

  public AppStateManager getManager ()
  {
    return manager;
  }

  public Nifty getNifty ()
  {
    return nifty;
  }

  public Screen getScreen ()
  {
    return screen;
  }

  public NiftyJmeDisplay getNiftyDisplay ()
  {
    return niftyDisplay;
  }
}
