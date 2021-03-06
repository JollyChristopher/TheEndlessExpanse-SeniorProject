package halemaster.ee.ai;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.item.Reward;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.Statistic;
import java.util.Random;

/**
 * @name ShopAI
 * 
 * @version 0.0.0
 * 
 * @date Apr 2, 2014
 */
public class ShopAI extends AI
{  
  public static final float NEW_FREQUENCY = .05f;
  public static final float RESET_FREQUENCY = .001f;
  public static final int RESET_SIZE = 3;
  public static final String SHOPPING = "shopping";
  
  private float passed = 0.0f;
 
  /**
   * Attach the given entity to this AI
   * @param entity entity to attach
   */
  @Override
  public void attachEntity (Entity entity)
  {
    super.attachEntity (entity);
    if (entity.getInventory ().isEmpty ())
    {
      resetShop ();
    }
  }
  
  private void resetShop ()
  {
    getEntity ().getInventory ().clear ();
    for (int i = 0; i < RESET_SIZE; i++)
    {
      addNew ();
    }
  }
  
  private void addNew ()
  {
    Reward reward = Reward.getReward (getEntity ().getStat (Statistic.LEVEL));
    if (reward.getItems ().length < getEntity ().getInventory ()
            .getFreeSpace ())
    {
      getEntity ().giveReward (reward, false);
    }
  }
  
  /**
   * Called when Update is called
   * @param tpf delta since last update
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    Random random; // this is NOT based on seed by design!
    String shop = getEntity ().getData (SHOPPING);
    if (null == shop || !Boolean.valueOf (shop))
    {
      this.passed += tpf;
      if (this.passed >= 1.0f)
      {
        random = new Random ();
        if (random.nextFloat () < NEW_FREQUENCY)
        {
          addNew ();
        }
        if (random.nextFloat () < RESET_FREQUENCY)
        {
          resetShop ();
        }

        this.passed = 0.0f;
      }
    }
  }

  /**
   * Not used. Called for Render control
   * @param rm render manager
   * @param vp view Point
   */
  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    // does nothing
  }
}
