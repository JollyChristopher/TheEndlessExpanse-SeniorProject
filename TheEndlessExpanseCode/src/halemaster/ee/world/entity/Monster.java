package halemaster.ee.world.entity;

import halemaster.ee.Json;
import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.terrain.tile.BiomeTile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name Monster
 * 
 * @version 0.0.0
 * 
 * @date Feb 13, 2014
 */
public class Monster 
{
  public static final String MONSTER_FOLDER = "assets/Monsters";
  
  private List<Map<String, String[]>> images;
  private float speed;
  private String name;
  private String[] possibleBiomes;
  private String initialSprite;
  private List<String> ai;
  private float powerBase;
  private float agilityBase;
  private float intellectBase;
  private String defaultAttack = Statistic.POWER.name ();
  private UseAnimation attackAnimation = null;
  
  private static Map<Biome, List<Monster>> monsters = null;

  public String getDefaultAttack ()
  {
    return this.defaultAttack;
  }
  
  public Map<String, String[]> getImages (int index)
  {
    return images.get (index);
  }
  
  public int getImageCount ()
  {
    return images.size ();
  }

  public void setImages (List<Map<String, String[]>> images)
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

  public String getName ()
  {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public String[] getPossibleBiomes ()
  {
    return possibleBiomes;
  }

  public void setPossibleBiomes (String[] possibleBiomes)
  {
    this.possibleBiomes = possibleBiomes;
  }

  public String getInitialSprite ()
  {
    return initialSprite;
  }

  public void setInitialSprite (String initialSprite)
  {
    this.initialSprite = initialSprite;
  }

  public List<String> getAi ()
  {
    return ai;
  }

  public void setAi (List<String> ai)
  {
    this.ai = ai;
  }
  
  public UseAnimation getAttackAnimation ()
  {
    return this.attackAnimation;
  }

  public float getPowerBase ()
  {
    return powerBase;
  }

  public void setPowerBase (float powerBase)
  {
    this.powerBase = powerBase;
  }

  public float getAgilityBase ()
  {
    return agilityBase;
  }

  public void setAgilityBase (float agilityBase)
  {
    this.agilityBase = agilityBase;
  }

  public float getIntellectBase ()
  {
    return intellectBase;
  }

  public void setIntellectBase (float intellectBase)
  {
    this.intellectBase = intellectBase;
  }
  
  public int[] getStructureForBiome (String biome)
  {
    int[] structs = null;
    String[] splits;
    
    for (int i = 0; null == structs && i < this.possibleBiomes.length; i++)
    {
      splits = this.possibleBiomes[i].split ("\\.");
      if (splits[0].equals (biome))
      {
        if (splits.length > 1)
        {
          structs = new int[splits.length - 1];
          for (int j = 0; j < structs.length; j++)
          {
            try
            {
              structs[j] = Integer.valueOf (splits[j + 1]);
            }
            catch (NumberFormatException e)
            {
              // log
            }
          }
        }
        else
        {
          structs = new int[1];
          structs[0] = BiomeTile.NONE;
        }
      }
    }
    
    return structs;
  }
  
  private static void loadMonsters ()
  {
    List<Monster> foundMonsters;
    File monsterFolder = new File (MONSTER_FOLDER);
    Monster[] monstersInFile;
    
    if (null == monsters)
    {
      monsters = new HashMap<Biome, List<Monster>>();
      for (File monsterFile : monsterFolder.listFiles ())
      {
        try
        {
          monstersInFile = Json.getFromFile (monsterFile, Monster.class);
          for (Monster monster : monstersInFile)
          {
            for (String possible : monster.getPossibleBiomes ())
            {
              String[] possibleSplit = possible.split ("\\.");
              Biome possibleBiome = BiomeClassifier.getBiome (possibleSplit[0]);
              foundMonsters = monsters.get (possibleBiome);
              if (null == foundMonsters)
              {
                foundMonsters = new ArrayList<Monster>();
                monsters.put (possibleBiome, foundMonsters);
              }
              foundMonsters.add (monster);
            }
          }
        }
        catch (IOException e)
        {
          // log
        }
      }
    }
  }
  
  public static Monster getForName (String name)
  {
    loadMonsters ();
    
    for (List<Monster> monsterSet : monsters.values ())
    {
      for (Monster mon : monsterSet)
      {
        if (mon.getName ().equals (name))
        {
          return mon;
        }
      }
    }
    
    return null;
  }
  
  public static List<Monster> getForBiome (Biome biome)
  {
    loadMonsters ();
    List<Monster> mons = new ArrayList<Monster> ();
    mons.addAll (monsters.get (biome));
    
    return mons;
  }
}
