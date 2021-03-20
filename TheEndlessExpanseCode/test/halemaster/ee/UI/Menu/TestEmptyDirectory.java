package halemaster.ee.UI.Menu;

import halemaster.ee.Game;
import halemaster.ee.state.Menu;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestEmptyDirectory
 * 
 * @version 0.0.0
 * 
 * @date Oct 27, 2013
 */
public class TestEmptyDirectory 
{
  @Test
  public void folderNotExist ()
  {
    File file = null;
    
    try
    {
      Menu.emptyDirectory (file);
      Assert.fail("Expected a NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
    
    file = new File (Game.HOME + Game.GAME_FOLDER +"/aFolder");
    
    Menu.emptyDirectory (file);
  }
  
  @Test
  public void validFolder () throws IOException
  {
    File file = new File (Game.HOME + Game.GAME_FOLDER + "/aFolder");
    File innerFile = new File (file.getAbsoluteFile () + "/stuff");
    
    file.mkdirs ();
    innerFile.createNewFile ();
    
    Assert.assertTrue (innerFile.exists ());
    
    Menu.emptyDirectory (file);
    
    Assert.assertFalse (innerFile.exists ());
    
    file.delete ();
  }
}
