/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package halemaster.ee.localization;

import halemaster.ee.Json;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Halemaster
 */
public class NameGen
{
  private static final int NO_CHANGE = -1;
  private static Map<String, NameChance[]> generators =
          new HashMap<String, NameChance[]> ();
  
  public static String getName (Random random, String type, int index)
  {
    int nextChance = NO_CHANGE;
    String name = "";
    String builder;
    String[] parts;
    NameChance[] tempGen;
    File file = new File ("./assets/Interface/NameGeneration/" + type + ".gen");
    
    try
    {
      if (!generators.containsKey (type))
      {
          tempGen = Json.getFromFile (file, NameChance.class);
          generators.put (type, tempGen);
      }
      if (index >= 0 && index < generators.get (type).length)
      {
        builder = generators.get (type)[index].getChoice ()
              [random.nextInt (generators.get (type)[index]
              .getChoice ().length)];
        parts = builder.split (":");
        name = "";
        
        for (int i = 0; i < parts.length; i++)
        {
          try
          {
            nextChance = Integer.valueOf (parts[i]);
          }
          catch (NumberFormatException e)
          {
            if (parts[i].equals ("NAMEGEN"))
            {
              builder = getName (random, parts[i + 1].split ("\\.")[0], 
                      Integer.valueOf (parts[i + 1].split ("\\.")[1]))
                      .split (":")[0];
              name += builder;
              i++;
            }
            else
            {
              name += parts[i];
            }
          }
        }
        
        if (NO_CHANGE != nextChance)
        {
          name += ":" + String.valueOf (nextChance);
        }
      }
    }
    catch (IOException e)
    {
      
    }
    
    return name;
  }
  
  public static String getName (Random random, String type)
  {
    String name = "";
    String nextName = getName (random, type, 0);
    String[] splitApart;
    int nextChance;
    int index = 0;
    
    while (!"".equals (nextName))
    {
      splitApart = nextName.split (":");
      if (splitApart.length > 1)
      {
        nextChance = Integer.valueOf (splitApart[1]);
      }
      else if (generators.get (type).length > index + 1)
      {
        nextChance = generators.get (type)[index + 1].getChance ();
      }
      else
      {
        nextChance = 0;
      }
      name += splitApart[0];
      
      if (random.nextInt (100) < nextChance)
      {
        index++;
        nextName = getName (random, type, index);
      }
      else
      {
        nextName = "";
      }
    }
    
    if ("".equals (name))
    {
      name = type + random.nextInt ();
    }
    
    return name;
  }
}
