package halemaster.ee.localization;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @name BundleLoader
 * 
 * @version 0.0.0
 * 
 * @date Jan 31, 2014
 */
public class BundleLoader extends ClassLoader
{
  /**
   * Find a resource bundle with the given name.
   * 
   * @param name name of the resource
   * @return url to the resource
   */
  @Override
  protected URL findResource(String name)
  {
    File f = new File(name);

    try
    {
      return f.toURI ().toURL ();
    }
    catch (MalformedURLException e)
    {
      // log
    }
    return super.findResource(name);
  }
}
