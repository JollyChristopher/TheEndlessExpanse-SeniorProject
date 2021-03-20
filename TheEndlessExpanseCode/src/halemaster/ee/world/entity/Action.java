package halemaster.ee.world.entity;

import com.jme3.math.Vector2f;
import halemaster.ee.Overlay;
import halemaster.ee.Sprite;
import halemaster.ee.item.ItemSet;
import java.util.List;
import java.util.Random;

/**
 * @name Action
 * 
 * @version 0.0.0
 * 
 * @date Mar 16, 2014
 */
public class Action 
{  
  private Entity target;
  private ItemSet item;
  
  public Action (Entity target, ItemSet item)
  {
    this.target = target;
    this.item = item;
  }

  public Entity getTarget ()
  {
    return target;
  }

  public ItemSet getItem ()
  {
    return item;
  }
  
  public boolean act (Entity user)
  {
    boolean worked = true;
    
    if (null == this.item)
    {
      user.attackEntity (this.target);
    }
    else
    {
      if (this.item.getItem ().useItem (user, this.target) &&
              this.item.getItem ().isUseUp ())
      {
        user.getInventory ().useUpItem (this.item.getItem ());
      }
      else
      {
        worked = false;
      }
    }
    
    return worked;
  }
}
