package halemaster.ee.state.start;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.tools.SizeValue;
import halemaster.ee.Game;
import halemaster.ee.localization.Localizer;
import halemaster.ee.state.Menu;
import halemaster.ee.state.PlayerState;
import java.io.File;

/**
 * @name MenuWorld
 * 
 * @version 0.0.0
 * 
 * @date Oct 23, 2013
 */
public class MenuWorld extends Menu
{
  public static final String FILE = "Interface/menu/worldSelect.xml";
  public static final String ID = "world";
  public static final int WORLDS_SHOWN = 5;
  
  public MenuWorld ()
  {
    super (FILE, ID);
  }
  
  /**
   * Initialize this menu.
   * 
   * @param manager manager to initialize with.
   * @param app application to initialize with.
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    File worldsDirectory =  new File (Game.HOME + 
            Game.GAME_FOLDER + Game.WORLD_FOLDER);
    
    super.initialize (manager, app);
    
    if (worldsDirectory.exists () && worldsDirectory.isDirectory ())
    {
      for (File world : worldsDirectory.listFiles ())
      {
        if (world.isDirectory ())
        {
          addWorld (world.getName ());
        }
      }
    }
  }
  
  /**
   * Called when the new button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean newWorld ()
  {
    changeMenu (new MenuNewWorld ());
    
    return true;
  }
  
  /**
   * Called when a world button should be added of the given name.
   * 
   * @param name the name of the world that is added.
   */
  public void addWorld (final String name)
  {
    Element worldPanel = getNifty ().getCurrentScreen ()
            .findElementByName ("worlds");
    final int height = getNifty ().getCurrentScreen ()
            .findElementByName ("scrollWorlds").getHeight () / WORLDS_SHOWN;
    
    worldPanel.setConstraintHeight (new SizeValue 
            (String.valueOf(worldPanel.getHeight () + height) + "px"));
    
    new PanelBuilder("World_" + name) {{
      childLayoutHorizontal();   
      height(String.valueOf(height) + "px");
      width("100%");
      valignCenter ();
      visibleToMouse(true);

      control(new ButtonBuilder("Select_" + name, name) {{
        height("100%");
        width("80%");
        valignCenter ();
        visibleToMouse(true);
        interactOnClick("selectWorld(" + name + ")");
      }});
      
      control(new ButtonBuilder("Delete_" + name, 
              Localizer.getString ("game.menu.delete")) {{
        height("100%");
        width("20%");
        valignCenter ();
        visibleToMouse(true);
        interactOnClick("deleteWorld(" + name + ")");
      }});
    }}.build (getNifty (), getScreen (), worldPanel);
  }
  
  /**
   * Called when a world is selected.
   * 
   * @param world the name of the world selected.
   * @return whether the click is consumed.
   */
  public boolean selectWorld (String world)
  {
    changeMenu (new MenuCharacter (world));
    
    return true;
  }
  
  /**
   * Called when a world is deleted.
   * 
   * @param world the name of the world to delete.
   * @return whether the click is consumed.
   */
  public boolean deleteWorld (String world)
  {
    Element deletePanel, parentPanel;
    final int height = getNifty ().getCurrentScreen ()
            .findElementByName ("scrollWorlds").getHeight () / WORLDS_SHOWN;
    File worldDirectory =
            new File (Game.HOME + 
            Game.GAME_FOLDER + Game.WORLD_FOLDER + "/" + world);
    File charWorldDirectory;
    File charDirectory = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.PLAYER_FOLDER);
    
    Menu.emptyDirectory (worldDirectory);
    worldDirectory.delete ();
    
    if (charDirectory.exists ())
    {
      for (File charDir : charDirectory.listFiles ())
      {
        charWorldDirectory = new File (charDir.getPath () + PlayerState.AREA + 
                "/" + world);
        Menu.emptyDirectory (charWorldDirectory);
        charWorldDirectory.delete ();
      }
    }
    
    deletePanel = getNifty ().getCurrentScreen ()
            .findElementByName ("World_" + world);
    parentPanel = deletePanel.getParent ();
    
    parentPanel.setConstraintHeight (new SizeValue 
            (String.valueOf(parentPanel.getHeight () - height) + "px"));
    
    deletePanel.markForRemoval ();
    
    return true;
  }
}
