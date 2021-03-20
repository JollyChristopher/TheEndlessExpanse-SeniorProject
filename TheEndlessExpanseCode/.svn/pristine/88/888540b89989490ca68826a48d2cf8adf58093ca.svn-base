package halemaster.ee.world;

import halemaster.ee.state.Loader;
import java.util.Random;

/**
 * @name NoiseCannon
 * 
 * @version 0.0.0
 * 
 * @date Oct 30, 2013
 * 
 * This file contains Setter functions that accept two variables. These are 
 * still simple Setter functions, but for the two variables that they relate
 * to, and do not require comments.
 */
public final class NoiseCannon 
{
  private static final double OUTER = 0.10;
  private static final double MIDDLE_OUTER = 0.15;
  private static final double MIDDLE_INNER = 0.25;
  private static final double INNER = 0.50;
  private static final double[][] KERNEL = {
    {OUTER / 24, OUTER / 24, OUTER / 24, OUTER / 24, OUTER / 24, 
      OUTER / 24, OUTER / 24},
    {OUTER / 24, MIDDLE_OUTER / 16, MIDDLE_OUTER / 16, MIDDLE_OUTER / 16,
      MIDDLE_OUTER / 16, MIDDLE_OUTER / 16, OUTER / 24},
    {OUTER / 24, MIDDLE_OUTER / 16, MIDDLE_INNER / 9, MIDDLE_INNER / 9, 
      MIDDLE_INNER / 9, MIDDLE_OUTER / 16, OUTER / 24},
    {OUTER / 24, MIDDLE_OUTER / 16, MIDDLE_INNER / 9, INNER, MIDDLE_INNER / 9, 
      MIDDLE_OUTER / 16, OUTER / 24},
    {OUTER / 24, MIDDLE_OUTER / 16, MIDDLE_INNER / 9, MIDDLE_INNER / 9, 
      MIDDLE_INNER / 9, MIDDLE_OUTER / 16, OUTER / 24},
    {OUTER / 24, MIDDLE_OUTER / 16, MIDDLE_OUTER / 16, MIDDLE_OUTER / 16, 
      MIDDLE_OUTER / 16, MIDDLE_OUTER / 16, OUTER / 24},
    {OUTER / 24, OUTER / 24, OUTER / 24, OUTER / 24, OUTER / 24, 
      OUTER / 24, OUTER / 24}
  };
  private static final double CAP = 2;
  private static final double PRO_COPY = .15;
  private static final double PRO_BLAST = .45;
  private static final double PRO_BLUR = .20;
  private Random generator;
  private int lowerBlasts = -1, upperBlasts = -2;
  private int lowerRadius = -1, upperRadius = -2;
  private int lowerAmount = -1, upperAmount = -2;
  private byte lowerStrength = -1, upperStrength = -2;
  private int lowerSlope = -1, upperSlope = -2;
  private int yBorder = 1;
  private int xBorder = 1;
  private double progress;
  
  public NoiseCannon ()
  {
    this (null);
  }
  
  public NoiseCannon (Random random)
  {
    setGenerator (random);
  }
  
  /**
   * Generate noise on the given canvas and return that canvas.
   * 
   * @param canvas canvas to generate additive noise onto.
   * @return the given canvas with noise on it.
   */
  public byte[][] generateNoise (byte[][] blurCanvas)
  {
    byte[][] canvas = new byte[blurCanvas.length][blurCanvas[0].length];
    int blasts = this.generator.nextInt (this.upperBlasts - 
            this.lowerBlasts + 1) + this.lowerBlasts;
    int radius = this.generator.nextInt (this.upperRadius - 
            this.lowerRadius + 1) + this.lowerRadius;
    int particles;
    int x, centerX;
    int y, centerY;
    double tempGaussX, tempGaussY;
    
    progress = 0;
    
    for (int i = 0; i < canvas.length; i++)
    {
      System.arraycopy (blurCanvas[i], 0, canvas[i], 0, canvas[i].length);
      this.progress += PRO_COPY / canvas.length;
    }
    
    for (int i = 0; i < blasts; i++)
    {
      centerX = this.generator.nextInt (canvas.length - (this.xBorder * 2) - 
              (radius * 2)) + this.xBorder + radius;
      centerY = this.generator.nextInt (canvas[0].length - (this.yBorder * 2) - 
              (radius * 2)) + this.yBorder + radius;
      particles = this.generator.nextInt (this.upperAmount - 
              this.lowerAmount + 1) + this.lowerAmount;
      
      for (int j = 0; j < particles; j++)
      {
        tempGaussX = this.generator.nextGaussian ();
        tempGaussY = this.generator.nextGaussian ();
        if (tempGaussX <= CAP && tempGaussY <= CAP &&
                tempGaussX >= -CAP && tempGaussY >= -CAP)
        {
          x = (int) (tempGaussX * (radius / CAP) + centerX);
          y = (int) (tempGaussY * (radius / CAP) + centerY); 

          canvas = agitate (canvas, x, y);
        }
        this.progress = PRO_COPY + PRO_BLAST / (canvas.length * 
                canvas[0].length) * (i * canvas[0].length + j);
      }
    }
    
    canvas = blur (canvas);
    canvas = blur (canvas);
    
    this.progress = Loader.DONE;
    
    return canvas;
  }
  
  /**
   * Blur the spot in the array. This should happen many times during agitation.
   * 
   * @param canvas canvas to blur.
   * @param x x to blur.
   * @param y y to blur.
   */
  private byte[][] blur (byte[][] canvas)
  {
    double intermediateBlur;
    byte blurValue;
    int kernelCenter = KERNEL.length / 2;
    byte[][] blurred = new byte[canvas.length][canvas[0].length];
        
    for (int x = 0; x < canvas.length; x++)
    {
      for (int y = 0; y < canvas[x].length; y++)
      {
        intermediateBlur = 0;
        for (int kX = 0; kX < KERNEL.length; kX++)
        {
          for (int kY = 0; kY < KERNEL[kX].length; kY++)
          {
            if (x + kX - kernelCenter >= 0 && 
                    x + kX - kernelCenter < canvas.length &&
                    y + kY - kernelCenter >= 0 && 
                    y + kY - kernelCenter < canvas[x].length)
            {
              intermediateBlur += (canvas[x + (kX - kernelCenter)][y + 
                      (kY - kernelCenter)]) 
                      * KERNEL[kX][kY];
            }
            else
            {
              intermediateBlur += (canvas[x][y]) * KERNEL[kX][kY];
            }
          }
        }

        blurValue = (byte) (intermediateBlur);

        blurred[x][y] = blurValue;
        
        this.progress += PRO_BLUR / (canvas.length * canvas[x].length);
      }
    }
    
    return blurred;
  }
  
  /**
   * Slope in all directions until there are no longer any areas larger than the
   * wave.
   * 
   * @param canvas canvas to slope.
   * @param x x of the wave.
   * @param y y of the wave.
   * @return 
   */
  private byte[][] slope (byte[][] canvas, int x, int y, boolean positive)
  {
    int slope;
    
    for (int i = -1; i <= 1; i += 2)
    {      
      slope = (this.generator.nextInt (this.upperSlope - this.lowerSlope
          + 1) + this.lowerSlope);
      
      if (x + i >= 0 && x + i < canvas.length)
      {
        if (!positive && canvas[x][y] < canvas[x + i][y])
        {
          if (canvas[x][y] + slope > Byte.MAX_VALUE)
          {
            canvas[x + i][y] = Byte.MAX_VALUE;
          }
          else
          {
            canvas[x + i][y] = (byte) (canvas[x][y] + slope);
            canvas = slope (canvas, x + i, y, positive);
          }
        }
        else if (positive && canvas[x][y] > canvas[x + i][y])
        {
          if (canvas[x][y] - slope < Byte.MIN_VALUE)
          {
            canvas[x + i][y] = Byte.MIN_VALUE;
          }
          else
          {
            canvas[x + i][y] = (byte) (canvas[x][y] - slope);
            canvas = slope (canvas, x + i, y, positive);
          }
        } 
      }
      
      slope = (this.generator.nextInt (this.upperSlope - this.lowerSlope
          + 1) + this.lowerSlope);
      
      if (y + i >= 0 && y + i < canvas[0].length)
      {
        if (!positive && canvas[x][y] < canvas[x][y + i])
        {
          if (canvas[x][y] + slope > Byte.MAX_VALUE)
          {
            canvas[x][y + i] = Byte.MAX_VALUE;
          }
          else
          {
            canvas[x][y + i] = (byte) (canvas[x][y] + slope);
            canvas = slope (canvas, x, y + i, positive);
          }
        }
        else if (positive && canvas[x][y] > canvas[x][y + i])
        {
          if (canvas[x][y] - slope < Byte.MIN_VALUE)
          {
            canvas[x][y + i] = Byte.MIN_VALUE;
          }
          else
          {
            canvas[x][y + i] = (byte) (canvas[x][y] - slope);
            canvas = slope (canvas, x, y + i, positive);
          }
        } 
      }
    }
    
    return canvas;
  }
  
  /**
   * Agitate the particle by adding one to the location, then finding a resting
   * place for it.
   * 
   * @param canvas canvas to agitate on.
   * @param x x to agitate.
   * @param y y to agitate.
   */
  private byte[][] agitate (byte[][] canvas, int x, int y)
  {
    int additive = this.generator.nextInt (this.upperStrength - 
            this.lowerStrength + 1) + this.lowerStrength;
    if (canvas[x][y] + additive > Byte.MAX_VALUE)
    {
      canvas[x][y] = Byte.MAX_VALUE;
    }
    else if (canvas[x][y] + additive < Byte.MIN_VALUE)
    {
      canvas[x][y] = Byte.MIN_VALUE;
    }
    else
    {
      canvas[x][y] += additive;
    }
    
    canvas = slope (canvas, x, y, additive > 0);
    
    return canvas;
  }
  
  public double getProgress ()
  {
    return this.progress;
  }
  
  public void setGenerator (Random random)
  {
    if (null != random)
    {
      generator = random;
    }
    else
    {
      generator = new Random ();
    }
  }
  
  public void setBlastCount (int count)
  {
    setBlastCount (count, count);
  }
  
  public void setBlastCount (int lower, int upper)
  {
    this.lowerBlasts = lower;
    this.upperBlasts = upper;
  }
  
  public void setBlastRadius (int count)
  {
    setBlastRadius (count, count);
  }
  
  public void setBlastRadius (int lower, int upper)
  {
    this.lowerRadius = lower;
    this.upperRadius = upper;
  }
  
  public void setParticleCount (int count)
  {
    setParticleCount (count, count);
  }
  
  public void setParticleCount (int lower, int upper)
  {
    this.lowerAmount = lower;
    this.upperAmount = upper;
  }
  
  public void setSlope (int count)
  {
    setSlope (count, count);
  }
  
  public void setSlope (int lower, int upper)
  {
    this.lowerSlope = lower;
    this.upperSlope = upper;
  }
  
  public void setParticleStrength (byte count)
  {
    setParticleStrength (count, count);
  }
  
  public void setParticleStrength (int count)
  {
    setParticleStrength (count, count);
  }
  
  public void setParticleStrength (byte lower, byte upper)
  {
    this.lowerStrength = lower;
    this.upperStrength = upper;
  }
  
  public void setParticleStrength (int lower, int upper)
  {
    setParticleStrength ((byte) lower, (byte) upper);
  }

  public void setyBorder (int yBorder)
  {
    this.yBorder = yBorder;
  }

  public void setxBorder (int xBorder)
  {
    this.xBorder = xBorder;
  }
}
