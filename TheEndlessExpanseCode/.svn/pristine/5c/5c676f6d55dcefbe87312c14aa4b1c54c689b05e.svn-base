package halemaster.ee.state;

import de.lessvoid.nifty.controls.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

/**
 * @name Command
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public abstract class Command 
{
  public enum CommandLevel
  {
    USER (0, 0),
    HIDDEN (USER.visibleLevel + 100, USER.usableLevel),
    ADMIN (HIDDEN.visibleLevel + 900, HIDDEN.visibleLevel + 900);
    
    private int visibleLevel;
    private int usableLevel;
    
    private CommandLevel (int visibleLevel, int usableLevel)
    {
      this.visibleLevel = visibleLevel;
      this.usableLevel = usableLevel;
    }
    
    /**
     * Test to see if the given command level is visible according to the
     * current command level.
     * @param level level to check visibility of
     * @return whether the command is visible
     */
    public boolean visible (CommandLevel level)
    {
      return level.visibleLevel <= this.visibleLevel;
    }
    
    /**
     * Test to see if the given command level is usable according to the
     * current command level.
     * @param level level to check usability of
     * @return whether the command is usable
     */
    public boolean usable (CommandLevel level)
    {
      return level.usableLevel <= this.usableLevel;
    }
  }
  
  private static Map<String, Command> commands;
  private static CommandLevel level = CommandLevel.USER;
  
  /**
   * Execute the given command
   * @param arguments arguements of the command
   * @param state player state for game modification
   * @param console console calling the command
   * @return whether the command was formatted properly
   */
  public abstract boolean execute (String[] arguments, PlayerState state,
          Console console);
  
  /**
   * Get the usage string of the Command
   * @return the usage string of the command
   */
  public abstract String getUsage ();
  
  /**
   * Get the name of the Command
   * @return the name of the command
   */
  public abstract String getName ();
  
  /**
   * Get the level of the command.
   * @return the level of the command.
   */
  public abstract CommandLevel getLevel ();
  
  private static void loadCommands ()
  {
    if (null == commands)
    {
      commands = new HashMap<String, Command>();
      Reflections reflections = new Reflections
              ("halemaster.ee.state.command");

      Set<Class<? extends Command>> allClasses = 
        reflections.getSubTypesOf(Command.class);
      
      Iterator<Class<? extends Command>> it = allClasses.iterator ();
      while (it.hasNext ())
      {
        Class<? extends Command> temp = it.next ();
        try
        {
          Command obj = temp.newInstance ();
          commands.put (obj.getName (), obj);
        }
        catch (InstantiationException e)
        {
          // log
        }
        catch (IllegalAccessException e)
        {
          // log
        }
      }
    }
  }
  
  /**
   * Find a command by name.
   * @param command command to find
   * @return the Command, or null if it doesn't exist within level reach.
   */
  public static Command findCommand (String command)
  {
    loadCommands ();
    
    Command foundCommand = commands.get (command);
    
    if (null != foundCommand && !level.usable (foundCommand.getLevel ()))
    {
      foundCommand = null;
    }
    
    return foundCommand;
  }
  
  /**
   * Get all command names
   * @return names of commands
   */
  public static List<String> getCommandNames ()
  {
    loadCommands ();
    
    List<String> commandList = new ArrayList<String> ();
    
    for (String command : commands.keySet ())
    {
      if (level.visible (commands.get (command).getLevel ()))
      {
        commandList.add (command);
      }
    }
    
    return commandList;
  }
  
  /**
   * Set the level of the Command interface
   * @param newLevel the new level of the command interface
   */
  public static void setLevel (CommandLevel newLevel)
  {
    level = newLevel;
  }
  
  /**
   * Get the longest command prefix from the given prefix
   * @param prefix prefix to start from
   * @return longest prefix that is common to all commands of the prefix
   */
  public static String getLongestPrefix (String prefix)
  {
    loadCommands ();
    
    List<String> commandList = getCommandNames ();
    boolean foundPrefix = false;
    
    for (int i = 0; i < commandList.size (); i++)
    {
      if (!commandList.get (i).startsWith (prefix))
      {
        commandList.remove (i);
        i--;
      }
    }
    
    if (0 == commandList.size ())
    {
      foundPrefix = true;
    }
    
    while (!foundPrefix)
    {
      for (String command : commandList)
      {
        if (!command.startsWith (prefix))
        {
          foundPrefix = true;
        }
      }
      
      if (!foundPrefix)
      {
        if (commandList.get (0).length () > prefix.length ())
        {
          prefix = prefix + commandList.get (0).charAt (prefix.length ());
        }
        else
        {
          foundPrefix = true;
        }
      }
      else
      {
        prefix = prefix.substring (0, prefix.length () - 1);
      }
    }
    
    return prefix;
  }
}
