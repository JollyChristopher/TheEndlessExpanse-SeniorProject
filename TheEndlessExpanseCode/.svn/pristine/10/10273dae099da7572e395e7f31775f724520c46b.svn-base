package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.Command;
import halemaster.ee.state.PlayerState;
import halemaster.ee.world.Area;
import halemaster.ee.world.entity.Entity;

/**
 * @name CommandTeleport
 * 
 * @version 0.0.0
 * 
 * @date Mar 16, 2014
 */
public class CommandTeleport extends Command
{
  public static final String NAME = "tp";
  public static final Command.CommandLevel LEVEL = Command.CommandLevel.ADMIN;
  private static final String USAGE = NAME + " x y";
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    Entity player = state.getPlayer ();
    Area loc = player.getExactLocation ();
    
    if (2 == arguments.length)
    {
      try
      {
        player.move (Float.valueOf (arguments[0]) - loc.getX (), 
                Float.valueOf (arguments[1]) - loc.getY ());
        console.output ("Teleport to (" + arguments[0] + "," + arguments[1]
                + ")");
        return true;
      }
      catch (NumberFormatException e)
      {
        return false;
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
