package halemaster.ee.world.history.event;

import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.Area;
import halemaster.ee.world.history.event.type.EventType;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @name EventIO
 * 
 * @version 0.0.0
 * 
 * @date Nov 19, 2013
 */
public class EventIO 
{
  public static final String HISTORY_FOLDER = "/history";
  public static final String HISTORY_EXT = "history";
  public static final String EVENT_FOLDER = "assets/Events";
  public static final String LOCAL_FOLDER = "/Local";
  public static final String GLOBAL_FOLDER = "/Global";
  
  /**
   * Get the types from the Json file.
   * 
   * @param file file to get types from.
   * @return all types as an array.
   * @throws IOException 
   */
  public static EventType[] getTypes (File file) throws IOException
  {
    return Json.getFromFile (file, EventType.class);
  }
  
  public static EventHolder getEvents (String world, EventHolder holder, 
          int left, int right, int top, int bottom, EventTypeHolder ... types) 
          throws IOException, InstantiationException, IllegalAccessException
  {
    File areaFolder = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + PlayerState.LOCATION_FOLDER +
            HISTORY_FOLDER);
    File anywhereFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + world + "/anywhere." + HISTORY_EXT);
    Event[] anywhereEvents;
    Event[] areaEvents;
    Set<EventTypeHolder> typeSet;
    
    if (holder == null)
    {
      holder = new EventHolder (world);
    }
    else if (null == types || types.length == 0)
    {
      typeSet = holder.getTypeHolders ();
      types = new EventTypeHolder[typeSet.size ()];
      int index = 0;
      
      for (EventTypeHolder temp : typeSet)
      {
        types[index] = temp;
        index++;
      }
    }
    
    for (int i = 0; i < types.length; i++)
    {
      holder.addTypeHolder (types[i]);
    }
    
    if (Area.ANYWHERE.getX () == left && Area.ANYWHERE.getY () == top)
    {
      anywhereEvents = Json.getFromFile (anywhereFile, Event.class);
    
      for (int i = 0; i < anywhereEvents.length; i++)
      {
        anywhereEvents[i].setHolder (holder);
        anywhereEvents[i].shake ();
        holder.addEvent (anywhereEvents[i]);
      }
    }
    
    if (top == bottom && left == right)
    {
      File areaFile = new File (areaFolder.getPath ()+ "/" + left + "," + 
              top + "." + HISTORY_EXT);
      if (areaFile.canRead ())
      {
        areaEvents = Json.getFromFile (areaFile, Event.class);
        for (int i = 0; i < areaEvents.length; i++)
        {
          areaEvents[i].setHolder (holder);
          areaEvents[i].shake ();
          holder.addEvent (areaEvents[i]);
        }
      }
    }
    else
    {
      for (File areaFile : areaFolder.listFiles ())
      {
        String name = areaFile.getName ();
        if (name.split ("\\.")[1].equals(HISTORY_EXT))
        {
          name = name.split ("\\.")[0];
          int x = Integer.valueOf (name.split (",")[0]);
          int y = Integer.valueOf (name.split (",")[1]);

          if (x >= left && x <= right && y >= top && y <= bottom)
          {
            areaEvents = Json.getFromFile (areaFile, Event.class);
            for (int i = 0; i < areaEvents.length; i++)
            {
              areaEvents[i].setHolder (holder);
              areaEvents[i].shake ();
              holder.addEvent (areaEvents[i]);
            }
          }
        }
      }
    }
    
    return holder;
  }
  
  public static void saveEvents (EventHolder holder, String name) throws 
          IOException
  {
    saveEvents (holder, name, Area.ANYWHERE.getY (), Integer.MAX_VALUE, 
            Area.ANYWHERE.getX (), Integer.MAX_VALUE);
  }
  
  public static void saveEvents (EventHolder holder, String name, int top, 
          int bottom, int left, int right) throws IOException
  {
    Event[] inOrderEvents;
    List<Event> anywhereEvents = new ArrayList<Event> ();
    HashMap<Area, List<Event>> areaEvents = new HashMap<Area, List<Event>>();
    Event[] saveEvents;
    String folderName = Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + name + PlayerState.LOCATION_FOLDER
            + HISTORY_FOLDER;
    File areaFile = new File (folderName);
    File anywhereFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.WORLD_FOLDER  + "/" + name + "/anywhere." + HISTORY_EXT);
    
    inOrderEvents = holder.getEvents (0, Integer.MAX_VALUE, left, 
            right, top, bottom);
    areaFile.mkdirs ();
    
    for (int i = 0; i < inOrderEvents.length; i++)
    {
      inOrderEvents[i].unShake ();
      if (Area.ANYWHERE.equals (inOrderEvents[i].getLocation ()))
      {
        if (Area.ANYWHERE.getX () == left && Area.ANYWHERE.getY () == top)
        {
          anywhereEvents.add (inOrderEvents[i]);
        }
      }
      else
      {
        if (!areaEvents.containsKey (inOrderEvents[i].getLocation ()))
        {
          areaEvents.put (inOrderEvents[i].getLocation (), 
                  new ArrayList<Event>());
        }
        areaEvents.get (inOrderEvents[i].getLocation ()).add (inOrderEvents[i]);
      }
    }
    
    if (Area.ANYWHERE.getX () == left && Area.ANYWHERE.getY () == top)
    {
      saveEvents = new Event[0];
      saveEvents = anywhereEvents.toArray (saveEvents);
      Json.saveJson (anywhereFile, saveEvents);
    }
    
    for (Entry<Area, List<Event>> tempEvents : areaEvents.entrySet ())
    {
      areaFile = new File (folderName + "/"
              + tempEvents.getKey ().getX () + "," + 
              tempEvents.getKey ().getY () + "." + HISTORY_EXT);
    
      saveEvents = new Event[0];
      saveEvents = tempEvents.getValue ().toArray (saveEvents);
      
      Json.saveJson (areaFile, saveEvents);
    }
  }
}
