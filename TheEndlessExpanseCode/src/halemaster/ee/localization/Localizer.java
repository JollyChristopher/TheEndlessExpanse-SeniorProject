package halemaster.ee.localization;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name Localizer
 * 
 * @version 0.0.0
 * 
 * @date Oct 12, 2013
 */
public class Localizer
{
  public static final String BUNDLE_FOLDER = "assets/Interface/Bundles";
  public static final String BUNDLE_NAME = "/Bundle";
  public static final Logger LOGGER = 
          Logger.getLogger(Localizer.class.getName ());
  private static List<String> bundles = null;
  private static Locale globalLocale = Locale.getDefault ();
  
  
  /**
   * Get a localized string of the given key from the singleton.
   * 
   * @param key key of string to localize.
   */
  public static String getString (String key, Object...arguments)
  {
    String value = null;
    File allBundles = new File (BUNDLE_FOLDER);
    
    if (null == bundles)
    {
      bundles = new ArrayList<String>();
      for (File bundleFolder : allBundles.listFiles ())
      {
        if (bundleFolder.isDirectory ())
        {
          bundles.add (bundleFolder.getPath () + BUNDLE_NAME);
        }
      }
    }
    
    for (int i = 0; null == value && i < bundles.size (); i++)
    {
      value = getString (bundles.get (i), key, arguments);
      if (value.equals (key))
      {
        value = null;
      }
    }
    
    if (null == value)
    {
      value = key;
    }
    
    return value;
  }
  
  /**
   * Change the language of the localizer singleton.
   * 
   * @param locale the language to change to.
   */
  public static void changeLanguage (Locale locale)
  {
    globalLocale = locale;
  }
  
  /**
   * Get a localized string based upon the given key.
   * 
   * @param bundle the bundle to find the string in.
   * @param key key of localized string.
   * @return the localized version of the string.
   */
  public static String getString (String bundle, String key, Object...arguments)
  {    
    try
    {
      key = MessageFormat.format (ResourceBundle.getBundle (bundle, 
              globalLocale, new BundleLoader ()).getString (key),
              arguments);
      LOGGER.log (Level.FINEST, "{0} localized", key);
    }
    catch (MissingResourceException e)
    {
      LOGGER.log (Level.WARNING, "Could not find a resource: ", e);
    }
      
    return key;
  }
}
