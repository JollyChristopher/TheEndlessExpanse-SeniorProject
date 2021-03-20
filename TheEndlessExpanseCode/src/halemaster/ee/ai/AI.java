package halemaster.ee.ai;

import com.jme3.scene.control.AbstractControl;
import halemaster.ee.world.entity.Entity;

/**
 * @name AI
 * 
 * @version 0.0.0
 * 
 * @date Feb 19, 2014
 */
public abstract class AI extends AbstractControl
{
  private Entity entity;
  
  /**
   * Attach the given entity to this AI
   * @param entity entity to attach
   */
  public void attachEntity (Entity entity)
  {
    this.entity = entity;
    this.entity.getSprite ().getImage ().addControl (this);
  }
  
  /**
   * Detach entity from AI
   */
  public void detachEntity ()
  {
    if (null != this.entity)
    {
      this.entity.getSprite ().getImage ().removeControl (this);
      this.entity = null;
    }
  }
  
  public Entity getEntity ()
  {
    return this.entity;
  }
}
