package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.Command;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.entity.Statistic;

/**
 * @name CommandHeal
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandHeal extends Command
{
  public static final String NAME = "heal";
  public static final CommandLevel LEVEL = CommandLevel.ADMIN;
  private static final String USAGE = NAME;
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {    
    if (0 == arguments.length)
    {
      console.output ("Healed to full health");
      state.getPlayer ().heal (state.getPlayer ().getStat (Statistic.HEALTH));
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
