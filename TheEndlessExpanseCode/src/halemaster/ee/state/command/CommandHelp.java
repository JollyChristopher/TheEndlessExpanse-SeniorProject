package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.Command;
import java.util.Collections;
import java.util.List;

/**
 * @name CommandHelp
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandHelp extends Command
{
  public static final String NAME = "help";
  public static final CommandLevel LEVEL = CommandLevel.USER;
  public static final int PAGE_SIZE = 5;
  public static final String PAGE_HINT = "Include a page number after help to "
          + "look at different pages";
  public static final String PAGE = "PAGE";
  private static final String USAGE = NAME + " (page)";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    boolean success;
    String[][] pages;
    int page = -1;
    List<String> allCommands = Command.getCommandNames ();
    
    Collections.sort (allCommands);
    pages = new String[(allCommands.size () - 1) / PAGE_SIZE + 1][PAGE_SIZE];
    int pageIndex = 0;
    for (int i = 0; i < allCommands.size (); i++)
    {
      pages[pageIndex][i % PAGE_SIZE] = Command.findCommand 
              (allCommands.get (i)).getUsage ();

      if ((i + 1) % PAGE_SIZE == 0)
      {
        pageIndex++;
      }
    }
    
    switch (arguments.length)
    {
      case 0: // no arguments. Page 0 with hint
        console.output (PAGE_HINT);
        page = 0;
        success = true;
        break;
      case 1: // page number included
        success = false;
        try
        {
          page = Integer.valueOf (arguments[0]);
          page--;
          if (page < pages.length)
          {
            success = true;
          }
          else
          {
            page = -1;
          }
        }
        catch (NumberFormatException e)
        {
          // log
        }
        break;
      default:
        success = false;
        break;
    }
    
    if (-1 != page)
    {
      for (String usage : pages[page])
      {
        if (null != usage && !"".equals (usage))
        {
          console.output (usage);
        }
      }
      
      console.output (PAGE + " " + (page + 1) + "/" + pages.length);
    }
    
    return success;
  }
  
  @Override
  public String getUsage ()
  {
    return USAGE;
  }
  
  @Override
  public String getName ()
  {
    return NAME;
  }
  
  @Override
  public CommandLevel getLevel ()
  {
    return LEVEL;
  }
}
