package halemaster.ee.state.start;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import halemaster.ee.Game;
import halemaster.ee.state.Menu;
import halemaster.ee.state.PlayerState;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;

/**
 * @name MenuOptions
 * 
 * @version 0.0.0
 * 
 * @date Oct 23, 2013
 */
public class MenuOptions extends Menu implements RawInputListener
{
  public static final String FILE = "Interface/menu/options.xml";
  public static final String ID = "options";
  public static final int MAX_WIDTH = 
          GraphicsEnvironment.getLocalGraphicsEnvironment()
          .getMaximumWindowBounds ().width;
  public static final int MAX_HEIGHT = 
          GraphicsEnvironment.getLocalGraphicsEnvironment()
          .getMaximumWindowBounds ().height;
  public static final float PERCENT = 100.0f; 
  private AppSettings settings;
  
  public MenuOptions ()
  {
    super (FILE, ID);
  }
  
  /**
   * Initialize this state
   * 
   * @param manager the manager to initialize with.
   * @param app the application to initialize with.
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    this.settings = new AppSettings (true);
    CheckBox fullScreen;
    CheckBox vSync;
    Slider width;
    Slider height;
    Button forward;
    Button back;
    Button left;
    Button right;
    CheckBox clickMove;
    
    super.initialize (manager, app);
    
    getGame ().getInputManager ().addRawInputListener (this);
    
    this.settings.copyFrom (getGame ().getSettings ());
    
    fullScreen = getNifty ().getCurrentScreen ()
            .findNiftyControl ("fullScreen_box", CheckBox.class);
    vSync = getNifty ().getCurrentScreen ()
            .findNiftyControl ("vSync_box", CheckBox.class);
    width = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenWidth", Slider.class);
    height = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenHeight", Slider.class);
    forward = getNifty ().getCurrentScreen ()
            .findNiftyControl ("forwardBtn", Button.class);
    back = getNifty ().getCurrentScreen ()
            .findNiftyControl ("backBtn", Button.class);
    left = getNifty ().getCurrentScreen ()
            .findNiftyControl ("leftBtn", Button.class);
    right = getNifty ().getCurrentScreen ()
            .findNiftyControl ("rightBtn", Button.class);
    clickMove = getNifty ().getCurrentScreen ()
            .findNiftyControl ("clickMove_box", CheckBox.class);

    fullScreen.setChecked (this.settings.isFullscreen ());
    vSync.setChecked (this.settings.isVSync ());
    width.setValue (this.settings.getWidth () / (float) MAX_WIDTH * PERCENT);
    height.setValue (this.settings.getHeight () / (float) MAX_HEIGHT * PERCENT);
    clickMove.setChecked (this.settings.getBoolean (PlayerState.CLICK_NAME));
    forward.setText (getKey(this.settings.getString (PlayerState.FORWARD)));
    back.setText (getKey(this.settings.getString (PlayerState.BACKWARD)));
    left.setText (getKey(this.settings.getString (PlayerState.LEFT)));
    right.setText (getKey(this.settings.getString (PlayerState.RIGHT)));
  }
  
  /**
   * Cleanup this state.
   */
  @Override
  public void cleanup ()
  {
    getGame ().getInputManager ().removeRawInputListener (this);
    
    super.cleanup ();
  }
  
  /**
   * Save the current options and apply them to the system.
   * 
   * @return whether the click was consumed.
   */
  public boolean save ()
  {
    AppSettings newSettings = new AppSettings (true);
    CheckBox fullScreen;
    CheckBox vSync;
    Slider width;
    Slider height;
    CheckBox clickMove;
    
    try
    {      
      fullScreen = getNifty ().getCurrentScreen ()
              .findNiftyControl ("fullScreen_box", CheckBox.class);
      vSync = getNifty ().getCurrentScreen ()
              .findNiftyControl ("vSync_box", CheckBox.class);
      width = getNifty ().getCurrentScreen ()
              .findNiftyControl ("screenWidth", Slider.class);
      height = getNifty ().getCurrentScreen ()
              .findNiftyControl ("screenHeight", Slider.class);
      clickMove = getNifty ().getCurrentScreen ()
              .findNiftyControl ("clickMove_box", CheckBox.class);
      
      this.settings.setVSync (vSync.isChecked ());
      this.settings.setResolution 
              ((int) (width.getValue () * MAX_WIDTH / PERCENT), 
              (int) (height.getValue () * MAX_HEIGHT / PERCENT));
      this.settings.putBoolean (PlayerState.CLICK_NAME, clickMove.isChecked ());
      
      newSettings.copyFrom (this.settings);
      
      if (fullScreen.isChecked ())
      {
        toggleToFullscreen (newSettings);
      }
      else
      {
        newSettings.setFullscreen (false);
      }
      
      newSettings.save (Game.SETTINGS);

      getGame ().setSettings (newSettings);
      getGame ().restart ();
      
      back ();
    }
    catch (BackingStoreException exception)
    {
      getGame ().handleError ("Failed to save settings", exception);
    }
    
    return true;
  }
  
  /**
   * Called as fast as possible.
   * 
   * @param tpf the time since the previous call to update.
   */
  @Override
  public void update (float tpf)
  {
    
    changeWidth ();
    changeHeight ();
  }
  
  /**
   * Get the display string from a given saved event string.
   * 
   * @param evt the string that was saved in the settings.
   * @return the display string for the above event string.
   */
  public static String getKey (String evt)
  {
    if (null != evt)
    {
      char character = evt.split (PlayerState.EVENT_SPLITTER)[0].charAt (0);
      int code = Integer.valueOf (evt.split (PlayerState.EVENT_SPLITTER)[1]);
      
      String input = String.valueOf(character).toUpperCase ();

      switch (code)
      {
        case KeyInput.KEY_UP:
          input = "^";
          break;
        case KeyInput.KEY_DOWN:
          input = "v";
          break;
        case KeyInput.KEY_RIGHT:
          input = ">";
          break;
        case KeyInput.KEY_LEFT:
          input = "<";
          break;
        case KeyInput.KEY_RETURN:
          input = "<E>";
          break;
      }

      return input;
    }
    else
    {
      return "";
    }
  }
  
  /**
   * Update the possible user keys based on key presses.
   * 
   * @param evt the event for the key press.
   */
  private void updateKeys (KeyInputEvent evt)
  {
    Button forward;
    Button back;
    Button left;
    Button right;
    boolean valid = 0 != evt.getKeyChar ();
    String eventString = evt.getKeyChar () + PlayerState.EVENT_SPLITTER + 
            evt.getKeyCode ();
    String input;
    
    switch (evt.getKeyCode ())
    {
      case KeyInput.KEY_UP:
      case KeyInput.KEY_DOWN:
      case KeyInput.KEY_RIGHT:
      case KeyInput.KEY_LEFT:
        valid = true;
        break;
      
      case KeyInput.KEY_TAB:
      case KeyInput.KEY_NUMPADENTER:
      case KeyInput.KEY_DELETE:
      case KeyInput.KEY_BACK:
      case KeyInput.KEY_SPACE:
        valid = false;
        break;
    }
    
    if (valid && evt.isPressed ())
    {
      input = getKey (eventString);
      
      forward = getNifty ().getCurrentScreen ()
              .findNiftyControl ("forwardBtn", Button.class);
      back = getNifty ().getCurrentScreen ()
              .findNiftyControl ("backBtn", Button.class);
      left = getNifty ().getCurrentScreen ()
              .findNiftyControl ("leftBtn", Button.class);
      right = getNifty ().getCurrentScreen ()
              .findNiftyControl ("rightBtn", Button.class);

      if (forward.hasFocus ())
      {
        forward.setText (input);
        this.settings.putString (PlayerState.FORWARD, eventString);
      }
      if (back.hasFocus ())
      {
        back.setText (input);
        this.settings.putString (PlayerState.BACKWARD, eventString);
      }
      if (left.hasFocus ())
      {
        left.setText (input);
        this.settings.putString (PlayerState.LEFT, eventString);
      }
      if (right.hasFocus ())
      {
        right.setText (input);
        this.settings.putString (PlayerState.RIGHT, eventString);
      }
    }
    else
    {
    }
  }
  
  /**
   * Called when the slider "screenWidth" is changed.
   * 
   * @param id the id of the slider.
   * @param event the event change for the slider.
   */
  @NiftyEventSubscriber(id="screenWidth")
  public void changeWidth (final String id, final SliderChangedEvent event)
  {
    TextField screenWidthText;
    int value;
    screenWidthText = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenWidthText", TextField.class);
    value = (int) (event.getValue () * MAX_WIDTH / PERCENT);

    if (!screenWidthText.hasFocus ())
    {
      screenWidthText.setText (String.valueOf (value));
    }
  }
  
  /**
   * Called when the slider "screenHeight" is changed.
   * 
   * @param id the id of the slider.
   * @param event the event change for the slider.
   */
  @NiftyEventSubscriber(id="screenHeight")
  public void changeHeight (final String id, final SliderChangedEvent event)
  {
    TextField screenHeightText;
    int value;
    screenHeightText = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenHeightText", TextField.class);
    value = (int) (event.getValue () * MAX_HEIGHT / PERCENT);

    if (!screenHeightText.hasFocus ())
    {
      screenHeightText.setText (String.valueOf (value));
    }
  }
  
  /**
   * Called to try and change the position of the slider based on the value of
   * the text box next to it.
   */
  public void changeWidth ()
  {
    Slider screenWidth;
    TextField screenWidthText;
    float value;
    
    screenWidth = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenWidth", Slider.class);
    screenWidthText = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenWidthText", TextField.class);
    try
    {
      value = Integer.valueOf (screenWidthText.getText ()) / 
              (float) MAX_WIDTH * PERCENT;
      
      if (!screenWidthText.hasFocus () && value > MAX_WIDTH)
      {
        value = MAX_WIDTH;
        screenWidthText.setText (String.valueOf (value));
      }

      screenWidth.setValue (value);
    }
    catch (NumberFormatException exception)
    {
      LOGGER.log (Level.WARNING, "Text was not a valid integer", exception);
    }
  }
  
  /**
   * Called to try and change the position of the slider based on the value of
   * the text box next to it.
   */
  public void changeHeight ()
  {
    Slider screenHeight;
    TextField screenHeightText;
    float value;
    
    screenHeight = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenHeight", Slider.class);
    screenHeightText = getNifty ().getCurrentScreen ()
            .findNiftyControl ("screenHeightText", TextField.class);
    try
    {
      value = Integer.valueOf (screenHeightText.getText ()) / 
              (float) MAX_HEIGHT * PERCENT;
      
      if (!screenHeightText.hasFocus () && value > MAX_HEIGHT)
      {
        value = MAX_HEIGHT;
        screenHeightText.setText (String.valueOf (value));
      }

      screenHeight.setValue (value);
    }
    catch (NumberFormatException exception)
    {
      LOGGER.log (Level.WARNING, "Text was not a valid integer", exception);
    }
  }
  
  /**
   * Change the given settings into the closest full screen mode.
   * 
   * @param settings the settings to change to full screen mode.
   */
  public void toggleToFullscreen(AppSettings settings) 
  {
    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
    DisplayMode[] modes = device.getDisplayModes ();
    DisplayMode closestMode = modes[0];
    int prevWidthDiff = Math.abs (settings.getWidth () - 
            closestMode.getWidth ());
    int prevHeightDiff = Math.abs (settings.getHeight () - 
            closestMode.getHeight ());    
    int widthDiff;
    int heightDiff;
    
    for (DisplayMode mode : modes)
    {
      widthDiff = Math.abs (settings.getWidth () - mode.getWidth ());
      heightDiff = Math.abs (settings.getHeight () - mode.getHeight ());
      if (widthDiff + heightDiff < prevWidthDiff + prevHeightDiff)
      {
        prevWidthDiff = widthDiff;
        prevHeightDiff = heightDiff;
        closestMode = mode;
      }
    }
    
    settings.setResolution(closestMode.getWidth(), closestMode.getHeight());
    settings.setFrequency(closestMode.getRefreshRate());
    settings.setFullscreen(device.isFullScreenSupported());
  }

  /**
   * Called when a key is pressed.
   * 
   * @param evt the event for the key press.
   */
  public void onKeyEvent (KeyInputEvent evt)
  {
    updateKeys (evt);
  }

  // The following are empty and required for the key input listening.
  public void beginInput ()
  {
    
  }

  public void endInput ()
  {
    
  }

  public void onJoyAxisEvent (JoyAxisEvent evt)
  {
    
  }

  public void onJoyButtonEvent (JoyButtonEvent evt)
  {
    
  }

  public void onMouseMotionEvent (MouseMotionEvent evt)
  {
    
  }

  public void onMouseButtonEvent (MouseButtonEvent evt)
  {
    
  }

  public void onTouchEvent (TouchEvent evt)
  {
    
  }
}
