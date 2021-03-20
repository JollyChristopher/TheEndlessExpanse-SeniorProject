package halemaster.ee.Creation.macro.terrain;

import halemaster.ee.world.NoiseCannon;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestNoiseCannon
 * 
 * @version 0.0.0
 * 
 * @date Nov 3, 2013
 */
public class TestNoiseCannon 
{
  @Test
  public void basicNoise ()
  {
    final int SIZE = 100;
    byte[][] canvas = new byte[SIZE][SIZE];
    NoiseCannon cannon = new NoiseCannon ();
    boolean same = true;
    
    for (int i = 0; i < SIZE; i++)
    {
      for (int j = 0; j < SIZE; j++)
      {
        canvas[i][j] = Byte.MIN_VALUE;
      }
    }
    
    cannon.setBlastCount (1,1);
    cannon.setBlastRadius (SIZE / 3, SIZE / 3);
    cannon.setParticleCount (100, 500);
    cannon.setParticleStrength (1, 50);
    cannon.setSlope (1, 5);
    
    canvas = cannon.generateNoise (canvas);
    
    for (int i = 0; i < SIZE; i++)
    {
      for (int j = 0; j < SIZE; j++)
      {
        if (canvas[i][j] != Byte.MIN_VALUE)
        {
          same = false;
        }
      }
    }
    
    Assert.assertFalse (same);
  }
  
  @Test
  public void missingData ()
  {
    final int SIZE = 100;
    byte[][] canvas = new byte[SIZE][SIZE];
    NoiseCannon cannon = new NoiseCannon ();
    
    for (int i = 0; i < SIZE; i++)
    {
      for (int j = 0; j < SIZE; j++)
      {
        canvas[i][j] = Byte.MIN_VALUE;
      }
    }
    
    try
    {
      cannon.generateNoise (canvas);
      Assert.fail ("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      
    }
    
    cannon.setBlastCount (1,1);
    try
    {
      cannon.generateNoise (canvas);
      Assert.fail ("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      
    }
    
    cannon.setBlastRadius (SIZE / 3, SIZE / 3);
    try
    {
      cannon.generateNoise (canvas);
      Assert.fail ("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      
    }
    
    cannon.setParticleCount (100, 500);
    try
    {
      cannon.generateNoise (canvas);
      Assert.fail ("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      
    }
    
    cannon.setParticleStrength (1, 50);
    try
    {
      cannon.generateNoise (canvas);
      Assert.fail ("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      
    }
    
    cannon.setSlope (1, 5);
    cannon.generateNoise (canvas);
  }
  
  @Test
  public void nullArray ()
  {
    NoiseCannon cannon = new NoiseCannon ();
    
    try
    {
      cannon.generateNoise (null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
  }
}
