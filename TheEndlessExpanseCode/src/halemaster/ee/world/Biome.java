package halemaster.ee.world;

import com.jme3.math.ColorRGBA;
import halemaster.ee.world.terrain.tile.BiomeGeneration;

/**
 * @name Biome
 * 
 * @version 0.0.0
 * 
 * @date Oct 30, 2013
 */
public final class Biome 
{
  public static final int BOUNDS = 2;
  public static final int LOWER = 0;
  public static final int UPPER = 1;
  public static final int COLOR_TYPE = 16; // HEXADECIMAL
  private static final float DIVISOR = 255;
  private String name;
  private String color; 
  private byte habituality;
  private byte[] temperature = new byte[BOUNDS];
  private byte[] precipitation = new byte[BOUNDS];
  private byte[] elevation = new byte[BOUNDS];
  private BiomeGeneration generation;
  
  public Biome (String name, String color, byte habituality,
          byte temperatureLower,
          byte temperatureUpper, byte precipitationLower, 
          byte precipitationUpper, byte elevationLower, byte elevationUpper,
          BiomeGeneration generation)
  {
    setName (name);
    setColor (color);
    setLowerTemperature (temperatureLower);
    setUpperTemperature (temperatureUpper);
    setLowerPrecipitation (precipitationLower);
    setUpperPrecipitation (precipitationUpper);
    setLowerElevation (elevationLower);
    setUpperElevation (elevationUpper);
    setHabituality (habituality);
    setGeneration (generation);
  }

  public BiomeGeneration getGeneration ()
  {
    return generation;
  }

  public void setGeneration (BiomeGeneration generation)
  {
    this.generation = generation;
  }

  public String getName ()
  {
    return this.name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  public byte getHabituality ()
  {
    return habituality;
  }

  public void setHabituality (byte habituality)
  {
    this.habituality = habituality;
  }
  
  public String getColorString ()
  {
    return this.color;
  }

  /**
   * Get the JME3 color from the hex string. The string better be formed
   * correctly!
   * 
   * @return the color of the biome.
   */
  public ColorRGBA getColor ()
  {
    long theColor = Long.parseLong (this.color, COLOR_TYPE);
    float red =   ((theColor & 0xff000000) / 
            Long.parseLong ("ff000000", COLOR_TYPE)) / DIVISOR;
    float green = ((theColor & 0x00ff0000) / 
            Long.parseLong ("00ff0000", COLOR_TYPE)) / DIVISOR;
    float blue =  ((theColor & 0x0000ff00) / 
            Long.parseLong ("0000ff00", COLOR_TYPE)) / DIVISOR;
    float alpha = ((theColor & 0x000000ff) / 
            Long.parseLong ("000000ff", COLOR_TYPE)) / DIVISOR;
    
    return new ColorRGBA (red, green, blue, alpha);
  }
  
  public int getRed ()
  {
    return (int) ((Long.parseLong (this.color, COLOR_TYPE) & 0xff000000) 
            >> (6 * 4));
  }
  
  public int getBlue ()
  {
    return (int) ((Long.parseLong (this.color, COLOR_TYPE) & 0x0000ff00)
            >> (2 * 4));
  }
  
  public int getGreen ()
  {
    return (int) ((Long.parseLong (this.color, COLOR_TYPE) & 0x00ff0000)
            >> (4 * 4));
  }
  
  public int getAlpha ()
  {
    return (int) (Long.parseLong (this.color, COLOR_TYPE) & 0x000000ff);
  }

  public void setColor (String color)
  {
    this.color = color;
  }

  public byte getLowerTemperature ()
  {
    return this.temperature[LOWER];
  }

  public void setLowerTemperature (byte lowerTemperature)
  {
    this.temperature[LOWER] = lowerTemperature;
  }

  public byte getUpperTemperature ()
  {
    return this.temperature[UPPER];
  }

  public void setUpperTemperature (byte upperTemperature)
  {
    this.temperature[UPPER] = upperTemperature;
  }

  public byte getLowerPrecipitation ()
  {
    return this.precipitation[LOWER];
  }

  public void setLowerPrecipitation (byte lowerPrecipitation)
  {
    this.precipitation[LOWER] = lowerPrecipitation;
  }

  public byte getUpperPrecipitation ()
  {
    return this.precipitation[UPPER];
  }

  public void setUpperPrecipitation (byte upperPrecipitation)
  {
    this.precipitation[UPPER] = upperPrecipitation;
  }

  public byte getLowerElevation ()
  {
    return this.elevation[LOWER];
  }

  public void setLowerElevation (byte lowerElevation)
  {
    this.elevation[LOWER] = lowerElevation;
  }

  public byte getUpperElevation ()
  {
    return this.elevation[UPPER];
  }

  public void setUpperElevation (byte upperElevation)
  {
    this.elevation[UPPER] = upperElevation;
  }
}
