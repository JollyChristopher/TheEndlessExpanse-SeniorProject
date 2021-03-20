package halemaster.ee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith (JUnit4.class)

/**
 * @name TestJson
 * 
 * @version 0.0.0
 * 
 * @date Nov 6, 2013
 */
public class TestJson 
{
  public static final String TEST_STRING = "{\"name\":\"Some Json\","
          + "\"id\":12345,\"color\":[0,0,0],\"stuff\":[{\"name\":\"hi\"},"
            + "{\"name\":\"not hi\"}]}";
  
  @Test
  public void isValid ()
  {
    try
    {
      Json.isJsonValid (null, null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
    try
    {
      Json.isJsonValid ("{}", null);
      Assert.fail ("Should throw NullPointerException");
    }
    catch (NullPointerException e)
    {
      
    }
    Assert.assertFalse (Json.isJsonValid ("--", MockJsonObject.class));
    Assert.assertTrue (Json.isJsonValid (TEST_STRING, MockJsonObject.class));
  }
  
  @Test
  public void jsonString ()
  {
    MockJsonObject obj = new MockJsonObject ();
    
    Assert.assertEquals (Json.toJson (null), "null");
    
    Assert.assertEquals (Json.toJson (obj), "{\"id\":0}");
    obj.id = 5;
    Assert.assertEquals (Json.toJson (obj), "{\"id\":5}");
    obj.name = "Hello";
    Assert.assertEquals (Json.toJson (obj), "{\"name\":\"Hello\",\"id\":5}");
    obj.color = new int[]{0, 1, 2};
    Assert.assertEquals (Json.toJson (obj), "{\"name\":\"Hello\",\"id\":5,"
            + "\"color\":[0,1,2]}");
    obj.stuff = new Map[]{new HashMap<String, String>()};
    obj.stuff[0].put ("hello", "nothing");
    Assert.assertEquals (Json.toJson (obj), "{\"name\":\"Hello\",\"id\":5,"
            + "\"color\":[0,1,2],\"stuff\":[{\"hello\":\"nothing\"}]}");
  }
  
  @Test
  public void fromFile ()
  {
    MockJsonObject[] objs;
    File file = new File ("test/halemaster/ee/mockJsonFile");
    
    try
    {
      try
      {
        Json.getFromFile (null, null);
        Assert.fail ("Should throw NullPointerException");
      }
      catch (NullPointerException e)
      {
        
      }
      
      try
      {
        objs = Json.getFromFile (null, MockJsonObject.class);
        Assert.fail ("Should throw NullPointerException");
      }
      catch (NullPointerException e)
      {
        
      }
      
      objs = Json.getFromFile (file, MockJsonObject.class);
      
      Assert.assertEquals (TEST_STRING, Json.toJson (objs[0]));
      Assert.assertEquals (TEST_STRING, Json.toJson (objs[1]));
    }
    catch (IOException e)
    {
      Assert.fail ("Should not throw IOException");
    }
  }
  
  @Test
  public void getNextLine ()
  {
    MockJsonObject obj;
    File file = new File ("test/halemaster/ee/mockJsonFile");
    try
    {
      BufferedReader reader = new BufferedReader (new FileReader (file));
      
      try
      {
        try
        {
          Json.getNextLine (null, null);
          Assert.fail ("Should throw NullPointerException");
        }
        catch (NullPointerException e)
        {
          
        }
        
        try
        {
          obj = Json.getNextLine (null, MockJsonObject.class);
          Assert.fail ("Should throw NullPointerException");
        }
        catch (NullPointerException e)
        {
          
        }
        
        obj = Json.getNextLine (reader, MockJsonObject.class);
        Assert.assertEquals (TEST_STRING, Json.toJson (obj));
        obj = null;
        Assert.assertNull (obj);
        obj = Json.getNextLine (reader, MockJsonObject.class);
        Assert.assertEquals (TEST_STRING, Json.toJson (obj));
      }
      catch (IOException e)
      {
        Assert.fail ("Should not throw IOException");
      }
    }
    catch (FileNotFoundException e)
    {
      Assert.fail ("Should not throw FileNotFoundException");
    }
  }
}
