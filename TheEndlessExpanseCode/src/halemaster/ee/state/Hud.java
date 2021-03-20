package halemaster.ee.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.FileLocator;
import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.Chat;
import de.lessvoid.nifty.controls.ChatTextSendEvent;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleExecuteCommandEvent;
import de.lessvoid.nifty.controls.Draggable;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.DroppableDropFilter;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.impl.Hint;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import halemaster.ee.Game;
import halemaster.ee.ai.ShopAI;
import halemaster.ee.item.Enchant;
import halemaster.ee.item.Inventory;
import halemaster.ee.item.Inventory.Equipment;
import halemaster.ee.item.Item;
import halemaster.ee.item.ItemSet;
import halemaster.ee.localization.Localizer;
import halemaster.ee.quest.Offer;
import halemaster.ee.quest.Quest;
import halemaster.ee.world.Area;
import halemaster.ee.world.Config;
import halemaster.ee.world.entity.Effect;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityType;
import halemaster.ee.world.entity.Statistic;
import halemaster.ee.world.macro.MacroTerrainGenerator;
import halemaster.ee.world.micro.AreaGenerator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @name Hud
 * 
 * @version 0.0.0
 * 
 * @date Oct 24, 2013
 */
public class Hud extends Menu implements KeyInputHandler, DroppableDropFilter
{
  public static final float TARGET_DISTANCE = 64f * AreaGenerator.TILE_WIDTH;
  public static final String FILE = "Interface/menu/hud.xml";
  public static final String ID = "hud";
  public static final String INITIAL_OUTPUT = "You've opened the console. "
          + "You can type commands here.";
  public static final String ERROR = "ERROR: ";
  public static final String USAGE = "USAGE: ";
  public static final String INVALID_COMMAND = "Invalid Command";
  public static final String TALK = "hud.talk";
  public static final String KILL = "hud.kill";
  public static final String PICKUP = "hud.item";
  public static final String TAG_PLAYER = "tag_player";
  public static final int MAX_EFFECT_SHOW = 9;
  public static final int TAG_SIZE = 16;
  public static final String TAG_FOLDER = "/tags";
  public static final String TAG_ID = "tag_";
  public static final String TAG_SEPARATOR = ";";
  public static final int TRAVEL_COST = 10;
  public static final String SHOP_UNSELECTED = "Interface/Images/shop_unselected_back.png";
  public static final String SHOP_SELECTED = "Interface/Images/shop_selected_back.png";
  
  private PlayerState player;
  private Entity target;
  private List<String> titles;
  private List<String> descriptions;
  private List<String> requirements;
  private List<String> previousCommands = new ArrayList<String> ();
  private int currentCommand = 0;
  private boolean consoleVisible = false;
  private boolean allowConsoleToggle = true;
  private boolean firstConsoleShow = true;
  private Element consolePopup;
  private ConcurrentLinkedQueue<Entity> addedUsers = 
          new ConcurrentLinkedQueue<Entity> ();
  private ConcurrentLinkedQueue<Entity> removedUsers = 
          new ConcurrentLinkedQueue<Entity> ();
  private Item[] skills = new Item[10];
  private Element gotoPopup;
  private Area travelLocation;
  private int travelCost;
  private String travelDestination;
  private Area shopSelected = null;
  private ItemSet itemSell = null;
  private boolean changed = true;
  
  public Hud (PlayerState player)
  {
    super (FILE, ID);
    
    this.player = player;
  }
  
  @Override
  public void bind (final Nifty newNifty, final Screen newScreen)
  {
    super.bind (newNifty, newScreen);
    
    newScreen.addKeyboardInputHandler (new HudMapping (), this);
    this.consolePopup = newNifty.createPopup ("consolePopup");
  }
  
  /**
   * Initialize this menu.
   * 
   * @param manager manager to initialize with.
   * @param app application to initialize with.
   */
  @Override
  public void initialize (AppStateManager manager, Application app)
  {
    Label playerName;
    Element playerImage;
    Window characterWindow, invWindow, questWindow, mapWindow, shopWindow;
    NiftyImage playerNiftyImage;
    Element characterImage;
    Element targetPanel;
    Element map;
    Droppable tempDrop;
    ImageBuilder tagBuilder;
    File tagFolder;
    
    super.initialize (manager, app);
    
    playerName = getNifty ().getCurrentScreen ()
            .findNiftyControl ("player_name", Label.class);
    playerImage = getNifty ().getCurrentScreen ().findElementByName 
            ("player_image");
    characterImage = getNifty ().getCurrentScreen ().findElementByName 
            ("window_character_player_image");
    characterWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_character", Window.class);
    invWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_inventory", Window.class);
    questWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_quest", Window.class);
    mapWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_map", Window.class);
    shopWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_shop", Window.class);
    map = getNifty ().getCurrentScreen ().findElementByName ("map_image");
    targetPanel = getNifty ().getCurrentScreen ().findElementByName ("target");
    playerNiftyImage = getNifty ()
            .createImage (this.player.getPlayerImage (), false);
    
    targetPanel.hide ();
    
    playerName.setText (this.player.getPlayerName ());
    playerImage.getRenderer (ImageRenderer.class).setImage (playerNiftyImage); 
    characterImage.getRenderer (ImageRenderer.class).setImage (playerNiftyImage); 
    characterWindow.getElement ().hide ();
    characterWindow.setTitle (this.player.getPlayerName ());
    invWindow.getElement ().hide ();
    questWindow.getElement ().hide ();
    shopWindow.getElement ().hide ();
    mapWindow.getElement ().hide ();
    mapWindow.setTitle (Localizer.getString ("game.hud.map", (Object) 
            this.player.getWorld ().getEvents ().getWorldName ()));
    this.player.getGame ().getAssetManager ().registerLocator(Game.HOME + 
            Game.GAME_FOLDER + Game.WORLD_FOLDER, FileLocator.class);
    map.getRenderer (ImageRenderer.class).setImage (getNifty ().createImage (
            this.player.getWorld ().getEvents ().getWorldName () + "/map.png",
            false));
    
    tagFolder = new File (Game.HOME + Game.GAME_FOLDER + Game.WORLD_FOLDER
            + "/" + this.player.getWorld ().getEvents ().getWorldName () +
            TAG_FOLDER);
    
    for (final File tag : tagFolder.listFiles ())
    {
      // place a tag
      final String location = tag.getName ().split (TAG_SEPARATOR)[0];
      final String title = tag.getName ().split (TAG_SEPARATOR)[1]
              .split ("\\.")[0];
      tagBuilder = new ImageBuilder () {{
        id (TAG_ID + title);
        width (String.valueOf(TAG_SIZE));
        height (String.valueOf(TAG_SIZE));
        x (String.valueOf (Integer.valueOf(location.split (",")[0]) - 
                TAG_SIZE / 2));
        y (String.valueOf (Integer.valueOf(location.split (",")[1]) - 
                TAG_SIZE / 2));
        filename (player.getWorld ().getEvents ().getWorldName () + TAG_FOLDER + 
                "/" + tag.getName ());
        interactOnClick ("attemptGoto(" + location + "," + title + ")");
        onHoverEffect (new HoverEffectBuilder ("hint")
        {{
          effectParameter ("hintText", title + " " + location);
        }});
      }};

      tagBuilder.build (getNifty (), getScreen(), map);
    }
    
    // place player on world
    tagBuilder = new ImageBuilder () {{
      id (TAG_PLAYER);
      width (String.valueOf(TAG_SIZE));
      height (String.valueOf(TAG_SIZE));
      x (String.valueOf(player.getPlayer ().getLocation ().getX () - 
              TAG_SIZE / 2));
      y (String.valueOf(player.getPlayer ().getLocation ().getY () - 
              TAG_SIZE / 2));
      filename (player.getPlayerImage ());
      onHoverEffect (new HoverEffectBuilder ("hint")
      {{
        effectParameter ("hintText", player.getPlayerName () + " " + player
                .getPlayer ().getLocation ().toString ());
      }});
    }};
    
    tagBuilder.build (getNifty (), getScreen(), map);
    
    for (Equipment equip : Equipment.values ())
    {
      tempDrop = getNifty ().getCurrentScreen ().findNiftyControl ("equip_" + 
              equip.name (), Droppable.class);
      tempDrop.addFilter (this);
    }
    for (int i = 0; i < 10; i++)
    {
      tempDrop = getNifty ().getCurrentScreen ().findNiftyControl ("skill-" + i
              , Droppable.class);
      tempDrop.addFilter (this);
    }
    for (int x = 0; x < Entity.INV_HEIGHT; x++)
    {
      for (int y = 0; y < Entity.INV_WIDTH; y++)
      {
        tempDrop = getNifty ().getCurrentScreen ().findNiftyControl ("inv_" + x
                + "," + y, Droppable.class);
        tempDrop.addFilter (this);
      }
    }
    tempDrop = getNifty ().getCurrentScreen ().findNiftyControl ("item_delete",
            Droppable.class);
    tempDrop.addFilter (this);
    tempDrop = getNifty ().getCurrentScreen ().findNiftyControl ("shop_sell",
            Droppable.class);
    tempDrop.addFilter (this);
  }
  
  public boolean attemptGoto (String x, String y, String title)
  {
    Label travelText;
    this.gotoPopup = getNifty ().createPopup ("movementPopup");
    getNifty ().showPopup (getScreen (), this.gotoPopup.getId (), null);
    travelText = this.gotoPopup.findNiftyControl ("travel_text", Label.class);
    this.travelLocation = new Area (Integer.valueOf (x), Integer.valueOf (y));
    this.travelDestination = title;
    this.travelCost = TRAVEL_COST * this.travelLocation.distance (this.player
            .getPlayer ().getLocation ());
    travelText.setText (Localizer.getString ("game.hud.travel.text", (Object)
            this.travelDestination, this.travelCost));
    
    return true;
  }
  
  public boolean travel ()
  {
    // travel to destination!
    if (this.player.getPlayer ().getInventory ().getMoney () >= this.travelCost)
    {
      this.player.getPlayer ().move (this.travelLocation.getX () * 
              AreaGenerator.AREA_SIZE - this.player.getPlayer ()
              .getExactLocation ().getX (), this.travelLocation.getY () * 
              AreaGenerator.AREA_SIZE - this.player.getPlayer ()
              .getExactLocation ().getY ());
      this.player.getPlayer ().getInventory ().spendMoney (this.travelCost);
    }
    closeGoto ();
    return true;
  }
  
  public boolean closeGoto ()
  {
    getNifty ().closePopup (this.gotoPopup.getId ());
    
    return true;
  }
  
  public String getWidthPercent (String percent, String width)
  {
    return String.valueOf ((int) (this.player.getGame ().getCamera ()
            .getWidth () * (Float.valueOf (percent) / 100) - 
            Integer.valueOf (width)));
  }
  
  public String getHeightPercent (String percent, String height)
  {
    return String.valueOf ((int) (this.player.getGame ().getCamera ()
            .getHeight () * (Float.valueOf (percent) / 100) - 
            Integer.valueOf (height)));
  }
  
  public String getMapWidth (String bonus)
  {
    int value = Integer.valueOf (bonus);
    try
    {
      value += Integer.valueOf (Config.getValue (this.player
              .getWorld ().getEvents ().getWorldName (), MacroTerrainGenerator
              .SIZE_KEY));
      return String.valueOf (value);
    }
    catch (IOException e)
    {
      return String.valueOf (300);
    }
  }
  
  public String getMapHeight (String bonus)
  {
    return getMapWidth (bonus);
  }
  
  public void changed ()
  {
    this.changed = true;
  }
  
  /**
   * Updates the hud with new information, if any exists.
   */
  public void update ()
  {
    ListBox questList = getNifty ().getCurrentScreen ()
            .findNiftyControl ("questLog", ListBox.class);
    Label title, description, requirement;
    HealthBarControl playerHealth, targetHealth;
    Element playerTag;
    Element cdTag;
    Offer offer;
    float health;
    
    if (null != this.target && this.target.getAbsoluteLocation ()
            .distance (this.player.getPlayer ().getAbsoluteLocation ())
            > TARGET_DISTANCE)
    {
      setTarget (null);
    }
    
    updateEffects ();
    
    if (this.changed)
    {
      updateInventory ();
      updateEquipment ();
      updateCharacterWindow ();
      this.changed = false;
    }
    
    for (int i = 0; i < this.skills.length; i++)
    {
      Item item = this.skills[i];
      String cd = "";
      cdTag = getNifty ().getCurrentScreen ().findElementByName ("skill-" + i + 
              "-cd");
      if (null != item)
      {
        if (this.player.getPlayer ().getRemainingCooldown (item.getCooldown ()) 
                > 0)
        {
          cd = String.valueOf (this.player.getPlayer ().getRemainingCooldown 
                  (item.getCooldown ()));
        }
      }
      
      cdTag.getRenderer (TextRenderer.class).setText (cd);
    }
    
    Area location = this.player.getPlayer ().getLocation ();
    
    playerTag = getNifty ().getCurrentScreen ().findElementByName (TAG_PLAYER);
    playerTag.setConstraintX (new SizeValue ((location.getX () - TAG_SIZE / 2) 
            + "px"));
    playerTag.setConstraintY (new SizeValue ((location.getY () - TAG_SIZE / 2) 
            + "px"));
    playerTag.getEffects (EffectEventId.onHover, Hint.class).get (0)
            .getParameters ().setProperty ("hintText", player.getPlayerName () +
            " " + location.toString ());
    
    
    if (this.player.questChanged ())
    {
      questList.clear ();
      this.titles = new ArrayList<String> ();
      this.descriptions = new ArrayList<String> ();
      this.requirements = new ArrayList<String> ();
      for (Quest quest : this.player.getQuests ())
      {
        offer = Offer.getOffer (quest.getOfferId (), null);
        questList.addItem (offer.getTitle ());
        this.titles.add (offer.getTitle ());
        this.descriptions.add (offer.getDescription ());
        String tempReqs = "";
        for (String talk : offer.getRequirements ().getTalkNames ())
        {
          tempReqs += Localizer.getString (TALK, (Object) talk) + "\n";
        }
        for (String kill : offer.getRequirements ().getKillNames ())
        {
          tempReqs += Localizer.getString (KILL, offer
                  .getRequirements ().getKillCount (kill) - quest
                  .getFinishedRequirements ().getKillCount (kill), offer
                  .getRequirements ().getKillCount (kill), Localizer
                  .getString(kill)) + "\n";
        }
        for (String item : offer.getRequirements ().getItemNames ())
        {
          tempReqs += Localizer.getString (PICKUP, offer
                  .getRequirements ().getItemCount (item) - quest
                  .getFinishedRequirements ().getItemCount (item), offer
                  .getRequirements ().getItemCount (item), Localizer
                  .getString(item)) + "\n";
        }
        this.requirements.add (tempReqs);
      }
      title = getNifty ().getCurrentScreen ()
              .findNiftyControl ("currentQuestTitle", Label.class);
      description = getNifty ().getCurrentScreen ()
              .findNiftyControl ("currentQuestDescription", Label.class);
      requirement = getNifty ().getCurrentScreen ()
              .findNiftyControl ("questRequirements", Label.class);
      if (this.titles.size () > 0 && this.descriptions.size () > 0 &&
              this.requirements.size () > 0)
      {
        questList.selectItemByIndex (0);
        title.setText (this.titles.get (0));
        description.setText (this.descriptions.get (0));
        requirement.setText (this.requirements.get (0));
      }
      else
      {
        title.setText ("");
        description.setText ("");
        requirement.setText ("");
      }
    }
    
    playerHealth = getNifty ().getCurrentScreen ()
            .findControl ("player_health", HealthBarControl.class);
    health = (float) this.player.getPlayer ().getCurrentHp () / 
            (float) this.player.getPlayer ().getStat (Statistic.HEALTH);
    playerHealth.setHealthPercent (health);
    
    if (this.target != null)
    {
      targetHealth = getNifty ().getCurrentScreen ()
            .findControl ("target_health", HealthBarControl.class);
      health = (float) this.target.getCurrentHp () / 
              (float) this.target.getStat (Statistic.HEALTH);
      targetHealth.setHealthPercent (health);
    }
    
    while (!this.addedUsers.isEmpty ())
    {
      finalizeAddChatUser (this.addedUsers.poll ());
    }
    
    while (!this.removedUsers.isEmpty ())
    {
      finalizeRemoveChatUser (this.removedUsers.poll ());
    }
  }
  
  
  
  /**
   * toggle opening of the character window
   * 
   * @return whether the input event was used up
   */
  public boolean openCharacter ()
  {
    Element characterWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_character", Window.class).getElement ();    
    
    if (characterWindow.isVisible ())
    {
      characterWindow.hide ();
    }
    else
    {      
      characterWindow.show ();
    }
    
    return true;
  }
  
  /**
   * toggle opening of the inventory window
   * 
   * @return whether the input event was used up
   */
  public boolean openInventory ()
  {
    Element inventoryWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_inventory", Window.class).getElement ();
    
    if (inventoryWindow.isVisible ())
    {
      inventoryWindow.hide ();
    }
    else
    {
      inventoryWindow.show ();
    }
    
    return true;
  }
  
  /**
   * toggle opening of the quest log window
   * 
   * @return whether the input event was used up
   */
  public boolean openQuestLog ()
  {
    Element questWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_quest", Window.class).getElement ();
    
    if (questWindow.isVisible ())
    {
      questWindow.hide ();
    }
    else
    {
      questWindow.show ();
    }
    
    return true;
  }
  
  public boolean openMap ()
  {
    Element questWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_map", Window.class).getElement ();
    
    if (questWindow.isVisible ())
    {
      questWindow.hide ();
    }
    else
    {
      questWindow.show ();
    }
    
    return true;
  }
  
  /**
   * Attempt to add a point to power
   * @return whether the event was consumed
   */
  public boolean addPower ()
  {
    addStat (Statistic.POWER);
    
    return true;
  }
  
  /**
   * Attempt to add a point to agility
   * @return whether the event was consumed
   */
  public boolean addAgility ()
  {
    addStat (Statistic.AGILITY);
    
    return true;
  }
  
  /**
   * Attempt to add a point to intellect
   * @return whether the event was consumed
   */
  public boolean addIntellect ()
  {
    addStat (Statistic.INTELLECT);
    
    return true;
  }
  
  /**
   * Attempt to add a point to the given stat
   * @param stat stat to add to
   */
  public void addStat (Statistic stat)
  {
    this.player.getPlayer ().spendStat (stat);
    changed ();
  }
  
  public void updateEffects ()
  {
    if (null != this.player)
    {
      updateEffects (this.player.getPlayer (), "player");
    }
    if (null != this.target)
    {
      updateEffects (this.target, "target");
    }
  }
  
  public void updateEffects (Entity entity, String id)
  {
    Element image;
    Label time;
    Effect[] effects = entity.getEffects ();
    
    for (int i = 0; i < MAX_EFFECT_SHOW; i++)
    {
      image = getNifty ().getCurrentScreen ().findElementByName (id + "_effect_"
              + i);
      time = getNifty ().getCurrentScreen ().findNiftyControl (id + "_effect_" +
              i + "_text", Label.class);
      if (i < effects.length)
      {
        image.getRenderer (ImageRenderer.class).setImage (getNifty ().createImage 
                (effects[i].getImage (), false));
        time.setText (String.valueOf ((int) Math.ceil (effects[i].getLasts () -
                entity.getEffectTime (effects[i]))));
        image.getEffects (EffectEventId.onHover, Hint.class).get (0)
                .getParameters ().setProperty ("hintText", 
                getEffectText (effects[i]));
        image.show ();
      }
      else
      {
        image.hide ();
      }
    }
    
    image = getNifty ().getCurrentScreen ().findElementByName 
            (id + "_effect_cont");
    if (effects.length > MAX_EFFECT_SHOW)
    {
      String effectText = null;
      
      for (int i = MAX_EFFECT_SHOW; i < effects.length; i++)
      {
        if (null == effectText)
        {
          effectText = getShortEffectText (effects[i], 
                  (int) (float) entity.getEffectTime (effects[i]));
        }
        else
        {
          effectText = "\n" + getShortEffectText (effects[i], 
                  (int) (float) entity.getEffectTime (effects[i]));
        }
      }
      
      image.getEffects (EffectEventId.onHover, Hint.class).get (0)
                .getParameters ().setProperty ("hintText", 
                effectText);
      image.show ();
    }
    else
    {
      image.hide ();
    }
  }
  
  public String getEffectText (Effect effect)
  {
    String value = "";
    
    value += Localizer.getString (effect.getName ());
    if (effect.getStack () > 1)
    {
      value += "     " + Localizer.getString ("item.effect.stack", effect
              .getCurrentStack (), effect.getStack ());
    }
    
    if (effect.getDamage () > 0)
    {
      value += "\n" + Localizer.getString ("item.effect.damage", 
              effect.getDamage ());
    }
    else if (effect.getDamage () < 0)
    {
      value += "\n" + Localizer.getString ("item.effect.heal", 
              -effect.getDamage ());
    }
    
    for (Entry<String, Float> boost : effect.getBoost ().entrySet ())
    {
      value += "\n" + Localizer.getString ("item.equip.stat.boost." + boost
              .getKey (), effect.getStat (Statistic.valueOf (boost.getKey ())));
    }
    
    return value;
  }
  
  public String getShortEffectText (Effect effect, int lasting)
  {
    return Localizer.getString ("item.effect.short", effect.getCurrentStack (), 
            effect.getName (), lasting);
  }
  
  /**
   * Update the character window with new data.
   */
  public void updateCharacterWindow ()
  {
    Label charName, charLevel, charXp, charHealth, charPower, charAgility,
            charIntellect, charArmor, charCraft, charDamage, charAccuracy, 
            charCritical, charPoints, charHaste;
    
    charName = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_name", Label.class);
    charPoints = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_points", Label.class);
    charLevel = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_level", Label.class);
    charXp = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_xp", Label.class);
    charHealth = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_health", Label.class);
    charPower = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_power", Label.class);
    charAgility = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_agility", Label.class);
    charIntellect = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_intellect", Label.class);
    charArmor = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_armor", Label.class);
    charCraft = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_craft", Label.class);
    charDamage = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_damage", Label.class);
    charAccuracy = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_accuracy", Label.class);
    charCritical = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_critical", Label.class);
    charHaste = getNifty ().getCurrentScreen ()
            .findNiftyControl ("char_haste", Label.class);

    charName.setText (this.player.getPlayer ().getName ());
    charName.setWidth (new SizeValue (charName.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charLevel.setText (String.valueOf (this.player.getPlayer ().getStat 
            (Statistic.LEVEL)));
    charLevel.setWidth (new SizeValue (charLevel.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charXp.setText (this.player.getPlayer ().getStat (Statistic.XP) + " / " + 
            Statistic.XP.getMaximum ());
    charXp.setWidth (new SizeValue (charXp.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charPoints.setText (String.valueOf (this.player.getPlayer ()
            .getStatPoints ()));
    charPoints.setWidth (new SizeValue (charPoints.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charHealth.setText (this.player.getPlayer ().getCurrentHp ()+ " / " + 
            this.player.getPlayer ().getStat (Statistic.HEALTH));
    charHealth.setWidth (new SizeValue (charHealth.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charPower.setText (String.valueOf(this.player.getPlayer ().getStat 
            (Statistic.POWER)));
    charPower.setWidth (new SizeValue (charPower.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charAgility.setText (String.valueOf(this.player.getPlayer ().getStat 
            (Statistic.AGILITY)));
    charAgility.setWidth (new SizeValue (charAgility.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charIntellect.setText (String.valueOf(this.player.getPlayer ().getStat 
            (Statistic.INTELLECT)));
    charIntellect.setWidth (new SizeValue (charIntellect.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charArmor.setText (String.valueOf (this.player.getPlayer ()
            .getStat (Statistic.ARMOR)));
    charArmor.setWidth (new SizeValue (charArmor.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charCritical.setText (String.valueOf (this.player.getPlayer ()
            .getStat (Statistic.CRITICAL)) + "%");
    charCritical.setWidth (new SizeValue (charCritical.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charCraft.setText (String.valueOf (this.player.getPlayer ()
            .getStat (Statistic.CRAFT)));
    charCraft.setWidth (new SizeValue (charCraft.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charAccuracy.setText (String.valueOf (this.player.getPlayer ()
            .getStat (Statistic.ACCURACY)));
    charAccuracy.setWidth (new SizeValue (charAccuracy.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charDamage.setText (this.player.getPlayer ()
            .getDamageLower () + " - " + 
            this.player.getPlayer ().getDamageUpper ());
    charDamage.setWidth (new SizeValue (charDamage.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    charHaste.setText (String.valueOf (this.player.getPlayer ()
            .getStat (Statistic.HASTE)));
    charHaste.setWidth (new SizeValue (charHaste.getElement ().getRenderer 
            (TextRenderer.class).getTextWidth () + "px"));
    
    charName.getElement ().getParent ().getParent ().getParent().getParent()
            .layoutElements ();
  }
  
  public void updateInventory ()
  {
    Inventory inv = this.player.getPlayer ().getInventory ();
    Element image;
    Element back;
    Element count;
    ItemSet item;
    
    for (int x = 0; x < inv.getWidth (); x++)
    {
      for (int y = 0; y < inv.getHeight (); y++)
      {
        image = getNifty ().getCurrentScreen ().findElementByName ("inv_" + y +
                "," + x + "_image");
        back = getNifty ().getCurrentScreen ().findElementByName ("inv_" + y +
                "," + x + "_back");
        count = getNifty ().getCurrentScreen ().findElementByName ("inv_" + y +
                "," + x + "_count");
        item = inv.getItem (x, y);
        if (null == item)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage ("Interface/Images/skill_blank.png", false));
          count.getRenderer (TextRenderer.class).setText ("");
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage (item.getItem ().getImage ()
                  , false));
          count.getRenderer (TextRenderer.class).setText (String.valueOf (
                  item.getAmount ()));
          setItemHint (item, back);
        }
      }
    }
    
    getNifty ().getCurrentScreen ().findNiftyControl ("coin_copper", 
            Label.class).setText (String.valueOf (inv.getMoney ()));
  }
  
  public void openShop ()
  {
    Element shopWindow = getNifty ().getCurrentScreen ()
            .findNiftyControl ("window_shop", Window.class).getElement ();
    
    if (this.target == null || !this.target.getType ().equals (EntityType
            .NPC.getId ()) || this.target.getInventory ().isEmpty ())
    {
      shopWindow.hide ();
    }
    else
    {
      shopWindow.show ();
      updateShop ();
    }
    returnSell ();
  }
  
  public void updateShop ()
  {
    this.target.setData (ShopAI.SHOPPING, String.valueOf(true));
    Inventory inv = this.target.getInventory ();
    Element image;
    Element back;
    ItemSet item;
    
    for (int x = 0; x < inv.getWidth (); x++)
    {
      for (int y = 0; y < inv.getHeight (); y++)
      {
        image = getNifty ().getCurrentScreen ().findElementByName ("shop_" + y +
                "," + x + "_image");
        back = getNifty ().getCurrentScreen ().findElementByName ("shop_" + y +
                "," + x + "_back");
        item = inv.getItem (x, y);
        back.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (SHOP_UNSELECTED, false));
        if (null == item)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage ("Interface/Images/skill_blank.png", false));
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage (item.getItem ().getImage ()
                  , false));
          setItemHint (item, back);
        }
      }
    }
    image = getNifty ().getCurrentScreen ().findElementByName ("shop_sell_image");
    image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage ("Interface/Images/skill_blank.png", false));
    this.shopSelected = null;
  }
  
  public boolean selectItem(String y, String x)
  {
    Area previous = this.shopSelected;
    Element back;
    
    this.shopSelected = new Area (Integer.valueOf (x), Integer.valueOf (y));
    
    back = getNifty ().getCurrentScreen ().findElementByName ("shop_" +
            y + "," + x + "_back");
    back.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (SHOP_SELECTED, false));
    
    if (null != previous)
    {
      back = getNifty ().getCurrentScreen ().findElementByName ("shop_" +
              previous.getY () + "," + previous.getX () + "_back");
      back.getRenderer (ImageRenderer.class).setImage (getNifty ()
                  .createImage (SHOP_UNSELECTED, false));
    }
    
    return true;
  }
  
  public boolean buyItem ()
  {
    if (null != this.shopSelected && null != this.target.getInventory ()
            .getItem (this.shopSelected.getX (), this.shopSelected.getY ()))
    {
      ItemSet item = new ItemSet (this.target.getInventory ().getItem (
              this.shopSelected.getX (), this.shopSelected.getY ()).getItem (),
              1);
      
      if (this.player.getPlayer ().getInventory ().spendMoney (item.getItem ()
              .getCost ()))
      {
        this.player.getPlayer ().getInventory ().addItem (item);
      }
    }
    
    return true;
  }
  
  public boolean sellItem ()
  {
    if (null != this.itemSell)
    {
      this.player.getPlayer ().getInventory ().addMoney (this.itemSell
              .getItem ().getCost () / 2);
      this.itemSell.setAmount (this.itemSell.getAmount () - 1);
      if (this.itemSell.getAmount () <= 0)
      {
        Element back = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_back");
        Element image = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_image");
        image.getRenderer (ImageRenderer.class).setImage (getNifty ()
              .createImage ("Interface/Images/skill_blank.png", false));
        back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                .getParameters ().setProperty ("hintText", "  ");
        this.itemSell = null;
      }
    }
    
    return true;
  }
  
  public void returnSell ()
  {
    if (null != this.itemSell)
    {
      this.player.getPlayer ().getInventory ().addItem (this.itemSell);
      this.itemSell = null;
    }
  }
  
  public void setItemHint (ItemSet item, Element back)
  {
    String itemText = Localizer.getString (item.getName (), 
                  item.getItem ().getLevel ()) + "      ";
    String itemType = "item.type";
    if (item.getItem ().getEquipLocations ().length == 0)
    {
      itemType += ".USE";
    }
    else
    {
      for (String type : item.getItem ().getEquipLocations ())
      {
        itemType += "." + type;
      }
    }
    itemText += Localizer.getString (itemType);
    if (item.getItem ().getOnEquip ().getDamageLower (item.getItem ()
            .getLevel ()) != 0 || item.getItem ().getOnEquip ()
            .getDamageUpper (item.getItem ().getLevel ()) != 0)
    {
      itemText += "\n" + Localizer.getString ("item.equip.stat.damage."
              + item.getItem ().getWeaponType (), 
              item.getItem ().getOnEquip ().getDamageLower (item
              .getItem ().getLevel ()), item.getItem ().getOnEquip ()
              .getDamageUpper (item.getItem ().getLevel ()));
    }
    if (null != item.getItem ().getOnEquip ().getBoost ())
    {
      for (String statBoost : item.getItem ()
              .getOnEquip ().getBoost ().keySet ())
      {
        if (item.getItem ().getOnEquip ().getBoost (statBoost, item.getItem ()
                    .getLevel ()) > 0)
        {
          itemText += "\n" + Localizer.getString ("item.equip.stat.boost."
                  + statBoost, item.getItem ().getOnEquip ().getBoost 
                  (statBoost, item.getItem ().getLevel ()));
        }
      }
    }
    for (halemaster.ee.world.entity.Effect effect : item.getItem ()
            .getOnEquip ().getEffects ())
    {
      itemText += "\n" + Localizer.getString ("item.equip.stat.effect", 
              (Object) Localizer.getString(effect.getName ()), 
              effect.getLasts (), effect.getCurrentStack (),
              effect.getStack ());
    }
    if (null != item.getItem ().getOnUse ())
    {
      if (item.getItem ().getOnUse ().getDamageLower (item.getItem ()
              .getLevel ()) != 0 || item.getItem ().getOnUse ()
              .getDamageUpper (item.getItem ().getLevel ()) != 0)
      {
        if (item.getItem ().getTarget ().equals (Item.TARGET))
        {
          itemText += "\n" + Localizer.getString ("item.use.stat.damage", 
                  item.getItem ().getOnUse ().getDamageLower (item
                  .getItem ().getLevel ()), item.getItem ().getOnUse ()
                  .getDamageUpper (item.getItem ().getLevel ()));
        }
        else if (item.getItem ().getTarget ().equals (Item.SELF))
        {
          itemText += "\n" + Localizer.getString ("item.use.stat.heal", 
                  item.getItem ().getOnUse ().getDamageLower (item
                  .getItem ().getLevel ()), item.getItem ().getOnUse ()
                  .getDamageUpper (item.getItem ().getLevel ()));
        }
      }
      if (null != item.getItem ().getOnUse ().getBoost ())
      {
        for (String statBoost : item.getItem ()
                .getOnUse ().getBoost ().keySet ())
        {
          if (item.getItem ().getOnUse ().getBoost (statBoost, item.getItem ()
                    .getLevel ()) > 0)
          {
            itemText += "\n" + Localizer.getString ("item.use.stat.boost."
                    + statBoost, item.getItem ().getOnUse ().getBoost 
                  (statBoost, item.getItem ().getLevel ()));
          }
        }
      }
      for (halemaster.ee.world.entity.Effect effect : item.getItem ()
            .getOnUse ().getEffects ())
      {
        itemText += "\n" + Localizer.getString ("item.use.stat.effect", 
                (Object) Localizer.getString(effect.getName ()), 
                effect.getLasts (), effect.getCurrentStack (),
                effect.getStack ());
      }
    }
    
    for (String enchant : item.getItem ().getEnchants ())
    {
      Enchant ench = Enchant.getEnchant (enchant);
      itemText += "\n" + Localizer.getString (ench.getId ());
      if (ench.getOnEquip ().getDamageLower (item.getItem ()
            .getLevel ()) != 0 || ench.getOnEquip ()
            .getDamageUpper (item.getItem ().getLevel ()) != 0)
      {
        if (ench.getAttackTarget ().equals (Item.SELF))
        {
          itemText += "\n  " + Localizer.getString ("item.use.stat.heal", 
                  ench.getOnEquip ().getDamageLower (item
                  .getItem ().getLevel ()), ench.getOnEquip ()
                  .getDamageUpper (item.getItem ().getLevel ()));
        }
        else if (ench.getAttackTarget ().equals (Item.TARGET))
        {
          itemText += "\n  " + Localizer.getString ("item.use.stat.damage", 
                  ench.getOnEquip ().getDamageLower (item
                  .getItem ().getLevel ()), ench.getOnEquip ()
                  .getDamageUpper (item.getItem ().getLevel ()));
        }
      }
      if (null != ench.getOnEquip ().getBoost ())
      {
        for (String statBoost : ench
                .getOnEquip ().getBoost ().keySet ())
        {
          if (ench.getOnEquip ().getBoost (statBoost, item.getItem ()
                    .getLevel ()) > 0)
          {
            itemText += "\n  " + Localizer.getString ("item.equip.stat.boost."
                + statBoost, ench.getOnEquip ().getBoost 
              (statBoost, item.getItem ().getLevel ()));
          }
        }
      }
      for (halemaster.ee.world.entity.Effect effect : ench
              .getOnEquip ().getEffects ())
      {
        itemText += "\n  " + Localizer.getString ("item.equip.stat.effect", 
                (Object) Localizer.getString(effect.getName ()), 
                effect.getLasts (), effect.getCurrentStack (),
                effect.getStack ());
      }
      if (null != ench.getOnUse ())
      {
        if (ench.getOnUse ().getDamageLower (item.getItem ()
                .getLevel ()) != 0 || ench.getOnUse ()
                .getDamageUpper (item.getItem ().getLevel ()) != 0)
        {
          if (ench.getUseTarget ().equals (Item.TARGET))
          {
            itemText += "\n  " + Localizer.getString ("item.use.stat.damage", 
                    ench.getOnUse ().getDamageLower (item
                    .getItem ().getLevel ()), ench.getOnUse ()
                    .getDamageUpper (item.getItem ().getLevel ()));
          }
          else if (ench.getUseTarget ().equals (Item.SELF))
          {
            itemText += "\n  " + Localizer.getString ("item.use.stat.heal", 
                    ench.getOnUse ().getDamageLower (item
                    .getItem ().getLevel ()), ench.getOnUse ()
                    .getDamageUpper (item.getItem ().getLevel ()));
          }
        }
        if (null != ench.getOnUse ().getBoost ())
        {
          for (String statBoost : ench
                  .getOnUse ().getBoost ().keySet ())
          {
            if (ench.getOnUse ().getBoost (statBoost, item.getItem ()
                    .getLevel ()) > 0)
            {
              itemText += "\n  " + Localizer.getString ("item.use.stat.boost."
                      + statBoost, ench.getOnUse ().getBoost 
                  (statBoost, item.getItem ().getLevel ()));
            }
          }
        }
        for (halemaster.ee.world.entity.Effect effect : ench
              .getOnUse ().getEffects ())
        {
          itemText += "\n  " + Localizer.getString ("item.use.stat.effect", 
                  (Object) Localizer.getString(effect.getName ()), 
                  effect.getLasts (), effect.getCurrentStack (),
                  effect.getStack ());
        }
      }
    }
    
    itemText += "\n" + Localizer.getString ("item.cost", 
            item.getItem ().getCost ());

    back.getEffects (EffectEventId.onHover, Hint.class).get (0)
            .getParameters ().setProperty ("hintText", itemText);
  }
  
  public void updateEquipment ()
  {
    Inventory inv = this.player.getPlayer ().getInventory ();
    Element image;
    Element back;
    ItemSet item;
    
    for (int i = 0; i < Equipment.values ().length; i++)
    {
      image = getNifty ().getCurrentScreen ().findElementByName ("equip_" + 
              Equipment.values ()[i].name ()+ "_image");
      back = getNifty ().getCurrentScreen ().findElementByName ("equip_" + 
              Equipment.values ()[i].name () + "_back");
      item = inv.getEquip (Equipment.values ()[i]);
      if (null == item)
      {
        image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage ("Interface/Images/skill_blank.png", false));
        back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                .getParameters ().setProperty ("hintText", "  ");
      }
      else
      {
        image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (item.getItem ().getImage ()
                , false));
        setItemHint (item, back);
      }
    }
  }
  
  /**
   * Called when the player submits text
   * @param id id of the chat box
   * @param event event submitted.
   */
  @NiftyEventSubscriber(id="chatArea")
  public final void onSendText(final String id, final ChatTextSendEvent event) 
  {
    if (!event.getText().isEmpty ()) 
    {
      chatLine (this.player.getPlayer (), event.getText ());
    }
  }
  
  /**
   * Add a user to the chat adding queue
   * @param user user to add
   */
  public void addChatUser (Entity user)
  {
    if (null != user)
    {
      this.addedUsers.add (user);
    }
  }
  
  /**
   * Add a user to the chat removing queue
   * @param user user to remove
   */
  public void removeChatUser (Entity user)
  {
    if (null != user)
    {
      this.removedUsers.add (user);
    }
  }
  
  /**
   * add a user to the chat
   * @param user user to add
   */
  private void finalizeAddChatUser (Entity user)
  {
    if (user.getType ().equals (EntityType.NPC.getId ()) || 
            user.getType ().equals (EntityType.PLAYER.getId ()))
    {
      if (null != getNifty () && null != getNifty ().getCurrentScreen ())
      {
        final Chat chat = getNifty ().getCurrentScreen ()
                .findNiftyControl("chatArea", Chat.class);
        if (null != chat)
        {
          chat.addPlayer(user.getName (), getNifty ()
                  .createImage (user.getBasicImage (), false));
        }
        else
        {
          this.addedUsers.add (user);
        }
      }
      else
      {
        this.addedUsers.add (user);
      }
    }
  }
  
  /**
   * Remove a user from the chat
   * @param user user to remove
   */
  private void finalizeRemoveChatUser (Entity user)
  {
    if (user.getType ().equals (EntityType.NPC.getId ()) || 
            user.getType ().equals (EntityType.PLAYER.getId ()))
    {
      if (null != getNifty () && null != getNifty ().getCurrentScreen ())
      {
        final Chat chat = getNifty ().getCurrentScreen ()
                .findNiftyControl("chatArea", Chat.class);
        if (null != chat)
        {
          chat.removePlayer (user.getName ());
        }
        else
        {
          this.removedUsers.add (user);
        }
      }
      else
      {
        this.removedUsers.add (user);
      }
    }
  }
  
  /**
   * Add a line of chat from the given user
   * @param user user of the chat
   * @param line line from that user
   */
  public void chatLine (Entity user, String line)
  {
    if (null != getNifty () && null != getNifty ().getCurrentScreen ())
    {
      final Chat chat = getNifty ().getCurrentScreen ()
              .findNiftyControl("chatArea", Chat.class);
      if (null != chat)
      {
        chat.receivedChatLine (user.getLocalizedName () + "> " + line, getNifty ()
                .createImage (user.getBasicImage (), false));
      }
    }
  }
  
  public Entity getTarget ()
  {
    return this.target;
  }
  
  /**
   * Set the target in the HUD
   * @param target the target in the hud
   */
  public void setTarget (Entity target)
  {
    Element targetPanel;
    Label targetName;
    Element targetImage;
    NiftyImage targetNiftyImage;
    
    if (null != this.target)
    {
      this.target.setData (ShopAI.SHOPPING, String.valueOf(false));
    }
    
    this.target = target;
    
    targetPanel = getNifty ().getCurrentScreen ().findElementByName ("target");
    
    if (null == this.target)
    {
      targetPanel.hide ();
    }
    else
    {
      targetPanel.show ();
      
      targetName = getNifty ().getCurrentScreen ()
            .findNiftyControl ("target_name", Label.class);
      targetImage = getNifty ().getCurrentScreen ().findElementByName 
            ("target_image");
      targetNiftyImage = getNifty ()
            .createImage (this.target.getBasicImage (), false);
      
      targetName.setText (this.target.getLocalizedName () + " LV. " + 
              this.target.getStat (Statistic.LEVEL));
      targetImage.getRenderer (ImageRenderer.class).setImage (targetNiftyImage); 
    }
    
    openShop ();
  }
  
  @NiftyEventSubscriber (id="questLog")
  public void onQuestLogChanged (final String id, 
          final ListBoxSelectionChangedEvent<String> event)
  {
    Label title, description, requirement;
    List<Integer> selections = event.getSelectionIndices ();
    Integer selection;
    
    if (selections.size () > 0)
    {
      selection = selections.get (0);
      title = getNifty ().getCurrentScreen ()
              .findNiftyControl ("currentQuestTitle", Label.class);
      description = getNifty ().getCurrentScreen ()
              .findNiftyControl ("currentQuestDescription", Label.class);
      requirement = getNifty ().getCurrentScreen ()
              .findNiftyControl ("questRequirements", Label.class);
      title.setText (this.titles.get (selection));
      description.setText (this.descriptions.get (selection));
      requirement.setText (this.requirements.get (selection));
    }
  }
  
  /**
   * Key input event for console toggle
   * @author nifty-gui example code
   * @param inputEvent nifty input event
   * @return whether the event was used
   */
  public boolean keyEvent (final NiftyInputEvent inputEvent)
  {
    boolean consumed = false;
    if (inputEvent == NiftyInputEvent.ConsoleToggle)
    {
      toggleConsole ();
      consumed = true;
    }
    else if (inputEvent == NiftyInputEvent.MoveCursorUp)
    {
      if (this.consoleVisible)
      {
        Console console = getScreen ().findNiftyControl ("console", Console.class);
        if (this.previousCommands.size () > this.currentCommand)
        {
          console.getTextField ().setText (this.previousCommands
                  .get (this.currentCommand));
          this.currentCommand++;
        }
        consumed = true;
      }
    }
    else if (inputEvent == NiftyInputEvent.MoveCursorDown)
    {
      if (this.consoleVisible)
      {
        Console console = getScreen ().findNiftyControl ("console", Console.class);
        if (0 < this.currentCommand && 0 < this.previousCommands.size ())
        {
          this.currentCommand--;
          console.getTextField ().setText (this.previousCommands
                  .get (this.currentCommand));
        }
        consumed = true;
      }
    }
    else if (inputEvent == NiftyInputEvent.NextInputElement)
    {
      if (this.consoleVisible)
      {
        Console console = getScreen ().findNiftyControl ("console", Console.class);
        console.getTextField ().setText (Command.getLongestPrefix 
                  (console.getTextField ().getDisplayedText ()));
        console.getTextField ().setFocus ();
        console.getTextField ().setCursorPosition (console.getTextField ()
                .getDisplayedText ().length ());
        consumed = true;
      }
    }
    
    return consumed;
  }
  
  /**
   * Toggle the console.
   * @author nifty-gui examples
   */
  public void toggleConsole ()
  {
    if (allowConsoleToggle)
    {
      allowConsoleToggle = false;
      if (consoleVisible)
      {
        closeConsole ();
      }
      else
      {
        openConsole ();
      }
    }
  }
  
  /**
   * Open the console.
   * @author nifty-gui examples
   */
  private void openConsole ()
  {
    getNifty ().showPopup (getScreen (), this.consolePopup.getId (), 
            this.consolePopup.findElementByName ("console#textInput"));
    getScreen ().processAddAndRemoveLayerElements ();
    
    if (this.firstConsoleShow)
    {
      this.firstConsoleShow = false;
      Console console = getScreen ().findNiftyControl ("console", Console.class);
      console.output (INITIAL_OUTPUT);
    }
    
    this.consoleVisible = true;
    this.allowConsoleToggle = true;
  }
  
  /**
   * Close the console.
   * @author nifty-gui examples
   */
  private void closeConsole ()
  {
    getNifty ().closePopup (this.consolePopup.getId (), new EndNotify ()
    {
      @Override
      public void perform ()
      {
        consoleVisible = false;
        allowConsoleToggle = true;
      }
    });
  }
  
  /**
   * Execute a console command
   * @param id id of the console
   * @param command command event executed
   */
  @NiftyEventSubscriber (id="console")
  public void onConsoleCommand (final String id, 
          final ConsoleExecuteCommandEvent commandEvent)
  {
    Console console = getScreen ().findNiftyControl ("console", Console.class);
    Command command = Command.findCommand (commandEvent.getCommand ());
    String commandFull = commandEvent.getCommand ();
    
    for (String arg : commandEvent.getArguments ())
    {
      commandFull += " " + arg;
    }
    
    this.previousCommands.add (0, commandFull);
    this.currentCommand = 0;
    
    if (null != command)
    {
      if (!command.execute (commandEvent.getArguments (), this.player, console))
      {
        printError (console, USAGE + command.getUsage ());
      }
    }
    else
    {
      printError (console, INVALID_COMMAND);
    }
  }
  
  /**
   * Print an error to the console
   * @param console console to print to
   * @param error error to print
   */
  private void printError (Console console, String error)
  {
    console.outputError (ERROR + error);
  }

  /**
   * Called when we drag something into a droppable registered with this hud
   * @param source source droppable
   * @param draggable draggable dragged
   * @param destination destination droppable
   * @return 
   */
  public boolean accept (Droppable source, Draggable draggable,
          Droppable destination)
  {
    ItemSet item = null;
    ItemSet itemAtDest;
    String itemLoc, itemDestLoc;
    
    if (destination.getId ().startsWith ("skill"))
    {
      // item to skill drop
      if (source.getId ().startsWith ("inv"))
      {
        // inv source
        itemLoc = source.getId ().substring (4);
        item = this.player.getPlayer ().getInventory ().getItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
      }
      else if (source.getId ().startsWith ("equip"))
      {
        // equip source
        itemLoc = source.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().getEquip (Equipment
                .valueOf (itemLoc));
      }
      
      setSkill (Integer.valueOf(destination.getId ().substring (6)), item);
    }
    else if (destination.getId ().startsWith ("inv"))
    {
      // item to inventory drop
      if (source.getId ().startsWith ("inv"))
      {
        // inv source
        itemLoc = source.getId ().substring (4);
        itemDestLoc = destination.getId ().substring (4);
        if (!itemLoc.equals (itemDestLoc))
        {
          this.player.getPlayer ().getInventory ().swapItems (Integer.valueOf (
                  itemLoc.split (",")[1]), Integer.valueOf (itemLoc.split (",")[0]
                  ), Integer.valueOf (itemDestLoc.split (",")[1]), Integer.
                  valueOf (itemDestLoc.split (",")[0]));
        }
      }
      else if (source.getId ().startsWith ("equip"))
      {
        // equip source
        itemLoc = destination.getId ().substring (4);
        itemDestLoc = source.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().getItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
        if (null == item || item.getItem ().isEquippable (itemDestLoc))
        {
          this.player.getPlayer ().getInventory ().takeItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
          itemAtDest = this.player.getPlayer ().getInventory ().takeEquip 
                  (Equipment.valueOf (itemDestLoc));
          this.player.getPlayer ().getInventory ().setEquip (Equipment.valueOf 
                  (itemDestLoc), item);
          this.player.getPlayer ().getInventory ().putItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]), itemAtDest);
        }
      }
      else if (source.getId ().equals ("shop_sell"))
      {
        Element back = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_back");
        Element image = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_image");
        itemLoc = destination.getId ().substring (4);
        item = this.player.getPlayer ().getInventory ().takeItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
          // put in sell
        returnSell ();
        this.itemSell = item;
        if (null == this.itemSell)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage ("Interface/Images/skill_blank.png", false));
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (this.itemSell.getItem ().getImage ()
                , false));
          setItemHint (item, back);
        }
      }
      
      changed ();
    }
    else if (destination.getId ().startsWith ("equip"))
    {
      // item to equipment drop
      if (source.getId ().startsWith ("inv"))
      {
        // inv source
        itemLoc = source.getId ().substring (4);
        itemDestLoc = destination.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().getItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
        if (null == item || item.getItem ().isEquippable (itemDestLoc))
        {
          this.player.getPlayer ().getInventory ().takeItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
          itemAtDest = this.player.getPlayer ().getInventory ().takeEquip 
                  (Equipment.valueOf (itemDestLoc));
          this.player.getPlayer ().getInventory ().setEquip (Equipment.valueOf 
                  (itemDestLoc), item);
          this.player.getPlayer ().getInventory ().putItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]), itemAtDest);
        }
      }
      else if (source.getId ().startsWith ("equip"))
      {
        // equip source
        itemLoc = source.getId ().substring (6);
        itemDestLoc = destination.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().getEquip (Equipment
                .valueOf (itemLoc));
        itemAtDest = this.player.getPlayer ().getInventory ().getEquip (
                Equipment.valueOf (itemDestLoc));
        if ((null == item || item.getItem ().isEquippable (itemDestLoc)) &&
                (null == itemAtDest || itemAtDest.getItem ().isEquippable 
                (itemLoc)))
        {
          this.player.getPlayer ().getInventory ().takeEquip (Equipment
                  .valueOf (itemLoc));
          this.player.getPlayer ().getInventory ().takeEquip 
                  (Equipment.valueOf (itemDestLoc));
          this.player.getPlayer ().getInventory ().setEquip (Equipment.valueOf 
                  (itemDestLoc), item);
          this.player.getPlayer ().getInventory ().setEquip (Equipment.valueOf
                  (itemLoc), itemAtDest);
        }
      }
      else if (source.getId ().equals ("shop_sell"))
      {
        Element back = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_back");
        Element image = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_image");
        itemLoc = destination.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().takeEquip (Equipment
                .valueOf (itemLoc));
          // put in sell
        returnSell ();
        this.itemSell = item;
        if (null == this.itemSell)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage ("Interface/Images/skill_blank.png", false));
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (this.itemSell.getItem ().getImage ()
                , false));
          setItemHint (item, back);
        }
      }
      
      changed ();
    }
    else if (destination.getId ().equals ("item_delete"))
    {
      // item to drop from inventory
      if (source.getId ().startsWith ("inv"))
      {
        // inv source
        itemLoc = source.getId ().substring (4);
        item = this.player.getPlayer ().getInventory ().takeItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
        if (null != item)
        {
          this.player.getWorld ().addGroundItemNearEntity (item,
                  this.player.getPlayer ());
        }
      }
      else if (source.getId ().startsWith ("equip"))
      {
        // equip source
        itemLoc = source.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().takeEquip (Equipment
                .valueOf (itemLoc));
        if (null != item)
        {
          this.player.getWorld ().addGroundItemNearEntity (item,
                  this.player.getPlayer ());
        }
      }
      
      changed ();
    }
    else if (destination.getId ().equals ("shop_sell"))
    {
      Element back = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_back");
      Element image = getNifty ().getCurrentScreen ().findElementByName 
              ("shop_sell_image");
      // item to drop from inventory
      if (source.getId ().startsWith ("inv"))
      {
        // inv source
        itemLoc = source.getId ().substring (4);
        item = this.player.getPlayer ().getInventory ().takeItem (Integer
                .valueOf (itemLoc.split (",")[1]), Integer.valueOf 
                (itemLoc.split (",")[0]));
          // put in sell
        returnSell ();
        this.itemSell = item;
        if (null == this.itemSell)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage ("Interface/Images/skill_blank.png", false));
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (this.itemSell.getItem ().getImage ()
                , false));
          setItemHint (item, back);
        }
      }
      else if (source.getId ().startsWith ("equip"))
      {
        // equip source
        itemLoc = source.getId ().substring (6);
        item = this.player.getPlayer ().getInventory ().takeEquip (Equipment
                .valueOf (itemLoc));
          // put in sell
        returnSell ();
        this.itemSell = item;
        if (null == this.itemSell)
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage ("Interface/Images/skill_blank.png", false));
          back.getEffects (EffectEventId.onHover, Hint.class).get (0)
                  .getParameters ().setProperty ("hintText", "  ");
        }
        else
        {
          image.getRenderer (ImageRenderer.class).setImage (getNifty ()
                .createImage (this.itemSell.getItem ().getImage ()
                , false));
          setItemHint (item, back);
        }
      }
    }
    
    return false;
  }
  
  public void setSkill (int index, ItemSet item)
  {
    if (null == item)
    {
      setSkill (index, (Item) null);
    }
    else if (item.getItem ().getUsable ())
    {
      setSkill (index, item.getItem ());
    }
  }
  
  public void setSkill (int index, Item item)
  {
    Element destImage = getNifty ().getCurrentScreen ().findElementByName 
            ("skill-" + index + "-image");
    Element hover = getNifty ().getCurrentScreen ().findElementByName 
            ("skill-" + index );
    String image = "Interface/Images/skill_blank.png";
    
    this.skills[index] = item;
    if (null != item)
    {
      image = item.getImage ();
    }
    
    destImage.getRenderer (ImageRenderer.class).setImage (getNifty ()
            .createImage (image, false));
    if (null != item)
    {
      hover.getEffects (EffectEventId.onHover, Hint.class).get (0)
              .getParameters().setProperty("hintText", Localizer.getString 
              (item.getName (), item.getLevel ()));
    }
    else
    {
      hover.getEffects (EffectEventId.onHover, Hint.class).get (0)
              .getParameters().setProperty("hintText", "   ");
    }
  }
  
  public Item getSkill (int index)
  {
    return this.skills[index];
  }
  
  public Item[] getSkills ()
  {
    return this.skills;
  }
  
  public boolean skill (String value)
  {
    int index = Integer.valueOf (value);
    
    if (null != this.skills[index])
    {
      this.player.getPlayer ().getInventory ().useItem (this.skills[index], 
              this.player.getPlayer ());
    }
    
    return true;
  }
}
