package halemaster.ee.world.terrain.structure;

/**
 * @name Structure
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public class Structure 
{
  Architecture arch;
  float chance;

  public Architecture getArch ()
  {
    return arch;
  }

  public void setArch (Architecture arch)
  {
    this.arch = arch;
  }

  public float getChance ()
  {
    return chance;
  }

  public void setChance (float chance)
  {
    this.chance = chance;
  }
}
