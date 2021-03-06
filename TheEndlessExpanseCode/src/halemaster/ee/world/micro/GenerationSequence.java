package halemaster.ee.world.micro;

import halemaster.ee.world.Area;
import halemaster.ee.world.entity.Entity;
import halemaster.ee.world.entity.EntityGenerator;
import halemaster.ee.world.faction.Faction;
import halemaster.ee.world.history.HistoryGenerator;
import halemaster.ee.world.terrain.tile.BiomeTile;

/**
 * @name GenerationSequence
 * 
 * @version 0.0.0
 * 
 * @date Jan 28, 2014
 */
public class GenerationSequence implements Runnable
{
  private HistoryGenerator history;
  private Entity closestPlayer;
  private BiomeTile[][] area;
  private Entity[] entities;
  private boolean finished = false;
  
  public GenerationSequence (HistoryGenerator history, Entity closestPlayer)
  {
    this.history = history;
    this.closestPlayer = closestPlayer;
  }
  
  public HistoryGenerator getHistory ()
  {
    return this.history;
  }
  
  public BiomeTile[][] getArea ()
  {
    return this.area;
  }
  
  public Entity[] getEntities ()
  {
    return this.entities;
  }
  
  public boolean isDone ()
  {
    return this.finished;
  }

  public void run ()
  {
    this.history.run ();
    this.area = AreaGenerator.generate (this.history
            .getCreated ().getWorld ()[this.history.getLeft ()]
            [this.history.getTop ()], 
            this.history.getRandom (), Faction.settlementAt (this.history
            .getCreated ().getWorldName (), this.history.getLeft (), 
            this.history.getTop ()));
    this.entities = EntityGenerator.generate (this.history.getCreated (), 
            new Area (this.history.getLeft (), this.history.getTop ()), area,
            this.history.getRandom (), this.closestPlayer);
    this.finished = true;
  }
}
