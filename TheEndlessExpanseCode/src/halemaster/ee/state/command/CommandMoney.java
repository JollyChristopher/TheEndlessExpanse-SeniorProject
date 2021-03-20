package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.Command;
import halemaster.ee.state.PlayerState;

/**
 * @name CommandMoney
 * 
 * @version 0.0.0
 * 
 * @date Apr 1, 2014
 */
public class CommandMoney extends Command
{
  public static final String NAME = "giveMoney";
  public static final Command.CommandLevel LEVEL = Command.CommandLevel.ADMIN;
  private static final String USAGE = NAME + " <value>";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    int money;
    
    if (1 == arguments.length)
    {
      try
      {
        money = Integer.valueOf (arguments[0]);
        console.output ("Giving you " + money + " coins!");
        state.getPlayer ().getInventory ().addMoney (money);
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
