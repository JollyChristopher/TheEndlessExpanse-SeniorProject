package halemaster.ee.state.start;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.render.NiftyImage;
import halemaster.ee.Game;
import halemaster.ee.localization.NameGen;
import halemaster.ee.state.Menu;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.PlayerState.PLAYER_IMAGE;
import halemaster.ee.world.entity.Entity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

/**
 * @name MenuNewCharacter
 * 
 * @version 0.0.0
 * 
 * @date Oct 24, 2013
 */
public class MenuNewCharacter extends Menu
{
  public static final String FILE = "Interface/menu/newCharacter.xml";
  public static final String ID = "newCharacter";
  
  public MenuNewCharacter ()
  {
    super (FILE, ID);
  }
  
  /**
   * Initialize this menu
   * @param manager
   * @param app 
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    super.initialize (manager, app);
    ImageSelect image;
    NiftyImage subImage;
    
    image = getNifty ().getCurrentScreen ()
            .findNiftyControl ("player_image", ImageSelect.class);
    for (PLAYER_IMAGE img : PLAYER_IMAGE.values ())
    {
      subImage = getNifty ().createImage (img.getImages ().get 
              (Entity.CHANNEL_LEFT)[0], false);
      image.addImage (subImage);
    }
  }
  
  /**
   * Called when the create button is clicked.
   * 
   * @return whether the click is consumed.
   */
  public boolean create ()
  {
    TextField nameElement;
    ImageSelect image;
    String characterName;
    File charactersDirectory =  new File (Game.HOME + 
            Game.GAME_FOLDER + Game.PLAYER_FOLDER);
    File characterDirectory;
    File file;
    BufferedWriter writer;
    
    nameElement = getNifty ().getCurrentScreen ()
            .findNiftyControl ("name_text", TextField.class);
    image = getNifty ().getCurrentScreen ()
            .findNiftyControl ("player_image", ImageSelect.class);
    
    characterName = nameElement.getRealText ();
    
    if ("".equals (characterName))
    {
      characterName = NameGen.getName (new Random (), "npc");
    }
    
    file = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER + 
            "/" + characterName + "/" + PlayerState.PLAYER_IMAGE_FILE);
    
    characterDirectory =
            new File (charactersDirectory.getPath () + "/" + characterName);
    
    if (!charactersDirectory.exists ())
    {
      charactersDirectory.mkdirs ();
    }
    
    if (!characterDirectory.exists ())
    {
      characterDirectory.mkdirs ();
      
      try
      {
        file.createNewFile ();
        writer = new BufferedWriter (new FileWriter (file));

        writer.write (PLAYER_IMAGE.values ()[image.getSelectedImageIndex ()].name ());

        writer.close ();
      }
      catch (IOException e)
      {
        LOGGER.log (Level.SEVERE, "Could not save player", e);
      }
    }
    
    back ();
    
    return true;
  }
}
