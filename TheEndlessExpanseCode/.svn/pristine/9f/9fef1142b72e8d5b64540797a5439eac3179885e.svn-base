package halemaster.ee.item;

import halemaster.ee.Game;
import halemaster.ee.ai.AutoAttackAI;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.Statistic;
import java.util.ArrayList;

/**
 * @name Inventory
 * 
 * @version 0.0.0
 * 
 * @date Mar 27, 2014
 */
public class Inventory 
{
  public enum Equipment
  {
    MAIN_HAND,
    OFF_HAND,
    BODY,
    HELM,
    BELT,
    GLOVES,
    BOOTS,
    LEGS,
    ACCESSORY;
  }
  
  private transient ArrayList<InventoryListener> listeners = 
          new ArrayList<InventoryListener>();
  private ItemSet[][] items;
  private ItemSet[] equipment;
  private int money;
          
  public Inventory ()
  {
    // default constructor used for reflection
  }
  
  public Inventory (int width, int height)
  {
    this.items = new ItemSet[width][height];
    this.equipment = new ItemSet[Equipment.values ().length];
    this.money = 0;
  }
  
  public int getWidth ()
  {
    return items.length;
  }
  
  public int getHeight ()
  {
    return items[0].length;
  }
  
  public void update (float tpf)
  {
    for (int x = 0; x < this.items.length; x++)
    {
      for (int y = 0; y < this.items[x].length; y++)
      {
        if (this.items[x][y] != null && this.items[x][y].getAmount () <= 0)
        {
          this.items[x][y] = null;
        }
      }
    }
  }
  
  public boolean isEmpty ()
  {
    boolean empty = true;
    
    for (int x = 0; empty && x < this.items.length; x++)
    {
      for (int y = 0; empty && y < this.items[x].length; y++)
      {
        if (null != this.items[x][y])
        {
          empty = false;
        }
      }
    }
    
    return empty;
  }
  
  public void clear ()
  {
    for (int x = 0; x < this.items.length; x++)
    {
      for (int y = 0; y < this.items[x].length; y++)
      {
        if (null != this.items[x][y])
        {
          this.items[x][y] = null;
        }
      }
    }
  }
  
  public int getFreeSpace ()
  {
    int freeSpace = 0;
    for (int x = 0; x < this.items.length; x++)
    {
      for (int y = 0; y < this.items[x].length; y++)
      {
        if (null == this.items[x][y])
        {
          freeSpace++;
        }
      }
    }
    return freeSpace;
  }
  
  public int getMoney ()
  {
    return this.money;
  }
  
  public void addMoney (int money)
  {
    this.money += money;
  }
  
  public boolean spendMoney (int money)
  {
    boolean spent = false;
    if (this.money >= money)
    {
      addMoney (-money);
      spent = true;
    }
    return spent;
  }
  
  public ItemSet getItem (int x, int y)
  {
    return this.items[x][y];
  }
  
  public ItemSet takeItem (int x, int y)
  {
    ItemSet item = this.items[x][y];
    this.items[x][y] = null;
    if (null != item)
    {
      loseItem (item.getItem (), item.getAmount ());
    }
    return item;
  }
  
  public ItemSet splitItem (int x, int y)
  {
    ItemSet item = new ItemSet ();
    ItemSet orig = this.items[x][y];
    item.setItem (orig.getItem ());
    item.setAmount (orig.getAmount () / 2);
    orig.setAmount (orig.getAmount () - item.getAmount ());    
    loseItem (item.getItem (), item.getAmount ());
    return item;
  }
  
  public void putItem (int x, int y, ItemSet item)
  {
    if (null == this.items[x][y] && null != item)
    {
      this.items[x][y] = item;
      gainItem (item.getItem (), item.getAmount ());
    }
  }
  
  public void swapItems (int x1, int y1, int x2, int y2)
  {
    ItemSet temp1 = this.items[x1][y1];
    ItemSet temp2 = this.items[x2][y2];
    if (null != temp1 && null != temp2 && 
            temp1.getItem ().equals (temp2.getItem ()))
    {
      temp1.setAmount (temp1.getAmount () + temp2.getAmount ());
      this.items[x2][y2] = null;
    }
    else
    {
      this.items[x1][y1] = temp2;
      this.items[x2][y2] = temp1;
    }
  }
  
  public void useItem (int x, int y, Entity owner)
  {
    ItemSet item = this.items[x][y];
    
    if (null != item)
    {
      owner.use (item, ((AutoAttackAI) (owner.getAI 
              (Game.AUTO_ATTACK_AI))).getTarget ());
    }
  }
  
  public void useItem (Item item, Entity owner)
  {
    ItemSet itemSet = null;
    ItemSet possible;
    
    for (int i = 0; null == itemSet && i < this.equipment.length; i++)
    {
      possible = this.equipment[i];
      if (null != possible && item.equals (possible.getItem ()))
      {
        itemSet = possible;
      }
    }
    for (int x = 0; null == itemSet && x < this.items.length; x++)
    {
      for (int y = 0; null == itemSet && y < this.items[x].length; y++)
      {
        possible = this.items[x][y];
        if (null != possible && item.equals (possible.getItem ()))
        {
          itemSet = possible;
        }
      }
    }
    
    if (null != itemSet)
    {
      owner.use (itemSet, ((AutoAttackAI) (owner.getAI 
              (Game.AUTO_ATTACK_AI))).getTarget ());
    }
  }
  
  public ItemSet getEquip (Equipment equip)
  {
    return this.equipment[equip.ordinal ()];
  }
  
  public void setEquip (Equipment equip, ItemSet item)
  {
    this.equipment[equip.ordinal ()] = item;
  }
  
  public ItemSet takeEquip (Equipment equip)
  {
    ItemSet eq = this.equipment[equip.ordinal ()];
    this.equipment[equip.ordinal ()] = null;
    return eq;
  }
  
  public int getEquipStat (Statistic stat)
  {
    int value = 0;
    
    for (int i = 0; i < this.equipment.length; i++)
    {
      if (null != this.equipment[i])
      {
        value += this.equipment[i].getItem ().getStatValueOnEquip (stat);
      }
    }
    
    return value;
  }
  
  public boolean addItem (ItemSet item)
  {
    boolean added = false;
    
    if (null != item)
    {
      for (int x = 0; !added && x < this.items.length; x++)
      {
        for (int y = 0; !added && y < this.items[x].length; y++)
        {
          if (null != this.items[x][y] &&
                  this.items[x][y].getItem ().equals (item.getItem ()))
          {
            this.items[x][y].setAmount (this.items[x][y].getAmount () + 
                    item.getAmount ());
            added = true;
            gainItem (item.getItem (), item.getAmount ());
          }
        }
      }

      for (int x = 0; !added && x < this.items.length; x++)
      {
        for (int y = 0; !added && y < this.items[x].length; y++)
        {
          if (null == this.items[x][y])
          {
            this.items[x][y] = item;
            added = true;
            gainItem (item.getItem (), item.getAmount ());
          }
        }
      }
    }
    
    return added;
  }
  
  public void useUpItem (Item item)
  {
    boolean found = false;
    
    for (int x = 0; !found && x < this.items.length; x++)
    {
      for (int y = 0; !found && y < this.items[x].length; y++)
      {
        if (null != this.items[x][y] &&
                this.items[x][y].getItem ().equals (item))
        {
          this.items[x][y].setAmount (this.items[x][y].getAmount () - 1);
          found = true;
          loseItem (item, 1);
        }
      }
    }
  }
  
  public void addListener (InventoryListener listener)
  {
    this.listeners.add (listener);
  }
  
  public void loseItem (Item item, int amount)
  {
    ItemSet itemSet = new ItemSet (item, amount);
    for (InventoryListener listener : this.listeners)
    {
      listener.loseItem (itemSet);
    }
  }
  
  public void gainItem (Item item, int amount)
  {
    ItemSet itemSet = new ItemSet (item, amount);
    for (InventoryListener listener : this.listeners)
    {
      listener.pickupItem (itemSet);
    }
  }
}
