package halemaster.ee.world.entity;

import java.util.Map;

/**
 * @name Effect
 * 
 * @version 0.0.0
 * 
 * @date Apr 1, 2014
 */
public class Effect 
{
  private String name;
  private String image;
  private int lasts;
  private int damage;
  private int stack;
  private int currentStack = 1;
  private Map<String, Float> boost;
  private int level;
  private transient Entity source = null;

  public String getName ()
  {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public String getImage ()
  {
    return image;
  }

  public void setImage (String image)
  {
    this.image = image;
  }

  public int getLasts ()
  {
    return lasts;
  }

  public void setLasts (int lasts)
  {
    this.lasts = lasts;
  }

  public int getDamage ()
  {
    return (int) (this.damage * this.level) * this.currentStack;
  }

  public void setDamage (int damage)
  {
    this.damage = damage;
  }

  public int getStack ()
  {
    return stack;
  }

  public void setStack (int stack)
  {
    this.stack = stack;
  }

  public int getCurrentStack ()
  {
    return currentStack;
  }

  public void setCurrentStack (int currentStack)
  {
    this.currentStack = currentStack;
  }

  public Map<String, Float> getBoost ()
  {
    return boost;
  }

  public void setBoost (Map<String, Float> boost)
  {
    this.boost = boost;
  }

  public int getLevel ()
  {
    return level;
  }

  public void setLevel (int level)
  {
    this.level = level;
  }
  
  public Entity getSource ()
  {
    return this.source;
  }
  
  public void setSource (Entity source)
  {
    this.source = source;
  }
  
  public boolean combine (Effect effect, Entity entity)
  {
    boolean worked = false;
    
    if (equals (effect))
    {
      if (effect.lasts > this.lasts - entity.getEffectTime (this))
      {
        this.lasts = effect.lasts;
        entity.setEffectTime (this, 0);
        worked = true;
      }
      if (this.currentStack < this.stack)
      {
        this.currentStack = Math.max (this.stack, this.currentStack + 
                effect.currentStack);
        worked = true;
      }
    }
    
    return worked;
  }
  
  public boolean update (Entity owner, float tpf)
  {
    boolean done = false;
    float current = owner.getEffectTime (this);
    
    for (int i = (int) current; i < (int) (current + tpf); i++)
    {
      // deal tick damage or healing
      if (this.damage < 0)
      {
        owner.heal (-getDamage ());
      }
      else
      {
        owner.damage (getDamage (), this.source);
      }
    }
    
    current += tpf;
    owner.setEffectTime (this, current);
    
    if (current >= this.lasts)
    {
      done = true;
    }
    
    return done;
  }
  
  public int getStat (Statistic stat)
  {
    if (null != this.boost.get (stat.name ()))
    {
      return (int) (this.boost.get (stat.name ()) * this.level) * 
              this.currentStack;
    }
    else
    {
      return 0;
    }
  }
  
  public Effect cloneEffect ()
  {
    Effect effect = new Effect ();
    
    effect.boost = this.boost;
    effect.currentStack = this.currentStack;
    effect.damage = this.damage;
    effect.image = this.image;
    effect.lasts = this.lasts;
    effect.level = this.level;
    effect.name = this.name;
    effect.source = this.source;
    effect.stack = this.stack;
    
    return effect;
  }
  
  @Override
  public boolean equals (Object other)
  {
    boolean equals;
    
    if (other instanceof Effect)
    {
      Effect effect = (Effect) other;
      equals = this.name.equals (effect.name) && this.image.equals 
              (effect.image) && this.level == effect.level && 
              this.stack == effect.stack && this.damage == effect.damage &&
              this.boost.equals (effect.boost);
    }
    else
    {
      equals = false;
    }
    
    return equals;
  }

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + (this.name != null ? this.name.hashCode () : 0);
    hash = 53 * hash + (this.image != null ? this.image.hashCode () : 0);
    hash = 53 * hash + this.damage;
    hash = 53 * hash + this.stack;
    hash = 53 * hash + (this.boost != null ? this.boost.hashCode () : 0);
    hash = 53 * hash + this.level;
    return hash;
  }
}
