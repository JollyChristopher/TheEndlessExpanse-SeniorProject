package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.Command;

/**
 * @name CommandExit
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandExit extends Command
{
  public static final String NAME = "exit";
  public static final CommandLevel LEVEL = CommandLevel.USER;
  private static final String USAGE = NAME;
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    if (0 == arguments.length)
    {
      state.getHud ().toggleConsole ();
      return true;
    }
    
    return false;
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
