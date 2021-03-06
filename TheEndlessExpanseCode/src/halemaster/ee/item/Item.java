package halemaster.ee.item;

import com.jme3.math.Vector2f;
import halemaster.ee.Overlay;
import halemaster.ee.localization.Localizer;
import halemaster.ee.world.entity.Effect;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.Statistic;
import halemaster.ee.world.entity.UseAnimation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @name Item
 * 
 * @version 0.0.0
 * 
 * @date Mar 23, 2014
 */
public class Item 
{
  public static final String SELF = "SELF";
  public static final String TARGET = "TARGET";
  public static final String ENCHANT_COOL = "";
  public static final int MAX_ENCHANT = 5;
  public static final int COST_MODIFIER = 3;
  public static final int ULTRA_COMMON = 5000;
  public static final int RARITY_COST = 1000;
  public static final int ENCHANT_COST = 10;
  public static final int ENCHANT_LEVELS = 5;
  
  private String name = null;
  private int level;
  private String image;
  private String weaponType;
  private String[] enchants;
  private String target;
  private int waitTime;
  private boolean useUp;
  private String[] equipLocations;
  private String cooldown;
  private StatModifier onUse;
  private StatModifier onEquip;
  private UseAnimation useAnimation;
  private UseAnimation attackAnimation;
  private int cost;
  
  public Item ()
  {
    // does nothing. Default constructor
  }
  
  public Item (ItemTemplate template, int level, Random random)
  {
    List<Enchant> enchantSet = new ArrayList<Enchant> ();
    float enchantRarityTotal = 0;
    float enchantRarityUse;
    this.level = ((level / template.getLevelGap ()) * template.getLevelGap ());
    if (0 == this.level)
    {
      this.level = template.getAvailableAt ();
    }
    for (String ench : template.getEnchants ())
    {
      Enchant enchant = Enchant.getEnchant (ench);
      if (enchant.getAvailableAt () <= this.level)
      {
        enchantSet.add (enchant);
        enchantRarityTotal += enchant.getRarity ();
      }
    }
    enchantRarityUse = enchantRarityTotal;
    this.cost = (ULTRA_COMMON - template.getRarity ()) / RARITY_COST + 1;
    this.attackAnimation = template.getAttackAnimation ();
    this.enchants = new String[random.nextInt (1 + Math.min (this.level / 
            ENCHANT_LEVELS + 1,enchantSet.size ()))];
    for (int i = 0; i < this.enchants.length; i++)
    {
      Enchant ench = null;
      float rarity = random.nextInt ((int) enchantRarityUse + 1);
      int index = 0;
      while (null == ench && index < enchantSet.size ())
      {
        if (enchantSet.get (index).getRarity () >= rarity)
        {
          ench = enchantSet.get (index);
          enchantSet.remove (index);
          enchantRarityUse -= ench.getRarity ();
        }
        else
        {
          rarity -= enchantSet.get (index).getRarity ();
        }
        index++;
      }
      this.enchants[i] = ench.getId ();
      this.cost += ENCHANT_COST * (enchantRarityTotal - ench.getRarity () + 1) / 
              enchantRarityTotal;
    }
    this.equipLocations = (template.getEquipLocations ());
    this.image = (template.getImage ());
    this.name = (template.getNames ()[random.nextInt (template.getNames ()
            .length)]);
    this.onEquip = (template.getOnEquip ());
    this.weaponType = (template.getWeaponType ());
    if (template.getOnUse ().length > 0)
    {
      UseCase use = template.getOnUse ()[random.nextInt (template.getOnUse 
                ().length)];
      this.cooldown = (use.getCooldown ());
      this.onUse = (use.getUse ());
      this.target = (use.getTarget ());
      this.useAnimation = (use.getAnimation ());
      this.useUp = (use.isUseUp ());
      this.waitTime = (use.getUseWait ());
    }
    else
    {
      this.cooldown = (Item.ENCHANT_COOL);
      this.onUse = (null);
      this.target = (null);
      this.useAnimation = (null);
      this.useUp = (false);
      this.waitTime = (0);
    }
    this.cost *= this.level;
    this.cost *= COST_MODIFIER;
  }
  
  public int getCost ()
  {
    return this.cost;
  }
  
  public void setCost (int cost)
  {
    this.cost = cost;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }
  
  public boolean getUsable ()
  {
    boolean usable = false;
    
    if (null != this.target && (this.target.equals (Item.SELF) || 
            this.target.equals (TARGET)))
    {
      usable = true;
    }
    
    return usable;
  }

  public int getLevel ()
  {
    return level;
  }

  public void setLevel (int level)
  {
    this.level = level;
  }

  public String getImage ()
  {
    return image;
  }

  public void setImage (String image)
  {
    this.image = image;
  }

  public String getWeaponType ()
  {
    return weaponType;
  }

  public void setWeaponType (String weaponType)
  {
    this.weaponType = weaponType;
  }

  public String[] getEnchants ()
  {
    return enchants;
  }

  public void setEnchants (String[] enchants)
  {
    this.enchants = enchants;
  }

  public String getTarget ()
  {
    return target;
  }

  public void setTarget (String target)
  {
    this.target = target;
  }

  public int getWaitTime ()
  {
    return waitTime;
  }

  public void setWaitTime (int waitTime)
  {
    this.waitTime = waitTime;
  }

  public boolean isUseUp ()
  {
    return useUp;
  }

  public void setUseUp (boolean useUp)
  {
    this.useUp = useUp;
  }

  public String[] getEquipLocations ()
  {
    return equipLocations;
  }

  public void setEquipLocations (String[] equipLocations)
  {
    this.equipLocations = equipLocations;
  }
  
  public boolean isEquippable (String location)
  {
    boolean equip = false;
    
    for (String equipLoc : this.equipLocations)
    {
      if (location.equals (equipLoc))
      {
        equip = true;
      }
    }
    
    return equip;
  }

  public StatModifier getOnUse ()
  {
    return onUse;
  }

  public void setOnUse (StatModifier onUse)
  {
    this.onUse = onUse;
  }

  public StatModifier getOnEquip ()
  {
    return onEquip;
  }

  public void setOnEquip (StatModifier onEquip)
  {
    this.onEquip = onEquip;
  }

  public String getCooldown ()
  {
    return cooldown;
  }

  public void setCooldown (String cooldown)
  {
    this.cooldown = cooldown;
  }

  public UseAnimation getUseAnimation ()
  {
    return useAnimation;
  }

  public void setUseAnimation (UseAnimation useAnimation)
  {
    this.useAnimation = useAnimation;
  }

  public UseAnimation getAttackAnimation ()
  {
    return attackAnimation;
  }

  public void setAttackAnimation (UseAnimation attackAnimation)
  {
    this.attackAnimation = attackAnimation;
  }
  
  public String getLocalized ()
  {
    return Localizer.getString (this.name);
  }
  
  /**
   * Get the statistic value of the stat given for this item as equipment
   * @param stat stat to get
   * @return value this equipment grants
   */
  public int getStatValueOnEquip (Statistic stat)
  {
    int statValue = this.onEquip.getBoost (stat.name (), this.level);
    Enchant enchant;
    
    for (String enchantId : this.enchants)
    {
      enchant = Enchant.getEnchant (enchantId);
      statValue += enchant.getOnEquip ().getBoost (stat.name (), this.level);
    }
    
    return statValue;
  }
  
  /**
   * Use the item on the given target
   * @param user the one who is using the item
   * @param target the target of the use.
   * @return whether the item was used successfully
   */
  public boolean useItem (Entity user, Entity target)
  {
    boolean used = false;
    Enchant enchant;
    
    if (this.target.equals (SELF))
    {
      used = use (this.cooldown, this.onUse, this.waitTime, this.useAnimation,
              user, user);
    }
    else if (this.target.equals (TARGET))
    {
      used = use (this.cooldown, this.onUse, this.waitTime, this.useAnimation,
              user, target);
    }
    
    if (used)
    {
      for (String enchantId : this.enchants)
      {
        enchant = Enchant.getEnchant (enchantId);
        if (enchant.getUseTarget ().equals (SELF))
        {
          use (ENCHANT_COOL, enchant.getOnUse (), 0, enchant.getUseAnimation (),
                  user, user);
        }
        else if (enchant.getUseTarget ().equals (TARGET))
        {
          use (ENCHANT_COOL, enchant.getOnUse (), 0, enchant.getUseAnimation (),
                  user, target);
        }
      }
    }
    
    return used;
  }
  
  /**
   * Use the action on the target
   * @param cooldown cooldown string to use
   * @param onUse stat modifier to use
   * @param waitTime how long to set cooldown
   * @param animation animation to display
   * @param user user of the item
   * @param target target of the use
   * @return whether the use was a success
   */
  private boolean use (String cooldown, StatModifier onUse, int waitTime, 
          UseAnimation animation, Entity user, Entity target)
  {
    boolean used = true;
    Random random = new Random ();
    int lowerDamage = (int) (onUse.getDamageLower (this.level)) + 
            user.getStat (Statistic.CRAFT);
    int upperDamage = (int) (onUse.getDamageUpper (this.level)) + 
            user.getStat (Statistic.CRAFT);
    
    if (!user.onCooldown (cooldown) && null != onUse && null != target)
    {
      // deal damage to target
      if (user != target)
      {
        target.damage (random.nextInt (upperDamage - lowerDamage + 1) + 
              lowerDamage, user);
      }
      else
      {
        target.heal (random.nextInt (upperDamage - lowerDamage + 1) + 
              lowerDamage);
      }
      
      // add effects to target
      for (Effect eff : onUse.getEffects ())
      {
        Effect effect = eff.cloneEffect ();
        effect.setLevel (this.level);
        effect.setSource (user);
        target.addEffect (effect);
      }
      
      // add boost to target
      for (Entry<String, Float> boost : onUse.getBoost ().entrySet ())
      {
        if (boost.getKey ().equals (Statistic.POWER.name ()))
        {
          target.setPower (target.getPower () + (int) (float) boost.getValue ());
        }
        else if (boost.getKey ().equals (Statistic.AGILITY.name ()))
        {
          target.setAgility (target.getAgility () + (int) (float) boost.getValue ());
        }
        else if (boost.getKey ().equals (Statistic.INTELLECT.name ()))
        {
          target.setIntellect (target.getIntellect () + (int) (float) boost.getValue ());
        }
      }
      
      // animation
      List<String> animList = animation.getAnimations ().get 
              (random.nextInt (animation.getAnimations ().size ()));
      String[] anim = new String[animList.size ()];
      Vector2f location = target.getAbsoluteLocation ();
      Entity dest = null;
      
      if (animation.getType ().equals (UseAnimation.TOWARDS))
      {
        location = user.getAbsoluteLocation ();
        dest = target;
      }
      
      for (int i = 0; i < anim.length; i++)
      {
        anim[i] = animList.get (i);
      }
      Overlay animOverlay = new Overlay (user.getName () + "ITEM" + 
              target.getName (), animation.getFps (), animation.getWidth (), 
              animation.getHeight (), anim, user.getHolder ().getHolder ()
              .getPlayerState ().getGame (), animation.getSpeed (), 
              location, dest, animation.getSound ());
      animOverlay.start ();
      
      // set up cooldown on user
      if (!ENCHANT_COOL.equals (cooldown))
      {
        user.putCooldown (cooldown, waitTime);
      }
    }
    else
    {
      used = false;
    }
    
    return used;
  }
  
  /**
   * Determine whether this and other are equal
   * @param other the other object
   * @return whether they are equal
   */
  @Override
  public boolean equals (Object other)
  {
    boolean equal = true;
    Item otherItem;
    
    if (other instanceof Item)
    {
      otherItem = (Item) other;
      equal &= this.name.equals (otherItem.name);
      equal &= this.level == otherItem.level;
      equal &= this.image.equals (otherItem.image);
      equal &= this.enchants.length == otherItem.enchants.length;
      equal &= (null == this.onUse && null == otherItem.onUse) || 
              (this.onUse != null && 
              otherItem.onUse != null && this.onUse.equals (otherItem.onUse));
      if (equal)
      {
        for (int i = 0; i < this.enchants.length; i++)
        {
          equal &= this.enchants[i].equals (otherItem.enchants[i]);
        }
      }
    }
    
    return equal;
  }

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 41 * hash + (this.name != null ? this.name.hashCode () : 0);
    hash = 41 * hash + this.level;
    hash = 41 * hash + (this.image != null ? this.image.hashCode () : 0);
    hash = 41 * hash + Arrays.deepHashCode (this.enchants);
    hash = 41 * hash + (this.onUse != null ? this.onUse.hashCode () : 0);
    return hash;
  }
}
