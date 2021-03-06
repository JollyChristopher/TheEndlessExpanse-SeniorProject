package halemaster.ee.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @name Reward
 * 
 * @version 0.0.0
 * 
 * @date Mar 5, 2014
 */
public class Reward 
{
  public static int MONEY_MAX_PER_LEVEL = 10;
  public static int MONEY_MIN_PER_LEVEL = 5;
  public static int LEVEL_DIFF = 5;
  public static int MAX_ITEMS = 5;
  
  private ItemSet[] items;
  private int level;
  private int money;
  
  public Reward (int level, int money, ItemSet ... items)
  {
    this.items = items;
    this.level = level;
    this.money = money;
  }
  
  public int getLevel ()
  {
    return this.level;
  }
  
  public ItemSet[] getItems ()
  {
    return this.items;
  }
  
  public int getMoney ()
  {
    return this.money;
  }
  
  /**
   * Get a reward suitable for the level given
   * @param level level of the reward
   * @return the suitable reward
   */
  public static Reward getReward (int level)
  {
    Reward reward;
    ItemSet[] items;
    Random random = new Random ();
    List<ItemTemplate> templates;
    ItemTemplate template;
    Item item;
    int money;
    int itemCount;
    int itemLevel = Math.max(1, level - random.nextInt (LEVEL_DIFF));
    int moneyLevel = Math.max(1, level - random.nextInt (LEVEL_DIFF));
    Map<Item, Integer> itemSet = new HashMap<Item, Integer>();
    
    money = random.nextInt (MONEY_MAX_PER_LEVEL * moneyLevel - 
            MONEY_MIN_PER_LEVEL * moneyLevel) + MONEY_MIN_PER_LEVEL * moneyLevel;
    
    templates = ItemTemplate.getForLevel (itemLevel);
    itemCount = random.nextInt (MAX_ITEMS);
    
    for (int i = 0; i < itemCount; i++)
    {
      template = getTemplate (templates, random);
      item = new Item (template, level, random);
      
      if (!itemSet.containsKey (item))
      {
        itemSet.put (item, 1);
      }
      else
      {
        itemSet.put (item, itemSet.get (item) + 1);
      }
    }
    
    items = new ItemSet[itemSet.size ()];
    int index = 0;
    for (Entry<Item, Integer> itemEntry : itemSet.entrySet ())
    {
      items[index] = new ItemSet ();
      items[index].setAmount (itemEntry.getValue ());
      items[index].setItem (itemEntry.getKey ());
    }
    
    reward = new Reward (level, money, items);
    
    return reward;
  }
  
  public static List<ItemTemplate> getStackables (int level)
  {
    List<ItemTemplate> templates = ItemTemplate.getForLevel (level);
    
    for (int i = 0; i < templates.size (); i++)
    {
      ItemTemplate temp = templates.get (i);
      if (temp.getNames ().length > 1 || temp.getEnchants ().length > 0
              || (temp.getOnUse ().length == 1 && 
              temp.getOnUse ()[0].getChance () < 100) || 
              temp.getOnUse ().length > 1)
      {
        templates.remove (i);
        i--;
      }
    }
    
    return templates;
  }
  
  public static Item getStackable (int level)
  {
    Item stackable;
    List<ItemTemplate> templates = getStackables(level);
    Random random = new Random ();
    
    stackable = new Item (templates.get (random.nextInt 
            (templates.size ())), level, random);
    
    return stackable;
  }
  
  /**
   * Find the weighted template from the list, given the random object
   * @param templates templates to look through
   * @param random random to use
   * @return a random template from the list
   */
  private static ItemTemplate getTemplate (List<ItemTemplate> templates, 
          Random random)
  {
    ItemTemplate template = null;
    int valueTotal = 0;
    int chosen;
    
    for (ItemTemplate temp : templates)
    {
      valueTotal += temp.getRarity ();
    }
    chosen = random.nextInt (valueTotal);
    
    for (int i = 0; null == template && i < templates.size (); i++)
    {
      ItemTemplate temp = templates.get (i);
      if (chosen < temp.getRarity ())
      {
        template = temp;
      }
      else
      {
        chosen -= temp.getRarity ();
      }
    }
    
    return template;
  }
}
