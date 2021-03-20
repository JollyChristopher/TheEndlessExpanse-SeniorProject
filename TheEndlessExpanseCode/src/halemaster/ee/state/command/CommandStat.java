package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.Command;
import halemaster.ee.state.PlayerState;

/**
 * @name CommandStat
 * 
 * @version 0.0.0
 * 
 * @date Mar 14, 2014
 */
public class CommandStat extends Command
{
  public static final String NAME = "giveStat";
  public static final Command.CommandLevel LEVEL = Command.CommandLevel.ADMIN;
  private static final String USAGE = NAME + " <value>";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    int stats;
    
    if (1 == arguments.length)
    {
      try
      {
        stats = Integer.valueOf (arguments[0]);
        console.output ("Giving you " + stats + " stat points!");
        state.getPlayer ().setStatPoints (state.getPlayer ()
                .getStatPoints () + stats);
        return true;
      }
      catch (NumberFormatException e)
      {
        // log
      }
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
  public Command.CommandLevel getLevel ()
  {
    return LEVEL;
  }
}
