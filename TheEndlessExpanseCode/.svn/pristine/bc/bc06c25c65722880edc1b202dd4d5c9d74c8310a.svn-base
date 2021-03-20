package halemaster.ee.world.entity;

import halemaster.ee.Sprite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @name UseAnimation
 * 
 * @version 0.0.0
 * 
 * @date Mar 19, 2014
 */
public class UseAnimation 
{
  public static final String ON = "on";
  public static final String TOWARDS = "towards";
  
  private List<List<String>> animations;
  private String sound;
  private float fps;
  private float speed;
  private String type;
  private int width;
  private int height;
  
  /**
   * default constructor. generally for use with Json!
   */
  public UseAnimation ()
  {
    this (0, 0, ON, null, Sprite.DEFAULT_BOX, Sprite.DEFAULT_BOX);
  }

  public UseAnimation (float fps, float speed, 
          String type, String sound, int width, int height, 
          String[] ... animations)
  {
    this.animations = new ArrayList<List<String>>();
    for (String[] animation : animations)
    {
      List<String> animateList = new ArrayList<String> ();
      animateList.addAll (Arrays.asList (animation));
      this.animations.add (animateList);
    }
    this.fps = fps;
    this.speed = speed;
    this.type = type;
    this.width = width;
    this.height = height;
    this.sound = sound;
  }
  
  public String getSound ()
  {
    return this.sound;
  }
  
  public void setSound (String sound)
  {
    this.sound = sound;
  }
  
  public List<List<String>> getAnimations ()
  {
    return animations;
  }

  public void setAnimations (List<List<String>> animations)
  {
    this.animations = animations;
  }

  public float getFps ()
  {
    return fps;
  }

  public void setFps (float fps)
  {
    this.fps = fps;
  }

  public float getSpeed ()
  {
    return speed;
  }

  public void setSpeed (float speed)
  {
    this.speed = speed;
  }

  public String getType ()
  {
    return type;
  }

  public void setType (String type)
  {
    this.type = type;
  }

  public int getWidth ()
  {
    return width;
  }

  public void setWidth (int width)
  {
    this.width = width;
  }

  public int getHeight ()
  {
    return height;
  }

  public void setHeight (int height)
  {
    this.height = height;
  }
}
