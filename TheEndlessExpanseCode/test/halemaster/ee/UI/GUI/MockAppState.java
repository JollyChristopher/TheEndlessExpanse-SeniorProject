package halemaster.ee.UI.GUI;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;

/**
 * @name MockAppState
 * 
 * @version 0.0.0
 * 
 * @date Oct 15, 2013
 */
public class MockAppState implements AppState
{
  boolean init = false;
  boolean enabled = false;
  
  public void initialize (AppStateManager stateManager, Application app)
  {
    init = true;
  }

  public boolean isInitialized ()
  {
    return init;
  }

  public void setEnabled (boolean active)
  {
    enabled = active;
  }

  public boolean isEnabled ()
  {
    return enabled;
  }

  public void stateAttached (AppStateManager stateManager)
  {
    
  }

  public void stateDetached (AppStateManager stateManager)
  {
    
  }

  public void update (float tpf)
  {
    
  }

  public void render (RenderManager rm)
  {
    
  }

  public void postRender ()
  {
    
  }

  public void cleanup ()
  {
    
  }
}
