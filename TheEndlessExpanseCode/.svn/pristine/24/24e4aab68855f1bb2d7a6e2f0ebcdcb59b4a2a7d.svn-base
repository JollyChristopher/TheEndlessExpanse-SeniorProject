package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.Command;

/**
 * @name CommandAdmin
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandAdmin  extends Command
{
  public static final String NAME = "secr3tDevices";
  public static final CommandLevel LEVEL = CommandLevel.HIDDEN;
  private static final String USAGE = NAME;
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    if (0 == arguments.length)
    {
      console.output ("Bumping this session to admin");
      Command.setLevel (CommandLevel.ADMIN);
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
