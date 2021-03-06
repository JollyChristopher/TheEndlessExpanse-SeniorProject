package halemaster.ee.state;

import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.input.mapping.DefaultInputMapping;

/**
 * @name HudMapping
 * 
 * @version 0.0.0
 * 
 * @date Mar 13, 2014
 */
public class HudMapping extends DefaultInputMapping
{
  @Override
  public NiftyInputEvent convert(KeyboardInputEvent inputEvent) 
  {
    if(inputEvent.getKey() == KeyboardInputEvent.KEY_UP) 
    {
      return NiftyInputEvent.MoveCursorUp;
    }
    else if (inputEvent.getKey () == KeyboardInputEvent.KEY_DOWN)
    {
      return NiftyInputEvent.MoveCursorDown;
    }
    else if (inputEvent.getKey () == KeyboardInputEvent.KEY_TAB)
    {
      return NiftyInputEvent.NextInputElement;
    }
    return super.convert (inputEvent);
  }
}
