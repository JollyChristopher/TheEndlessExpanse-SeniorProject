package halemaster.ee.world.entity;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import halemaster.ee.Game;
import halemaster.ee.Overlay;
import halemaster.ee.Sprite;
import halemaster.ee.ai.AI;
import halemaster.ee.ai.AIManager;
import halemaster.ee.ai.AutoAttackAI;
import halemaster.ee.item.Enchant;
import halemaster.ee.item.Inventory;
import halemaster.ee.item.Inventory.Equipment;
import halemaster.ee.item.Item;
import halemaster.ee.item.ItemSet;
import halemaster.ee.item.Reward;
import halemaster.ee.localization.Localizer;
import halemaster.ee.world.Area;
import halemaster.ee.world.micro.AreaGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @name Entity
 * 
 * @version 0.0.0
 * 
 * @date Feb 11, 2014
 */
public class Entity 
{
  public static final int ENTITY_LAYER = 20;
  public static final String CHANNEL_LEFT = "left";
  public static final String CHANNEL_RIGHT = "right";
  public static final int RESPAWN_TIME = 60;
  public static final int BASE_ACT = 1;
  public static final float ACT_MODIFIER = 0.01f;
  public static final float WORTH_PERCENT = 0.25f;
  public static final int CRIT_MODIFIER = 2;
  public static final int XP_MODIFIER = Statistic.XP.getMaximum () / 20;
  public static final int MELEE_RANGE = 3;
  public static final int INV_WIDTH = 5;
  public static final int INV_HEIGHT = 4;
  public static final int ACCURACY_UPPER = 10;
  public static final int ACCURACY_LOWER = -5;
  public static final UseAnimation MISS = new UseAnimation (1f, 0, 
          UseAnimation.ON, "Sounds/effects/miss.wav",
          Sprite.DEFAULT_BOX, Sprite.DEFAULT_BOX, 
          new String[] {"Textures/effects/miss/miss.png"});
  
  private transient Sprite sprite;
  private transient EntityHolder holder;
  private transient Map<String, AI> ais = new HashMap<String, AI>();
  private transient boolean isDead = false;
  private transient float deadTime;
  private transient ConcurrentLinkedQueue<Action> actions =
          new ConcurrentLinkedQueue<Action> ();
  private transient List<KillListener> listeners = new ArrayList<KillListener>();
  private transient float sinceLast = 0;
  private transient Map<Entity, Integer> hitBy = new HashMap<Entity, Integer>();
  private Map<String, String[]> images;
  private String currentAnimation = "";
  private float speed;
  private String name;
  private int x;
  private int y;
  private Area location;
  private String type;
  private Map<String, String> data = new HashMap<String, String>();
  private List<String> loadedAI;
  private int level = 1;
  private int xp = 0;
  private int statPoints = 0;
  private int power = 1;
  private int agility = 1;
  private int intellect = 1;
  private int currentHp = 0;
  private Map<String, Float> cooldown = new HashMap<String, Float> ();
  private Map<Effect, Float> effectTimes = new HashMap<Effect, Float> ();
  private String defaultAttack = Statistic.POWER.name ();
  private Inventory inventory = new Inventory (INV_WIDTH, INV_HEIGHT);
  private UseAnimation defaultAttackAnimation = new UseAnimation (2f, 0f, 
          UseAnimation.ON, "Sounds/effects/punch.wav",
          Sprite.DEFAULT_BOX, Sprite.DEFAULT_BOX, 
          new String[] {"Textures/effects/strike/strike_1.png", 
            "Textures/effects/strike/strike_2.png"});
  
  /**
   * Initialize the Entity with sprite info
   * @param game game to initialize with
   */
  public void initialize (Game game, EntityHolder holder)
  {
    this.sprite = new Sprite (this.name + this.location + this.x + this.y, 
            game.getAssetManager (), this.speed, ENTITY_LAYER);
    for (Entry<String, String[]> image : this.images.entrySet ())
    {
      this.sprite.addAnimation (image.getKey (), image.getValue ());
    }
    setAnimation (this.currentAnimation);
    this.holder = holder;
    if (null != this.loadedAI)
    {
      for (String ai : loadedAI)
      {
        addAI (ai);
      }
    }
    move (this.location.getX () * AreaGenerator.AREA_SIZE + this.x, 
            this.location.getY () * AreaGenerator.AREA_SIZE + this.y);
    game.attachSprite (this.sprite, Area.ANYWHERE);
  }
  
  /**
   * unload the entity in the game screen, effectively removing it from play.
   * @param game game to unload with
   */
  public void unload (Game game)
  {
    game.detachSprite (this.sprite, Area.ANYWHERE);
  }
  
  /**
   * update the entity
   * @param tpf delta from previous update
   */
  public void update (float tpf)
  {
    float actionSpeed = computeActionSpeed ();
    if (!this.isDead)
    {
      this.inventory.update (tpf);
      if (this.sprite != null)
      {
        this.sprite.update (tpf);
      }
      
      this.sinceLast += tpf;
      while (!this.actions.isEmpty () && this.sinceLast > actionSpeed)
      {
        Action action = this.actions.poll ();
        action.act (this);
        this.sinceLast -= actionSpeed;
      }
      
      List<Effect> remove = new ArrayList<Effect>();
      for (Effect effect : this.effectTimes.keySet ())
      {
        if (effect.update (this, tpf))
        {
          remove.add (effect);
        }
      }
      
      for (Effect effect : remove)
      {
        this.effectTimes.remove (effect);
      }
    }
    else
    {
      this.deadTime += tpf;
      if (this.deadTime >= RESPAWN_TIME)
      {
        respawn ();
      }
    }
    
    List<String> cooled = new ArrayList<String> ();
    for (Iterator<Entry<String, Float>> it = 
            this.cooldown.entrySet ().iterator (); it.hasNext ();)
    {
      Entry<String, Float> time = it.next ();
      this.cooldown.put (time.getKey (), time.getValue () - tpf);
      if (time.getValue () - tpf <= 0)
      {
        cooled.add (time.getKey ());
      }
    }
    
    for (String cool : cooled)
    {
      this.cooldown.remove (cool);
    }
  }

  public Sprite getSprite ()
  {
    return sprite;
  }

  public Map<String, String[]> getImages ()
  {
    return images;
  }

  public void setImages (Map<String, String[]> images)
  {
    this.images = images;
  }

  public float getSpeed ()
  {
    return speed;
  }

  public void setSpeed (float speed)
  {
    this.speed = speed;
  }

  public EntityHolder getHolder ()
  {
    return holder;
  }

  public String getName ()
  {
    return this.name;
  }
  
  public String getLocalizedName ()
  {
    return Localizer.getString (this.name);
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public int getX ()
  {
    return x;
  }

  public void setX (int x)
  {
    this.x = x;
  }

  public int getY ()
  {
    return y;
  }

  public void setY (int y)
  {
    this.y = y;
  }

  public Area getLocation ()
  {
    return location;
  }

  public void setLocation (Area location)
  {
    this.location = location;
  }
  
  public Area getExactLocation ()
  {
    return new Area (this.location.getX () * AreaGenerator.AREA_SIZE + this.x,
            this.location.getY () * AreaGenerator.AREA_SIZE + this.y);
  }

  public String getType ()
  {
    return type;
  }

  public void setType (String type)
  {
    this.type = type;
  }
  
  public void setAnimation (String anim)
  {
    if (null != this.sprite)
    {
      this.sprite.setAnimation (anim);
    }
    this.currentAnimation = anim;
  }
  
  public boolean isIdle ()
  {
    return this.actions.isEmpty ();
  }
  
  public boolean isDead ()
  {
    return this.isDead;
  }
  
  public Map<Entity, Integer> getHitBy ()
  {
    return this.hitBy;
  }
  
  /**
   * Add an attack action against the given entity
   * @param entity entity to attack
   */
  public void attack (Entity entity)
  {
    this.actions.add (new Action (entity, null));
  }
  
  /**
   * Use the item on the given entity
   * @param item item to use
   * @param entity 
   */
  public void use (ItemSet item, Entity entity)
  {
    this.actions.add (new Action (entity, item));
  }
  
  /**
   * Get the action speed of the Entity
   * @return the action speed of the entity
   */
  public float computeActionSpeed ()
  {
    return Math.max (BASE_ACT - ACT_MODIFIER *
            this.getStat (Statistic.HASTE), ACT_MODIFIER);
  }
  
  public String getAttackType ()
  {
    String attType = this.defaultAttack;
    if (this.inventory.getEquip (Equipment.MAIN_HAND) != null)
    {
      attType = this.inventory.getEquip (Equipment.MAIN_HAND).getItem ()
              .getWeaponType ();
    }
    return attType;
  }
  
  public int getDamageLower ()
  {
    int value = 0;
    
    if (Statistic.POWER.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.STRIKE);
    }
    else if (Statistic.AGILITY.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.AIM);
    }
    else if (Statistic.INTELLECT.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.MAGIK);
    }
    
    if (null != this.inventory.getEquip (Equipment.MAIN_HAND))
    {
      value += this.inventory.getEquip (Equipment.MAIN_HAND).getItem ()
              .getOnEquip ().getDamageLower (this.inventory
              .getEquip (Equipment.MAIN_HAND).getItem ().getLevel ());
    }
    if (null != this.inventory.getEquip (Equipment.OFF_HAND))
    {
      value += this.inventory.getEquip (Equipment.OFF_HAND).getItem ()
              .getOnEquip ().getDamageLower (this.inventory
              .getEquip (Equipment.OFF_HAND).getItem ().getLevel ());
    }
    
    return value;
  }
  
  public int getDamageUpper ()
  {
    int value = 0;
    if (Statistic.POWER.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.STRIKE);
    }
    else if (Statistic.AGILITY.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.AIM);
    }
    else if (Statistic.INTELLECT.name ().equals (getAttackType ()))
    {
      value = getStat (Statistic.MAGIK);
    }
    
    if (null != this.inventory.getEquip (Equipment.MAIN_HAND))
    {
      value += this.inventory.getEquip (Equipment.MAIN_HAND).getItem ()
              .getOnEquip ().getDamageUpper (this.inventory
              .getEquip (Equipment.MAIN_HAND).getItem ().getLevel ());
    }
    if (null != this.inventory.getEquip (Equipment.OFF_HAND))
    {
      value += this.inventory.getEquip (Equipment.OFF_HAND).getItem ()
              .getOnEquip ().getDamageUpper (this.inventory
              .getEquip (Equipment.OFF_HAND).getItem ().getLevel ());
    }
    
    return value;
  }
  
  public void setDefaultAttack (Statistic stat)
  {
    this.defaultAttack = stat.name ();
  }
  
  /**
   * Set the given data to the entity.
   * @param key key of the data
   * @param data data to add
   */
  public void setData (String key, String data)
  {
    this.data.put (key, data);
  }
  
  /**
   * Get the data with the given key
   * @param key key of the data
   * @return data found at that key
   */
  public String getData (String key)
  {
    return this.data.get (key);
  }
  
  /**
   * move the Entity to the given x, y coordinates
   * 
   * @param x x to move to
   * @param y y to move to
   */
  public void move (float x, float y)
  {
    moveAbsolute (x * AreaGenerator.TILE_WIDTH, y *
            AreaGenerator.TILE_HEIGHT);
  }
  
  /**
   * move the Entity to the given x, y coordinates
   * 
   * @param x x to move to
   * @param y y to move to
   */
  public void moveAbsolute (float x, float y)
  {
    float locX = getAbsoluteLocation ().x;
    float locY = getAbsoluteLocation ().y;
    Area nextLocation;
    
    this.sprite.move (x, y);
    x += locX;
    y += locY;
    
    nextLocation = new Area ((int)((x + AreaGenerator.TILE_WIDTH / 2) / 
            (AreaGenerator.TILE_WIDTH * AreaGenerator.AREA_SIZE)), 
            (int)((y + AreaGenerator.TILE_HEIGHT / 2) / 
            (AreaGenerator.TILE_HEIGHT * AreaGenerator.AREA_SIZE)));
    if (!nextLocation.equals (this.location))
    {
      this.holder.moveEntity (this, nextLocation);
      this.location = nextLocation;
    }
    this.x = (int)((x + AreaGenerator.TILE_WIDTH / 2) / 
            AreaGenerator.TILE_WIDTH) % (AreaGenerator.AREA_SIZE);
    this.y = (int)((y + AreaGenerator.TILE_HEIGHT / 2) / 
            AreaGenerator.TILE_HEIGHT) % (AreaGenerator.AREA_SIZE);
  }
  
  /**
   * Snap the entity to the grid
   */
  public void snapToGrid (Area exactLoc)
  {
    Vector2f mine = getAbsoluteLocation ();
    
    moveAbsolute (exactLoc.getX () * AreaGenerator.TILE_WIDTH - mine.x,
            exactLoc.getY () * AreaGenerator.TILE_HEIGHT - mine.y);
  }
  
  /**
   * Get the sprite location of the entity in game
   * @return x, y coordinates of the entity in game
   */
  public Vector2f getAbsoluteLocation ()
  {
    Vector2f loc = new Vector2f();
    Vector3f spriteLoc = this.sprite.getImage ().getLocalTranslation ();
    
    loc.x = spriteLoc.x;
    loc.y = spriteLoc.z;
    
    return loc;
  }
  
  /**
   * Add an id with the given string to the entity
   * @param id id of ai to add
   */
  public void addAI (String id)
  {
    AI add = AIManager.getAI (id);
    
    if (null != add && null != this.sprite && null == this.ais.put (id, add))
    {
      add.attachEntity (this);
    }
    
    if (this.loadedAI == null)
    {
      this.loadedAI = new ArrayList<String>();
    }
    
    if (!this.loadedAI.contains (id))
    {
      this.loadedAI.add (id);
    }
  }
  
  /**
   * Remove the ai with the given id
   * @param id id to remove
   * @return the AI
   */
  public AI removeAI (String id)
  {
    AI remove = this.ais.remove (id);
    
    if (null != remove && null != this.sprite)
    {
      remove.detachEntity ();
    }
    
    if (null != this.loadedAI && !this.loadedAI.contains (id))
    {
      this.loadedAI.remove (id);
    }
    
    return remove;
  }
  
  /**
   * Get the AI with the given id
   * @param id id of the AI
   * @return the AI found with the id
   */
  public AI getAI (String id)
  {
    return this.ais.get (id);
  }
  
  /**
   * Cleanup AIs for saving purposes
   */
  public void prepForSaving ()
  {
    for (AI ai : this.ais.values ())
    {
      ai.detachEntity ();
    }
  }
  
  public String getBasicImage ()
  {
    return this.images.get (CHANNEL_LEFT)[0];
  }
  
  /**
   * Get the fully derived statistic and return its value
   * @param stat stat to get
   * @return the fully derived statistic
   */
  public int getStat (Statistic stat)
  {
    int[] derived;
    int value;
    if (stat.getDerived ().length > 0)
    {
      derived = new int[stat.getDerived ().length];
      for (int i = 0; i < derived.length; i++)
      {
        derived[i] = getStat (stat.getDerived ()[i]);
      }
    }
    else
    {
      derived = new int[1];
      switch (stat)
      {
        case LEVEL:
          derived[0] = this.level;
          break;
        case XP:
          derived[0] = this.xp;
          break;
        case POWER:
          derived[0] = this.power;
          break;
        case AGILITY:
          derived[0] = this.agility;
          break;
        case INTELLECT:
          derived[0] = this.intellect;
          break;
        default:
          derived[0] = 0;
          break;
      }
    }
    
    value = stat.derive (derived);
    
    for (Equipment equip : Equipment.values ())
    {
      ItemSet equipment = this.inventory.getEquip (equip);
      if (null != equipment)
      {
        value += equipment.getItem ().getStatValueOnEquip (stat);
      }
    }
    
    for (Effect effect : this.effectTimes.keySet ())
    {
      value += effect.getStat (stat);
    }
    
    if (Statistic.NONE != stat.getMaximum () && value > stat.getMaximum ())
    {
      value = stat.getMaximum ();
    }
    
    return value;
  }
  
  public int getCurrentHp ()
  {
    return this.currentHp;
  }
  
  public int getStatPoints ()
  {
    return this.statPoints;
  }
  
  public void setCurrentHp (int hp)
  {
    this.currentHp = hp;
  }

  public void setLevel (int level)
  {
    this.level = level;
  }

  public void setXp (int xp)
  {
    this.xp = xp;
  }

  public void setStatPoints (int statPoints)
  {
    this.statPoints = statPoints;
  }

  public void setPower (int power)
  {
    this.power = power;
  }

  public void setAgility (int agility)
  {
    this.agility = agility;
  }

  public void setIntellect (int intellect)
  {
    this.intellect = intellect;
  }

  public int getLevel ()
  {
    return level;
  }

  public int getXp ()
  {
    return xp;
  }

  public int getPower ()
  {
    return power;
  }

  public int getAgility ()
  {
    return agility;
  }

  public int getIntellect ()
  {
    return intellect;
  }
  
  public void setDefaultAttackAnimation (UseAnimation animation)
  {
    this.defaultAttackAnimation = animation;
  }
  
  public UseAnimation getAttackAnimation ()
  {
    UseAnimation attackAnim = this.defaultAttackAnimation;
    
    if (null != this.inventory.getEquip (Equipment.MAIN_HAND))
    {
      attackAnim = this.inventory.getEquip (Equipment.MAIN_HAND).getItem ()
              .getAttackAnimation ();
    }
    
    return attackAnim;
  }
  
  /**
   * Add xp value to current. If over max, level, granting appropriate stat
   * points
   * @param xp amount of xp gain
   */
  public void addXp (int xp)
  {
    this.xp += xp;
    while (this.xp >= Statistic.XP.getMaximum ())
    {
      this.xp -= Statistic.XP.getMaximum ();
      this.level++;
      this.statPoints++;
    }
  }
  
  /**
   * Spend up to 1 stat point on the given primary stat. Secondary stats are
   * ignored
   * @param stat the stat to increase
   * @return whether we successfully spent the point
   */
  public boolean spendStat (Statistic stat)
  {
    boolean spent = true;
    
    if (this.statPoints > 0)
    {
      switch (stat)
      {
        case POWER:
          this.power++;
          break;
        case AGILITY:
          this.agility++;
          break;
        case INTELLECT:
          this.intellect++;
          break;
        default:
          spent = false;
          break;
      }
    }
    else
    {
      spent = false;
    }
    
    if (spent)
    {
      this.statPoints--;
    }
    
    return spent;
  }
  
  /**
   * Heal the entity
   * @param amount amount to heal by
   */
  public void heal (int amount)
  {
    int maxHealth = getStat (Statistic.HEALTH);
    damage (-amount, null);
    if (this.currentHp > maxHealth)
    {
      this.currentHp = maxHealth;
    }
  }
  
  /**
   * Damage the entity. This does not check for over max health
   * @param amount amount to damage by
   * @param source the source entity of the damage, or null if there isn't one
   */
  public void damage (int amount, Entity source)
  {
    Integer previous;
    
    if (null != source && source != this)
    {
      previous = this.hitBy.get (source);
      if (null == previous)
      {
        if (this.hitBy.isEmpty ())
        {
          previous = this.getStat (Statistic.HEALTH);
        }
        else
        {
          previous = 0;
        }
      }
      
      previous += amount;
      this.hitBy.put (source, previous);
    }
    
    this.currentHp -= amount;
    if (this.currentHp <= 0)
    {
      kill ();
    }
  }
  
  public void giveReward (Reward reward)
  {
    giveReward (reward, true);
  }
  
  /**
   * give the reward to the entity
   * @param reward reward to give
   */
  public void giveReward (Reward reward, boolean gainXp)
  {
    if (gainXp)
    {
      int xpGain = (int) ((float) XP_MODIFIER * (float) reward.getLevel () / 
              (float) this.getStat (Statistic.LEVEL));
      if (xpGain > 0)
      {
        addXp (xpGain);
      }
    }
    
    this.inventory.addMoney (reward.getMoney ());
    if (null != reward.getItems ())
    {
      for (ItemSet item : reward.getItems ())
      {
        if (!this.inventory.addItem (item) && null != item)
        {
          this.holder.getHolder ().addGroundItemNearEntity (item, this);
        }
      }
    }
  }
  
  /**
   * Kill the entity. The entity stops appearing and does not endure AI until
   * respawn. The entity is NOT removed.
   */
  public void kill ()
  {
    Reward reward;
    int worthDamage = (int) (this.getStat (Statistic.HEALTH) * WORTH_PERCENT);
    
    if (!this.isDead)
    {
      for (Entry<Entity, Integer> combatant : this.hitBy.entrySet ())
      {
        if (combatant.getValue () >= worthDamage)
        {
          reward = Reward.getReward (this.getStat (Statistic.LEVEL));
          combatant.getKey ().giveReward (reward);
          combatant.getKey ().kills (this);
        }
        combatant.getKey ().getHitBy ().remove (this);
      }

      this.hitBy.clear ();
      this.actions.clear ();
      ((AutoAttackAI) this.getAI (Game.AUTO_ATTACK_AI)).setTarget (null);
    }
    
    this.deadTime = 0;
    setAliveState (false);
  }
  
  /**
   * Respawn the entity. This will cause AI to resume and the sprite to reappear
   */
  public void respawn ()
  {
    setCurrentHp (getStat (Statistic.HEALTH));
    this.hitBy.clear ();
    this.effectTimes.clear ();
    setAliveState (true);
  }
  
  public void setDeadTime (float time)
  {
    this.deadTime = time;
  }
  
  /**
   * Set the alive state of the entity.
   * @param state state to set alive to.
   */
  private void setAliveState (boolean state)
  {
    this.isDead = !state;
    if (state)
    {
      this.sprite.getImage ().setCullHint (Spatial.CullHint.Dynamic);
    }
    else
    {
      this.sprite.getImage ().setCullHint (Spatial.CullHint.Always);
    }
    for (AI ai : this.ais.values ())
    {
      ai.setEnabled (state);
    }
  }
  
  public void attackEntity (Entity target)
  {
    // TODO: add in full item attacking
    int damageLower, damageUpper, damage;
    int armor;
    int acc;
    int crit;
    Random random = new Random ();
    UseAnimation anim = getAttackAnimation ();
    List<String> animList;
    String[] animation;
    Vector2f loc;
    Entity dest;
    
    damageLower = getDamageLower ();
    damageUpper = getDamageUpper ();
    armor = target.getStat (Statistic.ARMOR);
    acc = getStat (Statistic.ACCURACY);
    crit = getStat (Statistic.CRITICAL);

    damage = random.nextInt (damageUpper - damageLower + 1) + damageLower;
    if (crit > random.nextInt (100))
    {
      damage *= Entity.CRIT_MODIFIER;
    }

    if (acc + random.nextInt (ACCURACY_UPPER - ACCURACY_LOWER + 1) + 
            ACCURACY_LOWER >= target.getStat (Statistic.LEVEL))
    {
      damage = Math.max (damage - armor, 1);
      target.damage (damage, this);
      
      if (null != this.inventory.getEquip (Equipment.MAIN_HAND))
      {
        for (Effect eff : this.inventory.getEquip (Equipment.MAIN_HAND)
                .getItem ().getOnEquip ().getEffects ())
        {
          Effect effect = eff.cloneEffect ();
          effect.setLevel (this.level);
          effect.setSource (this);
          target.addEffect (effect);
        }
        for (String enchant : this.inventory.getEquip (Equipment.MAIN_HAND)
                .getItem ().getEnchants ())
        {
          Enchant ench = Enchant.getEnchant (enchant);
          if (ench.getAttackTarget ().equals (Item.SELF))
          {
            animList = ench.getAttackAnimation ().getAnimations ().get 
                    (random.nextInt (ench.getAttackAnimation ()
                    .getAnimations ().size ()));
            animation = new String[animList.size ()];
            loc = getAbsoluteLocation ();

            for (int i = 0; i < animation.length; i++)
            {
              animation[i] = animList.get (i);
            }
            Overlay animOverlay = new Overlay (getName () + "ATTACK" + 
                    target.getName (), ench.getAttackAnimation ().getFps (), 
                    ench.getAttackAnimation ().getWidth (), 
                    ench.getAttackAnimation ().getHeight (), animation, 
                    getHolder ().getHolder ().getPlayerState ().getGame (), 
                    ench.getAttackAnimation ().getSpeed (), 
                    loc, null, ench.getAttackAnimation ().getSound ());
            animOverlay.start ();
            
            heal (random.nextInt (ench.getOnEquip ().getDamageUpper () -
                    ench.getOnEquip ().getDamageLower () + 1) + 
                    ench.getOnEquip ().getDamageLower ());
            for (Effect eff : ench.getOnEquip ().getEffects ())
            {
              Effect effect = eff.cloneEffect ();
              effect.setLevel (this.level);
              effect.setSource (this);
              addEffect (effect);
            }
          }
          else if (ench.getAttackTarget ().equals (Item.TARGET))
          {
            animList = ench.getAttackAnimation ().getAnimations ().get 
                    (random.nextInt (ench.getAttackAnimation ()
                    .getAnimations ().size ()));
            animation = new String[animList.size ()];
            loc = target.getAbsoluteLocation ();
            dest = null;

            if (anim.getType ().equals (UseAnimation.TOWARDS))
            {
              loc = getAbsoluteLocation ();
              dest = target;
            }

            for (int i = 0; i < animation.length; i++)
            {
              animation[i] = animList.get (i);
            }
            Overlay animOverlay = new Overlay (getName () + "ATTACK" + 
                    target.getName (), ench.getAttackAnimation ().getFps (), 
                    ench.getAttackAnimation ().getWidth (), 
                    ench.getAttackAnimation ().getHeight (), animation, 
                    getHolder ().getHolder ().getPlayerState ().getGame (), 
                    ench.getAttackAnimation ().getSpeed (), 
                    loc, dest, ench.getAttackAnimation ().getSound ());
            animOverlay.start ();
            
            target.damage (random.nextInt (ench.getOnEquip ().getDamageUpper ()
                    - ench.getOnEquip ().getDamageLower () + 1) + 
                    ench.getOnEquip ().getDamageLower (), this);
            for (Effect eff : ench.getOnEquip ().getEffects ())
            {
              Effect effect = eff.cloneEffect ();
              effect.setLevel (this.level);
              effect.setSource (this);
              target.addEffect (effect);
            }
          }
        }
      }
    }
    else
    {
      anim = MISS;
    }

    animList = anim.getAnimations ().get 
            (random.nextInt (anim.getAnimations ().size ()));
    animation = new String[animList.size ()];
    loc = target.getAbsoluteLocation ();
    dest = null;

    if (anim.getType ().equals (UseAnimation.TOWARDS))
    {
      loc = getAbsoluteLocation ();
      dest = target;
    }

    for (int i = 0; i < animation.length; i++)
    {
      animation[i] = animList.get (i);
    }
    Overlay animOverlay = new Overlay (getName () + "ATTACK" + 
            target.getName (), anim.getFps (), anim.getWidth (), 
            anim.getHeight (), animation, getHolder ().getHolder ()
            .getPlayerState ().getGame (), anim.getSpeed (), 
            loc, dest, anim.getSound ());
    animOverlay.start ();
  }
  
  /**
   * Add a new kill listener to this entity
   * @param listener lisener to add
   */
  public void addKillListener (KillListener listener)
  {
    this.listeners.add (listener);
  }
  
  /**
   * Tell this Entity that it has killed the given entity
   * @param killed entity killed
   */
  public void kills (Entity killed)
  {
    for (KillListener listener : this.listeners)
    {
      listener.onKill (this, killed);
    }
  }
  
  public boolean onCooldown (String id)
  {
    return this.cooldown.containsKey (id);
  }
  
  public int getRemainingCooldown (String id)
  {
    if (null != this.cooldown.get (id))
    {
      return (int) Math.ceil ((float) (this.cooldown.get (id)));
    }
    else
    {
      return 0;
    }
  }
  
  public void putCooldown (String id, int time)
  {
    this.cooldown.put (id, (float) time);
  }
  
  public Inventory getInventory ()
  {
    return this.inventory;
  }
  
  public void setInventory (Inventory inventory)
  {
    this.inventory = inventory;
  }
  
  public Float getEffectTime (Effect effect)
  {
    return this.effectTimes.get (effect);
  }
  
  public void setEffectTime (Effect effect, float time)
  {
    this.effectTimes.put (effect, time);
  }
  
  public void addEffect (Effect effect)
  {
    boolean found = false;
    for (Effect eff : this.effectTimes.keySet ())
    {
      if (eff.equals (effect))
      {
        found = true;
        eff.combine (effect, this);
      }
    }
    
    if (!found)
    {
      this.effectTimes.put (effect, 0f);
    }
  }
  
  public Effect[] getEffects ()
  {
    Effect[] effects = new Effect[this.effectTimes.size ()];
    int index = 0;
    
    for (Effect eff : this.effectTimes.keySet ())
    {
      effects[index] = eff;
      index++;
    }
    
    return effects;
  }
}
