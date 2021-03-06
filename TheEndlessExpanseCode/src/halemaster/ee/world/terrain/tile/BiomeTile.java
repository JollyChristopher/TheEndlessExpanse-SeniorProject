package halemaster.ee.world.terrain.tile;

import halemaster.ee.world.BiomeClassifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @name BiomeTile
 * 
 * @version 0.0.0
 * 
 * @date Jan 30, 2014
 */
public class BiomeTile 
{
  public static final byte NONE = 0;
  
  String biomeId;
  byte structureId;
  List<Integer> tileId = new ArrayList<Integer>();

  public static byte getStructId (int index, boolean city)
  {
    index++;
    if (city)
    {
      index *= -1;
    }
    
    return (byte) index;
  }
  
  public static boolean isCity (byte id)
  {
    if (id < NONE)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public String getBiomeId ()
  {
    return biomeId;
  }

  public void setBiomeId (String biomeId)
  {
    this.biomeId = biomeId;
  }

  public byte getStructureId ()
  {
    return structureId;
  }

  public void setStructureId (byte structureId)
  {
    this.structureId = structureId;
  }

  public Integer[] getTileIds ()
  {
    Integer[] tiles = new Integer[this.tileId.size ()];
    
    for (int i = 0; i < tiles.length; i++)
    {
      tiles[i] = this.tileId.get (i);
    }
    
    return tiles;
  }

  public void addTileId (int tileId, boolean overwrite)
  {
    int index = -1;
    boolean alreadyExists = false;
    Tile[] tiles = BiomeClassifier.getBiome (this.biomeId).getGeneration ()
            .getTiles ();
    
    for (int i = 0; !alreadyExists && -1 == index && 
            i < this.tileId.size (); i++)
    {
      if (this.tileId.get (i) == tileId)
      {
        alreadyExists = true;
      }
      else if (tiles[this.tileId.get (i)].getLayer () == 
              tiles[tileId].getLayer ())
      {
        index = i;
      }
    }
    
    if (!alreadyExists)
    {
      if (overwrite)
      {
        if (-1 != index)
        {
          this.tileId.set (index, tileId);
        }
        else
        {
          this.tileId.add (tileId);
        }
      }
      else
      {
        if (-1 == index)
        {
          this.tileId.add (tileId);
        }
      }
    }
  }
}
