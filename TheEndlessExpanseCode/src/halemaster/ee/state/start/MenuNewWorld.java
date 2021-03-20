package halemaster.ee.state.start;

import de.lessvoid.nifty.controls.TextField;
import halemaster.ee.Game;
import halemaster.ee.state.Menu;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import java.io.File;
import java.util.Random;

/**
 * @name MenuNewWorld
 * 
 * @version 0.0.0
 * 
 * @date Oct 24, 2013
 */
public class MenuNewWorld extends Menu
{
  public static final String FILE = "Interface/menu/newWorld.xml";
  public static final String ID = "newWorld";
  
  public MenuNewWorld ()
  {
    super (FILE, ID);
  }
  
  /**
   * Called when the create button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean create ()
  {
    TextField nameElement;
    TextField seedElement;
    String worldName;
    String seed;
    File worldsDirectory =  new File (Game.HOME + 
            Game.GAME_FOLDER + Game.WORLD_FOLDER);
    File worldDirectory;
    MacroTerrainGenerator generator;
    
    nameElement = getNifty ().getCurrentScreen ()
            .findNiftyControl ("name_text", TextField.class);
    seedElement = getNifty ().getCurrentScreen ()
            .findNiftyControl ("seed_text", TextField.class);
    
    worldName = nameElement.getText ();
    seed = seedElement.getText ();
    if ("".equals (seed))
    {
      seed = String.valueOf (new Random ().nextLong ());
    }
    
    worldDirectory =
            new File (worldsDirectory.getPath () + "/" + worldName);
    
    if (!worldsDirectory.exists ())
    {
      worldsDirectory.mkdirs ();
    }
    
    if (!worldDirectory.exists ())
    {
      worldDirectory.mkdirs ();
      
      generator = MacroTerrainGenerator.worldGenerator (worldName, seed);
      
      changeMenu (new MenuLoading (generator));
    }
    else
    {
      back ();
    }
    
    return true;
  }
}
