package halemaster.ee.item;

import halemaster.ee.Json;
import halemaster.ee.world.entity.UseAnimation;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @name Enchant
 * 
 * @version 0.0.0
 * 
 * @date Mar 23, 2014
 */
public class Enchant 
{
  private static Map<String, Enchant> templates = null;
  
  private String id;
  private float rarity;
  private int availableAt;
  private String useTarget;
  private String attackTarget;
  private StatModifier onUse;
  private UseAnimation useAnimation;
  private StatModifier onEquip;
  private UseAnimation attackAnimation;

  public String getId ()
  {
    return id;
  }

  public float getRarity ()
  {
    return rarity;
  }

  public int getAvailableAt ()
  {
    return availableAt;
  }

  public String getUseTarget ()
  {
    return useTarget;
  }
  
  public String getAttackTarget ()
  {
    return attackTarget;
  }

  public StatModifier getOnUse ()
  {
    return onUse;
  }

  public UseAnimation getUseAnimation ()
  {
    return useAnimation;
  }

  public StatModifier getOnEquip ()
  {
    return onEquip;
  }
  
  public UseAnimation getAttackAnimation ()
  {
    return attackAnimation;
  }
  
  /**
   * load all templates from the Enchants folder
   */
  public static void loadTemplates ()
  {
    templates = new HashMap<String, Enchant> ();
    Enchant[] enchants;
    File templateFolder = new File ("assets/Enchants");
    
    for (File templateFile : templateFolder.listFiles ())
    {
      try
      {
        enchants = Json.getFromFile (templateFile, Enchant.class);
        for (Enchant enchant : enchants)
        {
          templates.put (enchant.getId (), enchant);
        }
      }
      catch (IOException e)
      {
        // log
      }
    }
  }
  
  /**
   * Get the enchant with the given id
   * @param id id of the enchant
   * @return enchant of the given id
   */
  public static Enchant getEnchant (String id)
  {
    if (null == templates)
    {
      loadTemplates ();
    }
    
    return templates.get (id);
  }
}
