package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.Command;
import halemaster.ee.state.PlayerState;

/**
 * @name CommandXp
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandXp extends Command
{
  public static final String NAME = "giveXp";
  public static final CommandLevel LEVEL = CommandLevel.ADMIN;
  private static final String USAGE = NAME + " <value>";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    int xp;
    
    if (1 == arguments.length)
    {
      try
      {
        xp = Integer.valueOf (arguments[0]);
        console.output ("Giving you " + xp + " xp!");
        state.getPlayer ().addXp (xp);
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
  public CommandLevel getLevel ()
  {
    return LEVEL;
  }
}
