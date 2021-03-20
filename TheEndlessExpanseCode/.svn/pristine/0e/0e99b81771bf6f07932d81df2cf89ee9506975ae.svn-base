package halemaster.ee.state.start;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import halemaster.ee.state.Loader;
import halemaster.ee.state.Menu;

/**
 * @name MenuLoading
 * 
 * @version 0.0.0
 * 
 * @date Nov 4, 2013
 */
public class MenuLoading extends Menu
{
  public static final String FILE = "Interface/menu/loading.xml";
  public static final String ID = "loading";
  private Loader loader;
  
  public MenuLoading (Loader loader)
  {
    super (FILE, ID);
    
    this.loader = loader;
  }
  
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    Thread starter;
    
    super.initialize (manager, app);
    
    starter = new Thread (this.loader);
    starter.setName (this.loader.getName ());
    starter.start ();
  }
  
  @Override
  public void update (float tpf)
  {
    final int MIN_WIDTH = 32;
    Menu previousMenu;
    Element progressBarElement;
    Label textRenderer;
    int pixelWidth;
    
    super.update (tpf);
    
    if (null != getNifty ())
    {
      progressBarElement = getNifty ().getCurrentScreen ()
              .findElementByName("progressbar");
      textRenderer = getNifty ().getCurrentScreen ()
              .findNiftyControl ("loadingtext", Label.class);

      textRenderer.setColor (Color.WHITE);
      textRenderer.setText(this.loader.getMessage ());

      pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() 
              - MIN_WIDTH) * this.loader.getPercentage ());
      progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
      progressBarElement.getParent().layoutElements();

      if (Loader.DONE <= this.loader.getPercentage ())
      {
        previousMenu = getPrevious ();
        back ();
        previousMenu.back ();
      }
    }
  }
}
