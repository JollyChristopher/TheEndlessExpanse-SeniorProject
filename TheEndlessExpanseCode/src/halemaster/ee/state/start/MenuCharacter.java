package halemaster.ee.state.start;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.tools.SizeValue;
import halemaster.ee.Game;
import halemaster.ee.localization.Localizer;
import halemaster.ee.state.Hud;
import halemaster.ee.state.Menu;
import halemaster.ee.state.PlayerState;
import java.io.File;

/**
 * @name MenuCharacter
 * 
 * @version 0.0.0
 * 
 * @date Oct 24, 2013
 */
public class MenuCharacter extends Menu
{
  public static final String FILE = "Interface/menu/characterSelect.xml";
  public static final String ID = "character";
  public static final int CHARACTERS_SHOWN = 5;
  private String worldName;
  
  public MenuCharacter (String worldName)
  {
    super (FILE, ID);
    
    this.worldName = worldName;
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
    File playersDirectory =  new File (Game.HOME + 
            Game.GAME_FOLDER + Game.PLAYER_FOLDER);
    
    super.initialize (manager, app);
    
    if (playersDirectory.exists () && playersDirectory.isDirectory ())
    {
      for (File player : playersDirectory.listFiles ())
      {
        if (player.isDirectory ())
        {
          addCharacter (player.getName ());
        }
      }
    }
  }
  
  /**
   * Called when the new button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean newCharacter ()
  {
    changeMenu (new MenuNewCharacter ());
    
    return true;
  }
  
  /**
   * Called when a character button should be added of the given name.
   * 
   * @param name the name of the character that is added.
   */
  public void addCharacter (final String name)
  {
    Element characterPanel = getNifty ().getCurrentScreen ()
            .findElementByName ("characters");
    final int height = getNifty ().getCurrentScreen ()
            .findElementByName ("scrollCharacters").getHeight () 
            / CHARACTERS_SHOWN;
    
    characterPanel.setConstraintHeight (new SizeValue 
            (String.valueOf(characterPanel.getHeight () + height) + "px"));
    
    new PanelBuilder("Character_" + name) {{
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
        interactOnClick("selectCharacter(" + name + ")");
      }});
      
      control(new ButtonBuilder("Delete_" + name, 
              Localizer.getString ("game.menu.delete")) {{
        height("100%");
        width("20%");
        valignCenter ();
        visibleToMouse(true);
        interactOnClick("deleteCharacter(" + name + ")");
      }});
    }}.build (getNifty (), getScreen (), characterPanel);
  }
  
  /**
   * Called when a character is selected.
   * 
   * @param character the name of the character selected.
   * @return whether the click is consumed.
   */
  public boolean selectCharacter (String character)
  {
    PlayerState state = new PlayerState (this.worldName, character);
    Hud hud;
    
    getGame ().addAppState (state);
    hud = new Hud (state);
    changeMenu (hud);
    state.setHud (hud);
    
    return true;
  }
  
  /**
   * Called when a character is deleted.
   * 
   * @param character the name of the character to delete.
   * @return whether the click is consumed.
   */
  public boolean deleteCharacter (String character)
  {
    Element deletePanel, parentPanel;
    final int height = getNifty ().getCurrentScreen ()
            .findElementByName ("scrollCharacters").getHeight () / 
            CHARACTERS_SHOWN;
    File characterDirectory =
            new File (Game.HOME + 
            Game.GAME_FOLDER + Game.PLAYER_FOLDER + "/" + character);
    
    Menu.emptyDirectory (characterDirectory);
    characterDirectory.delete ();
    
    deletePanel = getNifty ().getCurrentScreen ()
            .findElementByName ("Character_" + character);
    parentPanel = deletePanel.getParent ();
    
    parentPanel.setConstraintHeight (new SizeValue 
            (String.valueOf(parentPanel.getHeight () - height) + "px"));
    
    deletePanel.markForRemoval ();
    
    return true;
  }
}
