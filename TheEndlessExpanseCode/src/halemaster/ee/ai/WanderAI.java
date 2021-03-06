package halemaster.ee.ai;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import halemaster.ee.Game;
import halemaster.ee.world.Area;
import halemaster.ee.world.entity.Entity;
import java.util.Random;

/**
 * @name WanderAI
 * 
 * @version 0.0.0
 * 
 * @date Feb 20, 2014
 */
public class WanderAI extends AI
{
  public static final int DEFAULT_RADIUS = 5;
  public static final float DEFAULT_FREQ = .15f;
  
  private Area centerLocation;
  private Area centerPoint;
  private int radius;
  private float frequency;
  private float passed = 0.0f;

  public Area getCenterLocation ()
  {
    return centerLocation;
  }

  public void setCenterLocation (Area centerLocation)
  {
    this.centerLocation = centerLocation;
  }

  public Area getCenterPoint ()
  {
    return centerPoint;
  }

  public void setCenterPoint (Area centerPoint)
  {
    this.centerPoint = centerPoint;
  }

  public int getRadius ()
  {
    return radius;
  }

  public void setRadius (int radius)
  {
    this.radius = radius;
  }

  public float getFrequency ()
  {
    return frequency;
  }

  public void setFrequency (float frequency)
  {
    this.frequency = frequency;
  }
  
  /**
   * Attach the given entity to the ai
   * @param entity entity to attach
   */
  @Override
  public void attachEntity (Entity entity)
  {
    setCenterPoint (new Area (entity.getX (), entity.getY ()));
    setCenterLocation (entity.getLocation ());
    setRadius (DEFAULT_RADIUS);
    setFrequency (DEFAULT_FREQ);
    
    super.attachEntity (entity);
  }
  
  /**
   * Detach the currently attached entity
   */
  @Override
  public void detachEntity ()
  {
    if (null != getEntity ())
    {
      getEntity().setX (this.centerPoint.getX ());
      getEntity().setY (this.centerPoint.getY ());
      getEntity().setLocation (this.centerLocation);
    }
    
    super.detachEntity ();
  }
  
  /**
   * Called when Update is called
   * @param tpf delta since last update
   */
  @Override
  protected void controlUpdate (float tpf)
  {
    Random random; // this is NOT based on seed by design!
    MovementAI move;
    int xMove, yMove;
    if (null != getEntity ())
    {
      Area entityLoc = getEntity ().getExactLocation ();

      this.passed += tpf;
      if (this.passed >= 1.0f)
      {
        random = new Random ();
        if (random.nextFloat ()< this.frequency)
        {
          xMove = random.nextInt ((this.radius * 2)) - this.radius;
          yMove = random.nextInt ((this.radius * 2)) - this.radius;

          move = (MovementAI) getEntity ().getAI (Game.MOVEMENT_AI);

          move.setMoveTo (new Area (entityLoc.getX () + xMove, 
                  entityLoc.getY () + yMove));
        }

        this.passed = 0.0f;
      }
    }
  }

  /**
   * Not used. Called for Render control
   * @param rm render manager
   * @param vp view Point
   */
  @Override
  protected void controlRender (RenderManager rm, ViewPort vp)
  {
    vp.setClearColor (false);
  }
}
