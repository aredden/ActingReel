package actingreel.core.models;


import com.adobe.cq.sightly.WCMUsePojo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import actingreel.core.models.objects.YouTubeData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jcr.Node;
import javax.jcr.Property;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class VideoModel
  extends WCMUsePojo
{
  private YouTubeData youtubeObj = null;
  protected static final Logger log = LoggerFactory.getLogger(VideoModel.class.getClass());
   
  public void activate()
    throws Exception
  {
    Node currentNode = (Node)getResource().adaptTo(Node.class);
    this.youtubeObj = new YouTubeData();
     
    String videoid = "-d8gcfbjoZM";
    if (currentNode.hasProperty("jcr:videoid")) {
      videoid = currentNode.getProperty("./jcr:videoid").getString();
    }
    log.info("**** Video ID " + videoid);
    
    
    JsonParser parser = new JsonParser();
    JsonElement jsonTree = parser.parse(getJSON(videoid));
    JsonObject youtubeJson = jsonTree.getAsJsonObject();
    this.youtubeObj.setVideotitle(youtubeJson.get("title").getAsString());
    this.youtubeObj.setVideo_author_name(youtubeJson.get("author_name").getAsString());
    String htmlString = youtubeJson.get("html").toString().replace("\\\"", "");
    this.youtubeObj.setVideohtml(htmlString.substring(1,htmlString.length()-1));
  }
   
  public YouTubeData getYoutubeObj()
  {
    return this.youtubeObj;
  }
   
  public static String getJSON(String videoid)
  {
    try
    {
      DefaultHttpClient httpClient = new DefaultHttpClient();
       
      HttpGet getRequest = new HttpGet("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=" + videoid + "&format=json");
      getRequest.addHeader("accept", "application/json");
       
      HttpResponse response = httpClient.execute(getRequest);
      if (response.getStatusLine().getStatusCode() != 200)
      {
        log.info("exception");
         
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
       
      String myJSON = "";
      String output;
      while ((output = br.readLine()) != null) {
        myJSON = myJSON + output;
      }
      log.info("**** Myjson: " + myJSON);
      httpClient.getConnectionManager().shutdown();
       
      return myJSON;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
}