package halemaster.ee.ai;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.Game;
import halemaster.ee.world.entity.Entity;
import java.util.Map.Entry;

/**
 * @name ThreatAI
 * 
 * @version 0.0.0
 * 
 * @date Mar 17, 2014
 */
public class ThreatAI extends AI
{
  public static final float TARGET_SWITCH = 2;
  private float sinceSwitch = TARGET_SWITCH;
  
  /**
   * Called during the update
   * @param tpf delta since last update
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    AutoAttackAI attack = (AutoAttackAI) getEntity ().getAI 
            (Game.AUTO_ATTACK_AI);
    MovementAI move = (MovementAI) getEntity ().getAI (Game.MOVEMENT_AI);
    
    sinceSwitch += tpf;
    if (null == attack.getTarget () || sinceSwitch >= TARGET_SWITCH)
    {
      Entity target = null;
      int best = -1;
      for (Entry<Entity, Integer> attackers : 
              getEntity ().getHitBy ().entrySet ())
      {
        if (null == target || attackers.getValue () > best)
        {
          target = attackers.getKey ();
          best = attackers.getValue ();
        }
      }
      attack.setTarget (target);
      
      sinceSwitch = 0;
    }
    
    if (null != attack.getTarget ())
    {
      move.setMoveTo (attack.getTarget ().getExactLocation ());
    }
  }

  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    // do nothing
  }
}
