package halemaster.ee.state.command;

import de.lessvoid.nifty.controls.Console;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.Command;
import halemaster.ee.world.Config;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import java.io.IOException;

/**
 * @name CommandSeed
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class CommandSeed extends Command
{
  public static final String NAME = "seed";
  public static final CommandLevel LEVEL = CommandLevel.USER;
  private static final String USAGE = NAME;
  
  @Override
  public boolean execute (String[] arguments, PlayerState state, Console console)
  {
    if (0 == arguments.length)
    {
      try
      {
        console.output ("Seed:" + Config.getValue (state.getWorld ()
                .getEvents ().getWorldName (), MacroTerrainGenerator.SEED_KEY));
        return true;
      }
      catch (IOException e)
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
