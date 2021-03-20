package halemaster.ee;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.KeyInput;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import halemaster.ee.ai.AIManager;
import halemaster.ee.ai.AutoAttackAI;
import halemaster.ee.ai.MovementAI;
import halemaster.ee.ai.OfferAI;
import halemaster.ee.ai.ShopAI;
import halemaster.ee.ai.ThreatAI;
import halemaster.ee.ai.WanderAI;
import halemaster.ee.localization.Localizer;
import halemaster.ee.state.PlayerState;
import halemaster.ee.state.start.MenuSplash;
import halemaster.ee.world.Area;
import halemaster.ee.world.BiomeClassifier;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.imageio.ImageIO;

/**
 * @name Game
 * 
 * @version 0.0.0
 * 
 * @date 2013-10-12
 */
@SuppressWarnings ("ClassWithMultipleLoggers")
public class Game extends SimpleApplication
{
  public static final Logger LOGGER_ALL = Logger.getLogger ("");
  public static final Logger LOGGER = Logger.getLogger (Game.class.getName ());
  public static final String DEBUG = "--debug";
  public static final int MAX_ADD = 1000;
  public static final int MAX_REMOVE = 500;
  public static final String GAME_FOLDER = "/.endlessExpanse";
  public static final String WORLD_FOLDER = "/worlds";
  public static final String PLAYER_FOLDER = "/players";
  public static final String SCREENSHOTS = "/screenshots/";
  public static final String BIOME_FOLDER = "assets/Biomes";
  public static final String VERSION = "0.1.0";
  public static final String SETTINGS = "halemaster.ee.Settings";
  public static final String MOVEMENT_AI = "movement";
  public static final String WANDER_AI = "wander";
  public static final String QUEST_AI = "quest";
  public static final String AUTO_ATTACK_AI = "autoAttack";
  public static final String THREAT_AI = "threat";
  public static final String SHOP_AI = "shop";
  public static final String HOME = 
          JmeSystem.getStorageFolder ().getAbsolutePath ();
  private ConcurrentLinkedQueue<Sprite> attachSprites = 
          new ConcurrentLinkedQueue<Sprite>();
  private ConcurrentLinkedQueue<Sprite> detachSprites = 
          new ConcurrentLinkedQueue<Sprite>();
  private ConcurrentHashMap<Sprite, Area> attachMap =
          new ConcurrentHashMap<Sprite, Area> ();
  private ConcurrentHashMap<Sprite, Area> detachMap =
          new ConcurrentHashMap<Sprite, Area> ();
  private Map<Area, Node> spriteNodes = new HashMap<Area, Node> ();
  private Set<Sprite> currentlyAttached = new HashSet<Sprite>();
  private Set<Overlay> overlays = new HashSet<Overlay> ();
  private Node overlayNode = new Node ();
  
  /**
   * Main class for the entire game. Runs on startup.
   * 
   * @param args Arguments passed to the game.
   */
  public static void main (String[] args)
  {
    Game application;
    AppSettings settings = new AppSettings (true);
    boolean debug = false;
    
    for (String arg : args)
    {
      if (DEBUG.equals (arg))
      {
        debug = true;
      }
    }
    
    application = new Game (debug);
         
    settings.putBoolean (PlayerState.CLICK_NAME, true);
    settings.putString (PlayerState.FORWARD, "W" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_W);
    settings.putString (PlayerState.BACKWARD, "S" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_S);
    settings.putString (PlayerState.LEFT, "A" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_A);
    settings.putString (PlayerState.RIGHT, "D" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_D);
    settings.putString (PlayerState.HOTKEYS[1], "1" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_1);
    settings.putString (PlayerState.HOTKEYS[2], "2" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_2);
    settings.putString (PlayerState.HOTKEYS[3], "3" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_3);
    settings.putString (PlayerState.HOTKEYS[4], "4" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_4);
    settings.putString (PlayerState.HOTKEYS[5], "5" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_5);
    settings.putString (PlayerState.HOTKEYS[6], "6" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_6);
    settings.putString (PlayerState.HOTKEYS[7], "7" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_7);
    settings.putString (PlayerState.HOTKEYS[8], "8" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_8);
    settings.putString (PlayerState.HOTKEYS[9], "9" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_9);
    settings.putString (PlayerState.HOTKEYS[0], "0" + 
            PlayerState.EVENT_SPLITTER + KeyInput.KEY_0);
    
    try
    {
      settings.load(Game.SETTINGS);
    }
    catch (BackingStoreException exception)
    {
      LOGGER.log (Level.WARNING, "Failed to load settings", exception);
    }
    
    settings.setTitle(Localizer.getString ("game.title"));
    try
    {
      settings.setIcons (new BufferedImage[] {ImageIO.read (new File 
            ("assets/Interface/Images/TEEIcon.png"))});
    }
    catch (IOException e)
    {
      LOGGER.log (Level.WARNING, "Couldn't Load Icon", e);
    }
    application.setSettings (settings);
    
    AIManager.registerAI (MOVEMENT_AI, MovementAI.class);
    AIManager.registerAI (WANDER_AI, WanderAI.class);
    AIManager.registerAI (QUEST_AI, OfferAI.class);
    AIManager.registerAI (AUTO_ATTACK_AI, AutoAttackAI.class);
    AIManager.registerAI (THREAT_AI, ThreatAI.class);
    AIManager.registerAI (SHOP_AI, ShopAI.class);
    
    application.start ();
  }
  
  public Game ()
  {
    this (false);
  }
  
  public Game (boolean debugInitial)
  {
    setDebug (debugInitial);    
  }

  /**
   * Initialization of the game. Get rid of default debug info.
   */
  @Override
  public void simpleInitApp ()
  {
    AppSettings apps = this.settings;
    File biomeFolder = new File (BIOME_FOLDER);
    
    this.flyCam.setEnabled (false);
    
    apps.setRenderer (AppSettings.LWJGL_OPENGL_ANY);
    apps.setEmulateMouse (true);
    apps.setEmulateMouseFlipAxis (false, false);
    
    setSettings (apps);
    restart ();
    
    new File (HOME + GAME_FOLDER + SCREENSHOTS).mkdirs ();
    ScreenshotAppState screenShotState = new ScreenshotAppState();
    screenShotState.setFilePath (HOME + GAME_FOLDER + SCREENSHOTS);
    addAppState(screenShotState);
    
    this.rootNode.attachChild (this.overlayNode);
    
    try
    {
      for (File biome : biomeFolder.listFiles ())
      {
        BiomeClassifier.loadBiomes (biome);
      }

      addAppState (new MenuSplash ());
    }
    catch (IOException e)
    {
      handleError ("Could not load Biomes. Make sure they are in the correct"
              + "package!", e);
    }
  }
  
  @Override
  public void simpleUpdate (float tpf)
  {
    updateSprites ();
    for (Sprite runner : this.currentlyAttached)
    {
      runner.update (tpf);
    }
    
    // must make copy because overlays remove themselves during update!
    Set<Overlay> overlayList = new HashSet<Overlay> ();
    for (Overlay overlay : this.overlays)
    {
      overlayList.add (overlay);
    }
    for (Overlay overlay : overlayList)
    {
      overlay.update (tpf);
    }
  }
  
  public void updateSprites ()
  {
    Sprite sprite;
    Area location;
    Node node;
    
    int addSize = Math.min (MAX_ADD, this.attachSprites.size ());
    int removeSize = Math.min (MAX_REMOVE, this.detachSprites.size ());
    
    for (int i = 0; i < addSize; i++)
    {
      sprite = this.attachSprites.poll ();
      location = this.attachMap.remove (sprite);
      node = this.spriteNodes.get (location);
      if (null == node)
      {
        node = new Node ();
        this.spriteNodes.put (location, node);
        this.rootNode.attachChild (node);
      }
      sprite.attachTo (node);
      this.currentlyAttached.add (sprite);
    }
    for (int i = 0; i < removeSize; i++)
    {
      sprite = this.detachSprites.poll ();
      location = this.detachMap.remove (sprite);
      node = this.spriteNodes.get (location);
      if (null != node)
      {
        sprite.detachFrom (node);
        if (node.getChildren ().size () <= 0)
        {
          this.spriteNodes.remove (location);
          this.rootNode.detachChild (node);
        }
      }
      this.currentlyAttached.remove (sprite);
    }
  }
  
  public Node getSpriteNode (Area area)
  {
    return this.spriteNodes.get (area);
  }
  
  /**
   * Attach the given sprite at the earliest convenience.
   * 
   * @param sprite sprite to attach
   */
  public void attachSprite (Sprite sprite, Area area)
  {
    this.attachSprites.add (sprite);
    this.attachMap.put (sprite, area);
  }
  
  /**
   * Detach the given sprite from the root at the earliest possible time
   * 
   * @param sprite sprite to detach
   */
  public void detachSprite (Sprite sprite, Area area)
  {
    this.detachSprites.add (sprite);
    this.detachMap.put (sprite, area);
  }
  
  /**
   * Handles any errors that are thrown to the Game.
   * 
   * @param errorMessage Message thrown to the Game.
   * @param thrown Throwable that was thrown to the Game.
   */
  @Override
  public void handleError (String errorMessage, Throwable thrown)
  {
    LOGGER.log (Level.SEVERE, errorMessage, thrown);
    stop ();
  }
  
  /**
   * Turn Debugging on or off.
   * 
   * @param debug what to turn debugging to.
   */
  public final void setDebug (boolean debug)
  {
    setDisplayFps (debug);
    setDisplayStatView (debug);
    setShowSettings (debug);
    
    if (debug)
    {
      LOGGER_ALL.setLevel (Level.ALL);
    }
    else
    {
      LOGGER_ALL.setLevel (Level.SEVERE);
    }
  }
  
  /**
   * Add the given app state to the game.
   * 
   * @param state the state to add.
   */
  public void addAppState (AppState state)
  {
    this.stateManager.attach (state);
  }
  
  /**
   * remove the given app state from the game.
   * 
   * @param state the state to remove.
   * @return whether the state was removed successfully.
   */
  public boolean removeAppState (AppState state)
  {
    return this.stateManager.detach (state);
  }
  
  public AppSettings getSettings ()
  {
    return this.settings;
  }
  
  public void addOverlay (Overlay overlay)
  {
    overlay.getSprite ().attachTo (this.overlayNode);
    if (null != overlay.getSound ())
    {
      this.overlayNode.attachChild (overlay.getSound ());
    }
    this.overlays.add (overlay);
  }
  
  public void removeOverlay (Overlay overlay)
  {
    this.overlays.remove (overlay);
    if (null != overlay.getSound ())
    {
      this.overlayNode.detachChild (overlay.getSound ());
    }
    overlay.getSprite ().detachFrom (this.overlayNode);
  }
}
