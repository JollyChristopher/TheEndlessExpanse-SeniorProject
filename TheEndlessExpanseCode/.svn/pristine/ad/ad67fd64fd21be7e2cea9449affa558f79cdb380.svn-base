package halemaster.ee.item;

import halemaster.ee.world.entity.Effect;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @name StatModifier
 * 
 * @version 0.0.0
 * 
 * @date Mar 23, 2014
 */
public class StatModifier 
{  
  private float damageLower;
  private float damageUpper;
  private Map<String, Float> boost;
  private Effect[] effects = new Effect[0];

  public int getDamageLower ()
  {
    return (int) damageLower;
  }
  
  public int getDamageLower (int level)
  {
    return (int) (getDamageLower () * level);
  }

  public int getDamageUpper ()
  {
    return (int) damageUpper;
  }
  
  public int getDamageUpper (int level)
  {
    return (int) (getDamageUpper () * level);
  }

  public Map<String, Float> getBoost ()
  {
    return boost;
  }
  
  public int getBoost (String id, int level)
  {
    if (null != getBoost ().get (id))
    {
      return (int) (getBoost ().get (id) * level);
    }
    else
    {
      return 0;
    }
  }
  
  public Effect[] getEffects ()
  {
    return this.effects;
  }
  
  @Override
  public boolean equals (Object other)
  {
    boolean equal = true;
    if (other instanceof StatModifier)
    {
      StatModifier otherModifier = (StatModifier) other;
      equal &= this.damageLower == otherModifier.damageLower;
      equal &= this.damageUpper == otherModifier.damageUpper;
      equal &= Arrays.equals (this.effects, otherModifier.effects);
      equal &= this.boost.size () == otherModifier.boost.size ();
      if (equal)
      {
        for (Entry<String, Float> thisBoost : this.boost.entrySet ())
        {
          equal &= thisBoost.getValue ().equals (otherModifier.boost.get 
                  (thisBoost.getKey ()));
        }
      }
    }
    else
    {
      equal = false;
    }
    return equal;
  }

  @Override
  public int hashCode ()
  {
    int hash = 3;
    hash = 67 * hash + Float.floatToIntBits (this.damageLower);
    hash = 67 * hash + Float.floatToIntBits (this.damageUpper);
    hash = 67 * hash + (this.boost != null ? this.boost.hashCode () : 0);
    hash = 67 * hash + Arrays.deepHashCode (this.effects);
    return hash;
  }
}
