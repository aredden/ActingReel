package actingreel.core.models;


import com.adobe.cq.sightly.WCMUsePojo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import actingreel.core.models.objects.VideoData;

import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static actingreel.core.utils.VideoUtils.*;

public class VideoModel
  extends WCMUsePojo
{
  private VideoData youtubeObj = null;
  protected static final Logger log = LoggerFactory.getLogger(VideoModel.class.getClass());
   
  public void activate()
    throws Exception
  {
    Node currentNode = (Node)getResource().adaptTo(Node.class);
    this.youtubeObj = new VideoData();
     
    String videoid = "-d8gcfbjoZM";
    if (currentNode.hasProperty("jcr:videoid")) {
      videoid = currentNode.getProperty("./jcr:videoid").getString();
    }
    log.info("**** Video ID " + videoid);
    
    JsonParser parser = new JsonParser();
    JsonElement jsonTree = parser.parse(getYoutubeJSON(videoid));
    JsonObject youtubeJson = jsonTree.getAsJsonObject();
    this.youtubeObj.setVideotitle(youtubeJson.get("title").getAsString());
    this.youtubeObj.setVideo_author_name(youtubeJson.get("author_name").getAsString());
    String htmlString = youtubeJson.get("html").toString().replace("\\\"", "");
    this.youtubeObj.setVideohtml(htmlString.substring(1,htmlString.length()-1));
  }
   
  public VideoData getYoutubeObj()
  {
    return this.youtubeObj;
  }
}