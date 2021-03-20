package halemaster.ee;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @name Json
 * 
 * @version 0.0.0
 * 
 * @date Nov 1, 2013
 */
public class Json 
{
  private static Gson serializer = new Gson ();
  
  /**
   * Get the next line from a file as an object.
   * 
   * @param reader the buffered reader to get the next object from.
   * @param clazz the class of object to get
   * @return the next object in the file.
   * @throws IOException 
   */
  public static <type> type getNextLine (BufferedReader reader, 
          Class<type> clazz) throws IOException
  {
    type value = null;
    String next = reader.readLine ();
    
    while (!isJsonValid (next, clazz) && reader.ready ())
    {
      next += reader.readLine ();
    }
    
    if (null != next)
    {
      value = serializer.fromJson (next, clazz);
    }
    
    return value;
  }
  
  /**
   * Get all objects from the given file.
   * 
   * @param file the file to get all objects from.
   * @param clazz the class of the objects to get.
   * @return an array of objects from the file.
   * @throws IOException 
   */
  public static <type> type[] getFromFile (File file, Class<type> clazz)
          throws IOException
  {
    type[] values;
    BufferedReader reader = new BufferedReader (new FileReader (file));
    type temp;
    ArrayList<type> list = new ArrayList<type> ();
    
    temp = getNextLine (reader, clazz);
    
    while (null != temp)
    {
      list.add (temp);
      temp = getNextLine (reader, clazz);
    }
    
    reader.close ();
    
    values = (type[]) Array.newInstance (clazz, list.size ());
    for (int i = 0; i < list.size (); i++)
    {
      values[i] = list.get (i);
    }
    
    return values;
  }
  
  public static <type> void saveJson (File file, type[] objects) 
          throws IOException
  {
    BufferedWriter writer = new BufferedWriter (new FileWriter (file));
    for (type object : objects)
    {
      writer.write (toJson (object));
      writer.newLine ();
    }
    
    writer.close ();
  }
  
  public static <type> void saveJson (File file, type object)
          throws IOException
  {
    Object[] objects = {object};
    saveJson (file, objects);
  }
  
  /**
   * Convert an object into a json string.
   * 
   * @param object object to convert.
   * @return the json string.
   */
  public static <type> String toJson (type object)
  {
    String obj = serializer.toJson (object);
    
    if ("null".equals (obj))
    {
      obj = "{}";
    }
    
    return obj;
  }
  
  public static <type> type getJson (String value, Class<type> clazz)
  {
    return serializer.fromJson (value, clazz);
  }
  
  /**
   * Test if the String is valid json. Taken from:
   * http://stackoverflow.com/a/10174938
   * 
   * @param test String to test
   * @return whether it is valid Json.
   */
  public static boolean isJsonValid(String test, Class clazz)
  {
      boolean valid;
      try 
      {
          serializer.fromJson (test, clazz);
          valid = true;
      }
      catch(JsonSyntaxException ex) 
      { 
          valid = false;
      }
      return valid;
  }
}
