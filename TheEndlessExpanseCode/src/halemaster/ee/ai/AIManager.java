package halemaster.ee.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name AIManager
 * 
 * @version 0.0.0
 * 
 * @date Feb 19, 2014
 */
public class AIManager 
{
  public static final Logger LOGGER = 
          Logger.getLogger (AIManager.class.getName ());
  private static Map<String, Class<? extends AI>> aiList = 
          new HashMap<String, Class<? extends AI>>();
  
  /**
   * Get a new instance of the AI with the given id.
   * @param id id of ai
   * @return AI instance
   */
  public static AI getAI (String id)
  {
    AI newAI = null;
    Class<? extends AI> aiClass = aiList.get (id);
    
    if (null != aiClass)
    {
      try
      {
        newAI = aiClass.newInstance ();
      }
      catch (InstantiationException e)
      {
        LOGGER.log (Level.WARNING, "Could not get AI object", e);
      }
      catch (IllegalAccessException e)
      {
        LOGGER.log (Level.WARNING, "Could not get AI object", e);
      }
    }
    
    return newAI;
  }
  
  /**
   * Register a new ai class with the given id
   * @param id id of the ai
   * @param ai class of the ai
   */
  public static void registerAI (String id, Class<? extends AI> ai)
  {
    aiList.put (id, ai);
  }
}
