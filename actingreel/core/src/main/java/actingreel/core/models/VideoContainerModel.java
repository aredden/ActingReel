package actingreel.core.models;

import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

import static actingreel.core.constants.Constants.*;
import static actingreel.core.utils.VideoUtils.extractYTId;
import static actingreel.core.utils.VideoUtils.getYoutubeJSON;
import actingreel.core.models.objects.YouTubeData;

public class VideoContainerModel extends WCMUsePojo {

	// Class helper variables.
	private List<String> videoUrls;
	private ArrayList<HashMap<String,String>> dataWithDescription;
	
	// Main video data returned to videocontainer.html HTL.
	private List<YouTubeData> youtubeData;
	
	static Logger LOGGER = LoggerFactory.getLogger(VideoContainerModel.class);
	
	@Override
	public void activate() throws Exception {
		
			
			// Instantiate class level variables.
		 	videoUrls = new ArrayList<String>();
		 	youtubeData = new ArrayList<YouTubeData>();
		 	dataWithDescription = new ArrayList<HashMap<String,String>>();		 	
		 	
		 	// WCMUsePojo Resources.
		 	Resource videosNode;
		 	Resource videoContainer = getResource();
		 	Iterable<Resource> videos;
		 	
		 	// Get Youtube videos only if they exist.
		 	videosNode = videoContainer.hasChildren() ? videoContainer.getChild(VIDEOS) : null;
			if((videosNode!=null) && videosNode.hasChildren()) {
				videos = videosNode.getChildren();
				getYoutubeVideos(videos);
			}
	}
	
	/*
	 *  Returns Youtube URLs. (Mainly for testing.)
	 */
	public List<String> getUrls(){
		return videoUrls;
	}
	
	/*
	 *  Returns populated list of YouTubeData objects for HTL.
	 */
	public List<YouTubeData> getItems(){
		return youtubeData;
	}
	
	
	private void getYoutubeVideos(Iterable<Resource> videos){
		for(Resource video : videos) {
			ValueMap videoMap = video.getValueMap();
			
			HashMap<String, String> map = new HashMap<String,String>();
			
			String videoUrl = videoMap.get(VIDEO_URL,"");
			String videoDescription = videoMap.get(VIDEO_DESC,"");

			// Only populate dataWithDescription if there is a video URL
			if(!(videoUrl == null) && !(videoUrl == "")) {

				videoUrls.add(videoUrl);
				
				// Add URL and author description to HashMap list (dataWithDescription).
				map.put(VIDEO,videoUrl);
				map.put(DESCRIPTION,videoDescription);
				dataWithDescription.add(map);
			}
		}
		
		// Iterate through video URLs and extract video ID from full URL.
		for(HashMap<String, String> x : dataWithDescription) {
			String url = x.get(VIDEO);
			if(url.contains("?v=")) {
				String id = extractYTId(url);
				
				LOGGER.info("**** YoutubeId: "+id);
				x.replace(VIDEO, id);
			};
		}
		
		// Populates youtubeData with YouTubeData objects.
		for(HashMap<String, String> x : dataWithDescription) {
			String videoid = x.get(VIDEO);
			
			// Parse JSON from HTTP request utility function getYoutubeJSON()
			JsonParser parser = new JsonParser();
			JsonElement json = parser.parse(getYoutubeJSON(videoid));
			JsonObject youtubeJson = json.getAsJsonObject();
			
			// Populate a YouTubeData object with relevant data from JSON response.
			YouTubeData youtubeObj = new YouTubeData();
		    
				// Remove extra '\"'s from HTML JSON string.
				String htmlString = youtubeJson.get("html").toString().replace("\\\"", "");
			    
				// Set YouTubeData object fields.
				youtubeObj.setVideoid(videoid);
			    youtubeObj.setVideotitle(youtubeJson.get("title").getAsString());
			    youtubeObj.setVideo_author_name(youtubeJson.get("author_name").getAsString());
			    youtubeObj.setVideohtml(htmlString.substring(1,htmlString.length()-1));
			    youtubeObj.setVideo_description(x.get("description"));
			    
			    // Add YouTubeData object to class variable that is instance of List<YouTubeData>.
			    youtubeData.add(youtubeObj);
		}
	}
}