package halemaster.ee.state;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import halemaster.ee.Game;
import halemaster.ee.Json;
import halemaster.ee.Sprite;
import halemaster.ee.ai.AutoAttackAI;
import halemaster.ee.ai.MovementAI;
import halemaster.ee.item.Inventory;
import halemaster.ee.item.InventoryListener;
import halemaster.ee.item.Item;
import halemaster.ee.item.ItemSet;
import halemaster.ee.item.Reward;
import halemaster.ee.localization.Localizer;
import halemaster.ee.quest.Offer;
import halemaster.ee.quest.Quest;
import halemaster.ee.world.Area;
import halemaster.ee.world.Biome;
import halemaster.ee.world.BiomeClassifier;
import halemaster.ee.world.Config;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityType;
import halemaster.ee.world.entity.KillListener;
import halemaster.ee.world.entity.Statistic;
import halemaster.ee.world.history.event.EventHolder;
import halemaster.ee.world.history.event.EventIO;
import halemaster.ee.world.history.event.type.EventType;
import halemaster.ee.world.history.event.type.EventTypeHolder;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import halemaster.ee.world.micro.AreaGenerator;
import halemaster.ee.world.micro.WorldHolder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name PlayerState
 * 
 * @version 0.0.0
 * 
 * @date Oct 16, 2013
 */
public class PlayerState extends AbstractAppState implements ActionListener, 
        AnalogListener, KillListener, InventoryListener
{
  public enum PLAYER_IMAGE
  {
    KNIGHT_M ("knight", "m"),
    KNIGHT_F ("knight", "f"),
    PRIEST_M ("priest", "m"),
    PRIEST_F ("priest", "f"),
    ROGUE_M ("rogue", "m"),
    ROGUE_F ("rogue", "f"),
    ARCHER_M ("archer", "m"),
    ARCHER_F ("archer", "f"),
    MAGE_M ("mage", "m"),
    MAGE_F ("mage", "f");
    
    private Map<String, String[]> images;
    
    private PLAYER_IMAGE (String images, String gender)
    {
      String[] im;
      this.images = new HashMap<String,String[]>();
      im = new String[]{"Textures/entities/" + images + "/" + images + "_" + 
              gender + "_" + Entity.CHANNEL_LEFT + "_1.png","Textures/entities/"
              + images + "/" + images + "_" + gender + "_" + Entity.CHANNEL_LEFT
              + "_2.png"};
      this.images.put (Entity.CHANNEL_LEFT, im);
      im = new String[]{"Textures/entities/" + images + "/" + images + "_" + 
              gender + "_" + Entity.CHANNEL_RIGHT + 
              "_1.png","Textures/entities/" + images + "/" + images + "_" + 
              gender + "_" + Entity.CHANNEL_RIGHT + "_2.png"};
      this.images.put (Entity.CHANNEL_RIGHT, im);
    }
    
    public Map<String, String[]> getImages ()
    {
      return this.images;
    }
  }
  
  public static final Logger LOGGER = Logger.getLogger 
          (PlayerState.class.getName ());
  public static final String CLICK_NAME = "Player_SelectSpace";
  public static final String FRIENDLY_CHAT = "game.hud.chat.friendly";
  public static final String AREA = "/worlds";
  public static final String PLAYER_IMAGE_FILE = "image";
  public static final String PLAYER_INV = "inventory";
  public static final String PLAYER_SKILLS = "hotbar";
  public static final float PLAYER_SPEED = 0.75f;
  public static final String LOCATION_FOLDER = "/areas";
  public static final String PLAYER_STATS = "stats";
  public static final String FORWARD = "Player_forward_key";
  public static final String BACKWARD = "Player_back_key";
  public static final String LEFT = "Player_left_key";
  public static final String RIGHT = "Player_right_key";
  public static final String[] HOTKEYS = {"Player_hotkey_0",
    "Player_hotkey_1", "Player_hotkey_2", "Player_hotkey_3",
    "Player_hotkey_4", "Player_hotkey_5", "Player_hotkey_6",
    "Player_hotkey_7", "Player_hotkey_8", "Player_hotkey_9"};
  public static final String EVENT_SPLITTER = "\t";
  public static final float STEP_SIZE = AreaGenerator.TILE_HEIGHT;
  public static final float FORWARD_MOVE = -0.5f;
  public static final float BACK_MOVE = 1.5f;
  public static final float ADJUSTMENT = 0.25f;
  private static final float HOLD_AMOUNT = 0.25f;
  private static final float CAMERA_DISTANCE = 12f * Sprite.DEFAULT_SIZE;
  private static final float PIXEL_RATIO = 3.75f;
  private static final int UNSET_STAT = -1;
  private static final int POINT_START = 1;
  private static final int MAX_RESPAWN = 16;
  
  private Game game;
  private CameraNode playerCam;
  private float held = 0f;
  private String worldName; // this and player name are used to load game.
  private String playerName;
  private WorldHolder world;
  private Entity player; 
  private int playerX = Area.ANYWHERE.getX ();
  private int playerY = Area.ANYWHERE.getY ();
  private int playerSubX = 0;
  private int playerSubY = 0;
  private boolean clickMove = false;
  private boolean canInterrupt = false;
  private String playerImage = null;
  private Hud hud;
  private Map<String, Quest> questLog = new HashMap<String, Quest>();
  private boolean questChanged = true;
  private int playerLevel = UNSET_STAT, playerXp = UNSET_STAT, 
          playerHp = UNSET_STAT, playerPoints = UNSET_STAT, 
          playerPower = UNSET_STAT, playerAgility = UNSET_STAT, 
          playerIntellect = UNSET_STAT;
  private Inventory playerInventory = null;
  private Item[] skills = null;
  private boolean hasDied = false;
  private int previousEffects = 0;
  
  public PlayerState (String worldName, String playerName)
  {
    this.worldName = worldName;
    this.playerName = playerName;
  }
  
  /**
   * initialize this AppState.
   * 
   * @param manager State Manager that this is being attached to.
   * @param app Application that this is being attached to.
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    EventTypeHolder types;
    EventHolder events;
    Biome[][] biomes;
    EventType[] typeArr;
    File overTypeFolder = 
            new File (EventIO.EVENT_FOLDER + EventIO.GLOBAL_FOLDER);
    File typeFolder = new File (EventIO.EVENT_FOLDER + EventIO.LOCAL_FOLDER);
    String spawnArea;
    
    this.game = (Game) app;
    
    this.game.getInputManager ().addMapping (CLICK_NAME, 
              new MouseButtonTrigger (MouseInput.BUTTON_LEFT));
        this.game.getInputManager ().addListener (this, CLICK_NAME);
    setClickToMove (this.game.getSettings ().getBoolean (CLICK_NAME));
    setKeys (true);
    
    try
    {
      types = new EventTypeHolder ();
      
      for (File typeLocation : typeFolder.listFiles ())
      {
        typeArr = EventIO.getTypes (typeLocation);
        for (EventType type : typeArr)
        {
          types.addType (type);
        }
      }
      
      for (File overTypeLocation : overTypeFolder.listFiles ())
      {
        typeArr = EventIO.getTypes (overTypeLocation);
        for (EventType type : typeArr)
        {
          types.addType (type);
        }
      }
      
      biomes = loadBiomes ();
      events = new EventHolder (this.worldName, biomes);
      events = EventIO.getEvents (worldName, events, Area.ANYWHERE.getX (), 
              Area.ANYWHERE.getX (), Area.ANYWHERE.getY (), 
              Area.ANYWHERE.getY (), types);
      
      types = new EventTypeHolder ();
      
      for (File typeLocation : typeFolder.listFiles ())
      {
        typeArr = EventIO.getTypes (typeLocation);
        for (EventType type : typeArr)
        {
          types.addType (type);
        }
      }
      
      spawnArea = Config.getValue (this.worldName,
              MacroTerrainGenerator.SPAWN_POINT);
      loadPlayer ();
      if (Area.ANYWHERE.getX () == this.playerX ||
              Area.ANYWHERE.getY () == this.playerY)
      {
        this.playerX = Integer.valueOf (spawnArea.split (",")[0]);
        this.playerY = Integer.valueOf (spawnArea.split (",")[1]);
      }
      this.world = new WorldHolder (this, types, events);
      
      this.player = new Entity ();
      Map<String, String[]> images = new HashMap<String, String[]>();
      for (Entry<String, String[]> image : PLAYER_IMAGE.valueOf 
              (this.playerImage).getImages ().entrySet ())
      {
        images.put (image.getKey (), image.getValue ());
      }
      this.player.setImages (images);      
      this.player.setAnimation (Entity.CHANNEL_LEFT);
      this.player.setLocation (new Area (this.playerX, this.playerY));
      this.player.setName (this.playerName);
      this.player.setSpeed (PLAYER_SPEED);
      this.player.setType (EntityType.PLAYER.getId ());
      this.player.setX (this.playerSubX);
      this.player.setY (this.playerSubY);
      this.player.addAI (Game.MOVEMENT_AI);
      this.player.addAI (Game.AUTO_ATTACK_AI);
      this.player.addKillListener (this);
      if (UNSET_STAT != this.playerLevel)
      {
        this.player.setLevel (this.playerLevel);
      }
      if (UNSET_STAT != this.playerXp)
      {
        this.player.setXp (this.playerXp);
      }
      if (UNSET_STAT != this.playerPoints)
      {
        this.player.setStatPoints (this.playerPoints);
      }
      else
      {
        this.player.setStatPoints (POINT_START);
      }
      if (UNSET_STAT != this.playerPower)
      {
        this.player.setPower (this.playerPower);
      }
      if (UNSET_STAT != this.playerAgility)
      {
        this.player.setAgility (this.playerAgility);
      }
      if (UNSET_STAT != this.playerIntellect)
      {
        this.player.setIntellect (this.playerIntellect);
      }
      if (UNSET_STAT != this.playerHp)
      {
        this.player.setCurrentHp (this.playerHp);
      }
      else
      {
        this.player.setCurrentHp (this.player.getStat (Statistic.HEALTH));
      }
      if (null != this.playerInventory)
      {
        this.player.setInventory (this.playerInventory);
      }
      else
      {
        this.player.getInventory ().addMoney (50);
      }
      
      this.player.getInventory ().addListener (this);
      
      this.world.setPlayer (this.player);
      this.world.getEntityManager ().addEntity (this.player);
      this.game.updateSprites ();

      this.playerCam = new CameraNode("Player Camera", this.game.getCamera ());
      this.playerCam.setControlDir(ControlDirection.SpatialToCamera);
      this.game.getRootNode ().attachChild (this.playerCam);
      this.playerCam.setLocalTranslation(new Vector3f(
              this.player.getAbsoluteLocation ().x, CAMERA_DISTANCE, 
              this.player.getAbsoluteLocation ().y));
      this.playerCam.lookAt(this.player.getSprite ().getImage ()
              .getLocalTranslation(), Vector3f.UNIT_Y);
      this.playerCam.rotate (0, 0, FastMath.PI);
      
      List<Quest> quests = Quest.loadFrom (this.playerName, this.worldName);
      for (Quest quest : quests)
      {
        this.questLog.put (quest.getOfferId (), quest);
      }
      
      Thread worldThread = new Thread (this.world);
      worldThread.setName ("TEE-world");
      worldThread.setPriority (Thread.MIN_PRIORITY);
      worldThread.start ();
    }
    catch (IOException e)
    {
      this.game.handleError ("Failure to load events", e);
    }
    catch (InstantiationException e)
    {
      this.game.handleError ("Failure to load events", e);
    }
    catch (IllegalAccessException e)
    {
      this.game.handleError ("Failure to load events", e);
    }
    
    super.initialize (manager, app);
  }

  public Hud getHud ()
  {
    return hud;
  }

  public void setHud (Hud hud)
  {
    this.hud = hud;
  }
  
  /**
   * Load the biomes of all the world
   * @return biome array
   */
  private Biome[][] loadBiomes ()
  {
    File biomeFile = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + this.worldName + "/" + MacroTerrainGenerator.TERRAIN_FILE);
    int size;
    Biome[][] biomes = null;
    BufferedReader reader;
    
    try
    {
      size = Integer.valueOf (Config.getValue (this.worldName, 
            MacroTerrainGenerator.SIZE_KEY));
      biomes = new Biome[size][size];
      reader = new BufferedReader (new FileReader (biomeFile));
      
      for (int i = 0; i < biomes.length; i++)
      {
        for (int j = 0; j < biomes[i].length; j++)
        {
          biomes[i][j] = BiomeClassifier.getBiome (reader.readLine ());
        }
      }
      
      reader.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not load biomes!", e);
    }
    
    return biomes;
  }
  
  /**
   * cleanup this AppState.
   */
  @Override
  public void cleanup ()
  {
    savePlayer ();
    this.world.getEntityManager ().removeEntity (this.player);
    this.game.getRootNode ().detachChild (this.playerCam);
    this.game.getInputManager ().removeListener (this);
    
    this.game.getInputManager ().deleteMapping (CLICK_NAME);
    
    this.playerCam = null;
    this.world.stop ();
    
    Offer.clearOffers ();
    
    for (Quest quest : this.questLog.values ())
    {
      Quest.saveQuest (quest);
    }
    
    super.cleanup ();
  }
  
  /**
   * Load the player from file
   */
  public void loadPlayer ()
  {
    File worldFile = new File (Game.HOME + Game.GAME_FOLDER + 
            Game.PLAYER_FOLDER + "/" + this.playerName + AREA + "/" +
            this.worldName);
    File image = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER + 
            "/" + this.playerName + "/" + PLAYER_IMAGE_FILE);
    File stats = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER +
            "/" + this.playerName + "/" + PLAYER_STATS);
    File inventory = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER +
            "/" + this.playerName + "/" + PLAYER_INV);
    File skill = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER +
            "/" + this.playerName + "/" + PLAYER_SKILLS);
    BufferedReader reader;
    String location;
    
    try
    {
      reader = new BufferedReader (new FileReader (worldFile));
      
      location = reader.readLine ();
      
      if (null != location)
      {
        this.playerX = Integer.valueOf (location.split (",")[0]);
        this.playerY = Integer.valueOf (location.split (",")[1]);
      }
      
      location = reader.readLine ();
      
      if (null != location)
      {
        this.playerSubX = Integer.valueOf (location.split (",")[0]);
        this.playerSubY = Integer.valueOf (location.split (",")[1]);
      }
      
      reader.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not load player", e);
    }
    
    try
    {
      reader = new BufferedReader (new FileReader (image));
      
      this.playerImage = reader.readLine ();
      
      reader.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not load player", e);
    }
    
    try
    {
      reader = new BufferedReader (new FileReader (stats));
      
      this.playerLevel = Integer.valueOf (reader.readLine ());
      this.playerXp = Integer.valueOf (reader.readLine ());
      this.playerHp = Integer.valueOf (reader.readLine ());
      this.playerPoints = Integer.valueOf (reader.readLine ());
      this.playerPower = Integer.valueOf (reader.readLine ());
      this.playerAgility = Integer.valueOf (reader.readLine ());
      this.playerIntellect = Integer.valueOf (reader.readLine ());
      
      reader.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not load player", e);
    }
    
    try
    {
      this.playerInventory = Json.getFromFile (inventory, Inventory.class)[0];
      this.skills = Json.getFromFile (skill, Item.class);
      for (int i = 0; i < this.skills.length; i++)
      {
        if (this.skills[i].getName () == null)
        {
          this.skills[i] = null;
        }
      }
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not load player", e);
    }
  }
  
  /**
   * Save the player to file
   */
  public void savePlayer ()
  {
    File worldFile = new File (Game.HOME + Game.GAME_FOLDER +
            Game.PLAYER_FOLDER + "/" + this.playerName + AREA + "/" + 
            this.worldName);
    File stats = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER +
            "/" + this.playerName + "/" + PLAYER_STATS);
    File inventory = new File (Game.HOME + Game.GAME_FOLDER +
            Game.PLAYER_FOLDER + "/" + this.playerName + "/" + PLAYER_INV);
    File skill = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER +
            "/" + this.playerName + "/" + PLAYER_SKILLS);
    BufferedWriter writer;
    
    try
    {
      File folder = new File (Game.HOME + Game.GAME_FOLDER + Game.PLAYER_FOLDER 
              + "/" + this.playerName + AREA);
      folder.mkdir ();
      worldFile.createNewFile ();
      writer = new BufferedWriter (new FileWriter (worldFile));
      
      writer.write (this.player.getLocation ().toString () + "\n");
      writer.write (this.player.getX () + "," + this.player.getY ());
      
      writer.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not save player", e);
    }
    
    try
    {
      writer = new BufferedWriter (new FileWriter (stats));
      
      writer.write (String.valueOf (this.player.getLevel ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getXp ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getCurrentHp ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getStatPoints ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getPower ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getAgility ()));
      writer.newLine ();
      writer.write (String.valueOf (this.player.getIntellect ()));
      writer.newLine ();
      
      writer.close ();
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not save player", e);
    }
    
    try
    {
      Json.saveJson (inventory, this.player.getInventory ());
      Json.saveJson (skill, this.hud.getSkills ());
    }
    catch (IOException e)
    {
      LOGGER.log (Level.SEVERE, "Could not save player", e);
    }
  }
  
  /**
   * Set the movement keys to on or off.
   * 
   * @param on whether to turn keys on or off.
   */
  public void setKeys (boolean on)
  {
    try
    {
      if (on)
      {
        this.game.getInputManager ().addMapping (FORWARD, 
              new KeyTrigger (Integer.valueOf(this.game.getSettings ()
                .getString (FORWARD).split (EVENT_SPLITTER)[1])));
        this.game.getInputManager ().addListener (this, FORWARD);
        this.game.getInputManager ().addMapping (BACKWARD, 
              new KeyTrigger (Integer.valueOf(this.game.getSettings ()
                .getString (BACKWARD).split (EVENT_SPLITTER)[1])));
        this.game.getInputManager ().addListener (this, BACKWARD);
        this.game.getInputManager ().addMapping (LEFT, 
              new KeyTrigger (Integer.valueOf(this.game.getSettings ()
                .getString (LEFT).split (EVENT_SPLITTER)[1])));
        this.game.getInputManager ().addListener (this, LEFT);
        this.game.getInputManager ().addMapping (RIGHT, 
              new KeyTrigger (Integer.valueOf(this.game.getSettings ()
                .getString (RIGHT).split (EVENT_SPLITTER)[1])));
        this.game.getInputManager ().addListener (this, RIGHT);
        for (int i = 0; i < HOTKEYS.length; i++)
        {
          this.game.getInputManager ().addMapping (HOTKEYS[i], 
                new KeyTrigger (Integer.valueOf(this.game.getSettings ()
                  .getString (HOTKEYS[i]).split (EVENT_SPLITTER)[1])));
          this.game.getInputManager ().addListener (this, HOTKEYS[i]);
        }
      }
      else
      {
        this.game.getInputManager ().deleteMapping (FORWARD);
        this.game.getInputManager ().deleteMapping (BACKWARD);
        this.game.getInputManager ().deleteMapping (LEFT);
        this.game.getInputManager ().deleteMapping (RIGHT);
        for (int i = 0; i < HOTKEYS.length; i++)
        {
          this.game.getInputManager ().deleteMapping (HOTKEYS[i]);
        }
      }
    }
    catch (NullPointerException exception)
    {
      LOGGER.log (Level.FINE, "Tried to delete Non-existant", exception);
    }
  }
  
  public void setClickToMove (boolean move)
  {
    this.clickMove = move;
  }
  
  /**
   * Pause/unpause this State.
   * 
   * @param enable whether to enable this state.
   */
  @Override
  public void setEnabled (boolean enable)
  {
    this.playerCam.setEnabled (enable);
    
    super.setEnabled (enable);
  }
  
  /**
   * update anything that is controlled by this state.
   * 
   * @param tpf the delta since the previous call to update.
   */
  @Override
  public void update (float tpf)
  {
    if (isEnabled ())
    {
      this.world.update (tpf);
      this.playerCam.setLocalTranslation (
              this.player.getAbsoluteLocation ().x, 
              this.playerCam.getLocalTranslation ().y, 
              this.player.getAbsoluteLocation ().y);
      if (this.player.getEffects ().length != this.previousEffects)
      {
        this.hud.changed ();
        this.previousEffects = this.player.getEffects ().length;
      }
      if (!this.player.getHitBy ().isEmpty () && null == this.hud.getTarget ())
      {
        Iterator<Entity> hit = this.player.getHitBy ().keySet ().iterator ();
        while (hit.hasNext () && null == this.hud.getTarget ())
        {
          Entity ent = hit.next ();
          if (!ent.isDead ())
          {
            clickEntity (ent);
          }
        }
      }
      if (this.player.isDead () && !this.hasDied)
      {
        Random random = new Random ();
        this.player.getInventory ().spendMoney (this.player.getInventory ()
                .getMoney () / 2);
        this.player.move (random.nextInt (MAX_RESPAWN), 
                random.nextInt (MAX_RESPAWN));
        this.player.setDeadTime (Entity.RESPAWN_TIME - 1);
        this.hasDied = true;
      }
      if (!this.player.isDead ())
      {
        this.hasDied = false;
      }
      
      this.hud.update ();
      
      if (null != this.skills)
      {
        for (int i = 0; i < this.skills.length; i++)
        {
          if (null != this.skills[i])
          {
            this.hud.setSkill (i, this.skills[i]);
          }
        }
        
        this.skills = null;
      }
    }
    
    super.update (tpf);
  }
  
  /**
   * Called when an analog action that is registered with this state happens.
   * 
   * @param action name of action called.
   * @param strength strength of the input signal, or tpf for boolean triggers.
   * @param tpf the time since the last call of this action.
   */
  public void onAnalog (String action, float strength, float tpf)
  {
    MovementAI move;
    Area playerLoc = this.player.getExactLocation ();
    
    if (isEnabled ())
    {
      move = (MovementAI) this.player.getAI (Game.MOVEMENT_AI);
      if (CLICK_NAME.equals (action))
      {
        this.held += tpf;
      }
      else if (FORWARD.equals (action))
      {
        if (null == move.getMoveTo () || this.canInterrupt)
        {
          move.setMoveTo (new Area (playerLoc.getX (), playerLoc.getY () - 1));
          this.canInterrupt = false;
        }
      }
      else if (BACKWARD.equals (action))
      {
        if (null == move.getMoveTo () || this.canInterrupt)
        {
          move.setMoveTo (new Area (playerLoc.getX (), playerLoc.getY () + 1));
          this.canInterrupt = false;
        }
      }
      else if (LEFT.equals (action))
      {
        if (null == move.getMoveTo () || this.canInterrupt)
        {
          move.setMoveTo (new Area (playerLoc.getX () - 1, playerLoc.getY ()));
          this.canInterrupt = false;
        }
      }
      else if (RIGHT.equals (action))
      {
        if (null == move.getMoveTo () || this.canInterrupt)
        {
          move.setMoveTo (new Area (playerLoc.getX () + 1, playerLoc.getY ()));
          this.canInterrupt = false;
        }
      }
      else
      {
        for (int i = 0; i < HOTKEYS.length; i++)
        {
          if (HOTKEYS[i].equals (action))
          {
            this.hud.skill (String.valueOf(i));
          }
        }
      }
    }
  }
  /**
   * Called when a boolean action that is registered with this state happens.
   * 
   * @param action name of action called.
   * @param clicked false if the action is ongoing.
   * @param tpf the time since the last call of this action.
   */
  public void onAction (String action, boolean clicked, float tpf)
  {
    MovementAI move;
    float pixelsPer;
    float mouseX, mouseY;
    float relX, relY;
    float width, height;
    int boxX, boxY;
    int clickX, clickY;
    
    if (isEnabled ())
    {
      move = (MovementAI) this.player.getAI (Game.MOVEMENT_AI);
      if (CLICK_NAME.equals (action) && !clicked)
      {
        if (this.held < HOLD_AMOUNT)
        {          
          pixelsPer = CAMERA_DISTANCE / PIXEL_RATIO;
          mouseX = this.game.getInputManager ().getCursorPosition ().x;
          mouseY = this.game.getInputManager ().getCursorPosition ().y;
          width = this.game.getRenderManager ().getCurrentCamera ().getWidth ()
                  + AreaGenerator.TILE_WIDTH;
          height = this.game.getRenderManager ().getCurrentCamera ()
                  .getHeight () + AreaGenerator.TILE_HEIGHT;
          relX = (mouseX - width / 2);
          
          if (relX > 0)
          {
            boxX = (int) Math.ceil (relX / (pixelsPer / 2)) / 2;
            clickX = boxX + this.player.getExactLocation ().getX ();
          }
          else
          {
            boxX = (int) Math.floor (relX / (pixelsPer / 2) / 2);
            clickX = boxX + this.player.getExactLocation ().getX ();
          }
          
          relY = ((height - mouseY) - height / 2);
          
          if (relY > 0)
          {
            boxY = (int) Math.ceil (relY / (pixelsPer / 2)) / 2;
            clickY = boxY + this.player.getExactLocation ().getY ();
          }
          else
          {
            boxY = (int) Math.ceil (relY / (pixelsPer / 2) / 2);
            clickY = boxY + this.player.getExactLocation ().getY ();
          }
          
          if (this.clickMove)
          {
            move.setMoveTo (new Area (clickX, clickY));
            this.canInterrupt = true;
          }
          
          Entity clickedEntity = null;
          List<Entity> possibles = this.world.getEntityManager ()
                      .getEntities (this.player.getLocation ().getX (), 
                      this.player.getLocation ().getY ());
          for (int i = 0; null == clickedEntity && i < possibles.size ();
                  i++)
          {
            if (possibles.get (i).getExactLocation ().getX () == clickX &&
                    possibles.get (i).getExactLocation ().getY () == clickY)
            {
              clickedEntity = possibles.get (i);
            }
          }
          
          clickEntity (clickedEntity);
        }

        this.held = 0f;
      }
    }
  }
  
  public void clickEntity (Entity clickedEntity)
  {
    AutoAttackAI attack;
    this.hud.setTarget (clickedEntity);
    if (null != clickedEntity &&
            clickedEntity.getType ().equals (EntityType.NPC.getId ()))
    {
      String description = Localizer.getString (FRIENDLY_CHAT,
              (Object) this.player.getName ());

      if (null != clickedEntity.getData (Offer.OFFER_CURRENT))
      {
        Offer offer = Offer.getOffer (clickedEntity.getData 
                (Offer.OFFER_CURRENT), clickedEntity);
        if ((-1 == offer.getOfferRate () || offer.getOfferRate () >
                offer.getOfferAmount (this.player.getName ())))
        {
          description = offer.getDescription ();
          if (!this.questLog.containsKey (offer.getOfferId ()))
          {
            Quest newQuest = Quest.newQuest (this.player.getName (), 
                    offer);
            this.questLog.put (offer.getOfferId (), newQuest);
            this.questChanged = true;
          }
        }
      }

      List<Quest> removeQuests = new ArrayList<Quest> ();
      for (Quest quest : this.questLog.values ())
      {
        quest.onTalk (clickedEntity);
        if (quest.isDone ())
        {
          removeQuests.add(quest);
        }
      }
      for (Quest remove : removeQuests)
      {
        Offer check = Offer.getOffer (remove.getOfferId (), null);
        Reward reward = check.finishQuest (this.player.getName ());
        Quest.deleteQuest (remove);
        this.questLog.remove (remove.getOfferId ());
        this.player.giveReward (reward);
        this.questChanged = true;
      }

      this.hud.chatLine (clickedEntity, description);
    }
    else if (null == clickedEntity ||
            clickedEntity.getType ().equals (EntityType.MONSTER.getId ()))
    {
      attack = (AutoAttackAI) this.player.getAI (Game.AUTO_ATTACK_AI);
      attack.setTarget (clickedEntity);
    }
  }

  public Game getGame ()
  {
    return game;
  }

  public WorldHolder getWorld ()
  {
    return world;
  }
  
  public String getPlayerName ()
  {
    return this.playerName;
  }
  
  public String getPlayerImage ()
  {
    return this.player.getBasicImage ();
  }
  
  public Entity getPlayer ()
  {
    return this.player;
  }
  
  public Collection<Quest> getQuests ()
  {
    return this.questLog.values ();
  }
  
  /**
   * Determine whether quests have changed since this function was last called
   * @return whether quests have changed
   */
  public boolean questChanged ()
  {
    boolean changed = this.questChanged;
    this.questChanged = false;
    return changed;
  }
  
  /**
   * Called when the player kills an entity
   * @param killer the player
   * @param killed the entity killed
   */
  public void onKill (Entity killer, Entity killed)
  {
    List<Quest> removeQuests = new ArrayList<Quest> ();
    AutoAttackAI attack = (AutoAttackAI) this.player.getAI (Game.AUTO_ATTACK_AI);
    
    this.hud.changed ();
    
    if (this.hud.getTarget () == killed)
    {
      this.hud.setTarget (null);
    }
    
    if (attack.getTarget () == killed)
    {
      attack.setTarget (null);
    }
    
    for (Quest quest : this.questLog.values ())
    {
      quest.onKill (killed);
      if (quest.isDone ())
      {
        removeQuests.add(quest);
      }
    }
    for (Quest remove : removeQuests)
    {
      Offer check = Offer.getOffer (remove.getOfferId (), null);
      Reward reward = check.finishQuest (this.player.getName ());
      this.player.giveReward (reward);
      Quest.deleteQuest (remove);
      this.questLog.remove (remove.getOfferId ());
    }

    this.questChanged = true;
  }

  public void pickupItem (ItemSet item)
  {
    List<Quest> removeQuests = new ArrayList<Quest> ();
    
    this.hud.changed ();
    
    for (Quest quest : this.questLog.values ())
    {
      quest.onPickup (item);
      if (quest.isDone ())
      {
        removeQuests.add(quest);
      }
    }
    for (Quest remove : removeQuests)
    {
      Offer check = Offer.getOffer (remove.getOfferId (), null);
      Reward reward = check.finishQuest (this.player.getName ());
      Quest.deleteQuest (remove);
      this.questLog.remove (remove.getOfferId ());
      this.player.giveReward (reward);
    }

    this.questChanged = true;
  }

  public void loseItem (ItemSet item)
  {
    List<Quest> removeQuests = new ArrayList<Quest> ();
    
    this.hud.changed ();
    
    for (Quest quest : this.questLog.values ())
    {
      quest.onLose (item);
      if (quest.isDone ())
      {
        removeQuests.add(quest);
      }
    }
    for (Quest remove : removeQuests)
    {
      Offer check = Offer.getOffer (remove.getOfferId (), null);
      Reward reward = check.finishQuest (this.player.getName ());
      Quest.deleteQuest (remove);
      this.questLog.remove (remove.getOfferId ());
      this.player.giveReward (reward);
    }

    this.questChanged = true;
  }
}
