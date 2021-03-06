package halemaster.ee.item;

import halemaster.ee.Json;
import halemaster.ee.world.entity.UseAnimation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @name ItemTemplate
 * 
 * @version 0.0.0
 * 
 * @date Mar 23, 2014
 */
public class ItemTemplate 
{
  private static List<ItemTemplate> templates = null;
  
  private String[] names;
  private String[] enchants;
  private UseCase[] onUse;
  private String[] equipLocations;
  private StatModifier onEquip;
  private String weaponType;
  private int levelGap;
  private int rarity;
  private int availableAt;
  private String image;
  private UseAnimation attackAnimation;

  public String[] getNames ()
  {
    return names;
  }

  public String[] getEnchants ()
  {
    return enchants;
  }

  public UseCase[] getOnUse ()
  {
    return onUse;
  }

  public String[] getEquipLocations ()
  {
    return equipLocations;
  }

  public StatModifier getOnEquip ()
  {
    return onEquip;
  }

  public String getWeaponType ()
  {
    return weaponType;
  }

  public int getLevelGap ()
  {
    return levelGap;
  }

  public int getRarity ()
  {
    return rarity;
  }

  public int getAvailableAt ()
  {
    return availableAt;
  }

  public String getImage ()
  {
    return image;
  }

  public UseAnimation getAttackAnimation ()
  {
    return attackAnimation;
  }
  
  /**
   * load all templates from the Items folder
   */
  public static void loadTemplates ()
  {
    templates = new ArrayList<ItemTemplate> ();
    File templateFolder = new File ("assets/Items");
    
    for (File templateFile : templateFolder.listFiles ())
    {
      try
      {
        templates.addAll (Arrays.asList (Json.getFromFile (templateFile,
                ItemTemplate.class)));
      }
      catch (IOException e)
      {
        // log
      }
    }
  }
  
  /**
   * Get all items that are avaiable at most a specific level.
   * @param level level of the highest level item
   * @return list of templates within the level range.
   */
  public static List<ItemTemplate> getForLevel (int level)
  {
    List<ItemTemplate> levelItems = new ArrayList<ItemTemplate>();
    
    if (null == templates)
    {
      loadTemplates ();
    }
    
    for (ItemTemplate template : templates)
    {
      if (template.getAvailableAt () <= level)
      {
        levelItems.add (template);
      }
    }
    
    return levelItems;
  }
}
