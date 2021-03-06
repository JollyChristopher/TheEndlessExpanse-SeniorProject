package halemaster.ee.state;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;

/**
 * @name HealthBarControl
 * 
 * @version 0.0.0
 * 
 * @date Feb 25, 2014
 */
public class HealthBarControl implements Controller
{  
  public static final int HEART_COUNT = 10;
  public static final int HEART_PARTS = 2;
  public static final String HEART_STRING = "heart_";
  public static final String HEART_PERCENT = "percentage";
  public static final String HEART_FULL = "Interface/Images/heart_full.png";
  public static final String HEART_HALF = "Interface/Images/heart_half.png";
  public static final String HEART_EMPTY = "Interface/Images/heart_empty.png";
  
  private Element[] hearts;
  private Nifty nifty;
  private float percentage = 1.0f;
  private NiftyImage fullHeart = null;
  private NiftyImage halfHeart = null;
  private NiftyImage emptyHeart = null;
  
  public void onStartScreen ()
  {
    // do nothing for now
  }
  
  public void onFocus (final boolean getFocus)
  {
    // do nothing for now
  }
  
  public boolean inputEvent (final NiftyInputEvent inputEvent)
  {
    // do nothing for now
    return false;
  }

  public void bind (Nifty nifty, Screen screen, Element element, 
          Properties parameter, Attributes controlDefinitionAttributes)
  {
    this.hearts = new Element[HEART_COUNT];
    this.nifty = nifty;
    
    for (int i = 0; i < this.hearts.length; i++)
    {
      this.hearts[i] = element.findElementByName (HEART_STRING + (i + 1));
    }
  }

  public void init (Properties parameter, Attributes controlDefinitionAttributes)
  {
    // do nothing for now
    float percent = controlDefinitionAttributes.getAsFloat (HEART_PERCENT);
    setHealthPercent (percent);
  }
  
  public void setHealthPercent (float percent)
  {
    int heartCount, fullCount;
    
    if (percent < 0)
    {
      percent = 0;
    }
    else if (percent > 1)
    {
      percent = 1;
    }
    
    if (null == this.fullHeart)
    {
      this.fullHeart = this.nifty.createImage (HEART_FULL, false);
    }
    if (null == this.halfHeart)
    {
      this.halfHeart = this.nifty.createImage (HEART_HALF, false);
    }
    if (null == this.emptyHeart)
    {
      this.emptyHeart = this.nifty.createImage (HEART_EMPTY, false);
    }
    
    this.percentage = percent;
    
    heartCount = (int) (HEART_COUNT * HEART_PARTS * this.percentage);
    fullCount = heartCount / HEART_PARTS;
    
    for (int i = 0; i < fullCount; i++)
    {
      this.hearts[i].getRenderer (ImageRenderer.class).setImage (this.fullHeart);
    }
    
    if (heartCount % 2 == 1)
    {
      this.hearts[fullCount].getRenderer (ImageRenderer.class)
              .setImage (this.halfHeart);
      fullCount++;
    }
    
    for (int i = fullCount; i < this.hearts.length; i++)
    {
      this.hearts[i].getRenderer (ImageRenderer.class)
              .setImage (this.emptyHeart);
    }
    
    this.hearts[0].getParent ().layoutElements ();
  }
}
