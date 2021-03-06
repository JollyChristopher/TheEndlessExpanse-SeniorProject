package halemaster.ee.ai;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.Game;
import halemaster.ee.quest.Offer;
import java.util.Random;

/**
 * @name OfferAI
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 * 
 * NOTE: The randoms in this class is ok, and does NOT need to be based off of
 * the world seed. This is because we want different offers and wait times
 * each time the game is started. This means that although 2 worlds are the same
 * , the rate and time in which npcs offer quests varies! The quests will still
 * choose from the same list.
 */
public class OfferAI extends AI
{
  public static final float MAX_TIME = 5*60;
  public static final float MIN_TIME = 30;
  
  private float sinceLast = 0.0f;
  private float waitTime = new Random ().nextFloat () * 
          (MAX_TIME - MIN_TIME + 1) + MIN_TIME;
  private String[] offers = null;
  
  /**
   * Called during update of ai
   * @param tpf time since last frame 
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    this.sinceLast += tpf;
    if (this.sinceLast > this.waitTime)
    {
      if (null == this.offers)
      {
        if (null != getEntity ().getData (Offer.OFFER_LIST))
        {
          this.offers = getEntity ().getData (Offer.OFFER_LIST)
                  .split (Offer.OFFER_SEPARATOR);
        }
      }
      
      if (offers.length > 0)
      {
        getEntity ().setData (Offer.OFFER_CURRENT, 
                offers[new Random ().nextInt (offers.length)]);
      }
      
      this.sinceLast = 0f;
    }
  }

  /**
   * Called for rendering the AI
   * @param rm render manager used
   * @param vp view part used
   */
  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    // does nothing. Overridden for abstract
  }
}
