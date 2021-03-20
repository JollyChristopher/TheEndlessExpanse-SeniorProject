package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.Command;

/**
 * @name CommandSay
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandSay extends Command
{
  public static final String NAME = "say";
  public static final CommandLevel LEVEL = CommandLevel.USER;
  private static final String USAGE = NAME + " (...message)";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    String message = "";
    for (String argument : arguments)
    {
      message += argument + " ";
    }
    console.output (message);
    
    return true;
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
