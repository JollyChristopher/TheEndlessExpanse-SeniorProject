package halemaster.ee.world.entity;

import halemaster.ee.Game;
import halemaster.ee.world.micro.AreaGenerator;

/**
 * @name EntityType
 * 
 * @version 0.0.0
 * 
 * @date Feb 13, 2014
 */
public enum EntityType 
{
  PLAYER ("player", "/players"),
  NPC ("npc"),
  MONSTER ("monster");
  
  public static final String DEFAULT_LOCATION = EntityHolder.ENTITY_FOLDER;
  private String id;
  private String location;
  
  private EntityType (String id)
  {
    this (id, DEFAULT_LOCATION);
  }
  
  private EntityType (String id, String location)
  {
    this.id = id;
    this.location = location;
  }
  
  public String getId ()
  {
    return this.id;
  }
  
  public String getLocation ()
  {
    return this.location;
  }
  
  /**
   * Get a type by the id
   * @param id id fo the type
   * @return type of the entity.
   */
  public static EntityType getById (String id)
  {
    EntityType type = null;
    
    for (EntityType possible : EntityType.values ())
    {
      if (id.equals (possible.getId ()))
      {
        type = possible;
      }
    }
    
    return type;
  }
}
