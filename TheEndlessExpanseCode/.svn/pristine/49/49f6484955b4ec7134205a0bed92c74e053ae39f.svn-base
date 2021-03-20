package halemaster.ee.ai;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.Statistic;

/**
 * @name AutoAttackAi
 * 
 * @version 0.0.0
 * 
 * @date Mar 17, 2014
 */
public class AutoAttackAI extends AI
{
  private Entity target;
  private float sinceLast;
  
  public Entity getTarget ()
  {
    return this.target;
  }
  
  public void setTarget (Entity target)
  {
    this.target = target;
  }
  
  /**
   * Update the Auto Attack
   * @param tpf delta since last update
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    if (null != this.target)
    {
      if (!this.target.isDead ())
      {
        this.sinceLast += tpf;
        if (this.sinceLast >= getEntity ().computeActionSpeed () &&
                getEntity ().isIdle () &&
                this.target.getExactLocation ().distance (getEntity ()
                .getExactLocation ()) <= getEntity ().getStat (Statistic.RANGE))
        {
          getEntity ().attack (this.target);

          this.sinceLast = 0;
        }
      }
      else
      {
        getEntity ().getHitBy ().remove (target);
        this.target = null;
      }
    }
  }

  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    // do nothing
  }
}
