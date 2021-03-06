package halemaster.ee;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.HashMap;
import java.util.Map;

/**
 * @name Sprite
 * 
 * @version 0.0.0
 * 
 * @date Jan 28, 2014
 */
public class Sprite 
{
  public static final float DEFAULT_SIZE = 20f;
  public static final int DEFAULT_BOX = 32;
  private static Map<String, Material> materials = new HashMap<String, Material>();
  private static Quad spriteBox = new Quad(DEFAULT_SIZE,DEFAULT_SIZE);
  private float fps;
  private float passed = fps;
  private int next = 0;
  private String currentAnim = "";
  private Map<String, Texture[]> textures = new HashMap<String, Texture[]>();
  private Geometry image;
  private AssetManager manager;
  
  public Sprite (String name, AssetManager manager, float fps, int layer)
  {
    this (name, manager, fps, layer, DEFAULT_BOX, DEFAULT_BOX);
  }
  
  public Sprite (String name, AssetManager manager, float fps, int layer,
          int width, int height)
  {
    Material spriteMat;
    Quad usedBox = spriteBox;
    
    this.manager = manager;
    this.fps = fps;
    
    if (width != DEFAULT_BOX || height != DEFAULT_BOX)
    {
      usedBox = new Quad(DEFAULT_SIZE * (width / DEFAULT_BOX),
              DEFAULT_SIZE * (height / DEFAULT_BOX));
    }
    
    spriteMat = materials.get (name);
    if (null == spriteMat)
    {
      spriteMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
      spriteMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
      materials.put (name, spriteMat);
    }
    this.image = new Geometry(name, usedBox);
    this.image.setQueueBucket(RenderQueue.Bucket.Transparent);
    this.image.setMaterial(spriteMat);
    this.image.rotate ((float) (3 * Math.PI / 2), 0, 0f);
    this.image.move (0, layer / 10.0f, 0);
    this.image.scale ((float) (1));
    this.passed = 1.0f / this.fps;
  }
  
  public String getCurrentAnimation ()
  {
    return this.currentAnim;
  }
  
  public void move (float x, float y)
  {
    this.image.move (x, 0, y);
  }
  
  public boolean update (float tpf)
  {
    boolean finishedTexture = false;
    Texture[] tex = this.textures.get (this.currentAnim);
    if (null != tex)
    {
      if (tex.length > 1)
      {
        this.passed += tpf;
        if (this.passed > 1.0f / this.fps)
        {
          this.image.getMaterial ().setTexture ("ColorMap", tex[this.next]);
          this.next++;
          if (this.next >= tex.length)
          {
            this.next = 0;
            finishedTexture = true;
          }

          this.passed = 0;
        }
      }
      else
      {
        if (null == this.image.getMaterial ().getTextureParam ("ColorMap"))
        {
          this.image.getMaterial ().setTexture ("ColorMap", tex[0]);
        }
        finishedTexture = true;
      }
    }
    else
    {
      finishedTexture = true;
    }
    return finishedTexture;
  }
  
  public void addAnimation (String name, String[] images)
  {
    Texture[] texture = new Texture2D[images.length];
    
    for (int i = 0; i < texture.length; i++)
    {
      texture[i] = this.manager.loadTexture (images[i]);
    }
    
    this.textures.put (name, texture);
  }
  
  public void setAnimation (String anim)
  {
    this.currentAnim = anim;
    if (this.currentAnim != null && this.textures != null && this.textures.get 
              (this.currentAnim) != null)
    {
      this.image.getMaterial ().setTexture ("ColorMap", this.textures.get 
              (this.currentAnim)[this.next]);
    }
  }
  
  public void attachTo (Node attach)
  {
    attach.attachChild (this.image);
  }
  
  public void detachFrom (Node detach)
  {
    detach.detachChild (this.image);
  }
  
  public Geometry getImage ()
  {
    return this.image;
  }
}
