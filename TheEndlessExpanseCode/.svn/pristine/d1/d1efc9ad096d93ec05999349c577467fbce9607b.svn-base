package halemaster.ee;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.micro.AreaGenerator;

/**
 * @name Overlay
 * 
 * @version 0.0.0
 * 
 * @date Mar 19, 2014
 */
public class Overlay 
{
  public static final int OVERLAY_LAYER = Entity.ENTITY_LAYER + 10;
  public static final String OVERLAY_ANIMATION = "overlay";
  public static final float CLOSING_DISTANCE = AreaGenerator.TILE_WIDTH;
  
  private Sprite sprite;
  private Game game;
  private AudioNode sound;
  private Entity destination;
  private float speed;
  private boolean clearAnimation;
  private boolean clearSound;
  private float clearTime;
  private float fps;
  
  public Overlay (String name, float fps, int width, int height, 
          String[] animation, Game game, float speed, Vector2f location, 
          Entity destination, String sound)
  {
    this.game = game;
    this.sprite = new Sprite (name, game.getAssetManager (), fps, OVERLAY_LAYER
            , width, height);
    this.speed = speed;
    this.fps = fps;
    this.destination = destination;
    this.sprite.addAnimation (OVERLAY_ANIMATION, animation);
    this.sprite.setAnimation (OVERLAY_ANIMATION);
    this.sprite.getImage ().setLocalTranslation (location.x, this.sprite
            .getImage ().getLocalTranslation ().y, location.y);
    
    if (null != sound)
    {
      this.sound = new AudioNode (this.game.getAssetManager (), sound);
      this.sound.setReverbEnabled (false);
    }
  }
  
  /**
   * Start the overlay animation
   * @param game game to start in
   */
  public void start ()
  {
    this.game.addOverlay (this);
    this.clearAnimation = false;
    this.clearSound = true;
    if (null != this.sound)
    {
      this.sound.play ();
      this.clearSound = false;
    }
  }
  
  /**
   * Update the Overlay
   * @param tpf delta since last update
   */
  public void update (float tpf)
  {
    boolean finished;
    
    finished = this.sprite.update (tpf);
    if (null != this.destination)
    {
      // move towards destination entity
      Vector2f pointLocation = this.destination.getAbsoluteLocation ().clone ();
      Vector2f myLocation = new Vector2f (this.sprite.getImage ()
              .getLocalTranslation ().x, this.sprite.getImage ()
              .getLocalTranslation ().z);
      Quaternion rotate = new Quaternion ()
              .fromAngleAxis (FastMath.atan2 (myLocation.y - pointLocation.y,
              pointLocation.x - myLocation.x), Vector3f.UNIT_Y);
      
      this.sprite.getImage ().setLocalRotation (rotate);
      this.sprite.getImage ().rotate ((float) (3 * Math.PI / 2), 0, 0f);
      this.sprite.move (rotate.getRotationColumn (0).x * tpf * this.speed, 
              rotate.getRotationColumn (0).z * tpf * this.speed);
      if (myLocation.distance (pointLocation) <= CLOSING_DISTANCE)
      {
        finished = true;
        clearTime = 1.0f / this.fps;
      }
      else
      {
        finished = false;
      }
    }
    if (finished && !this.clearAnimation)
    {
      this.clearAnimation = true;
      this.clearTime = 0;
    }
    
    if (null != this.sound && !this.clearSound &&
            this.sound.getStatus () == AudioSource.Status.Stopped)
    {
      this.clearSound = true;
    }
    
    if (this.clearAnimation && this.clearSound)
    {
      this.clearTime += tpf;
      if (this.clearTime >= 1.0f / this.fps)
      {
        end ();
      }
    }
  }
  
  public Sprite getSprite ()
  {
    return this.sprite;
  }
  
  public AudioNode getSound ()
  {
    return this.sound;
  }
  
  /**
   * Disconnect from the game
   */
  private void end ()
  {
    this.game.removeOverlay (this);
  }
}
