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
import static actingreel.core.utils.VideoUtils.extractYoutubeId;
import static actingreel.core.utils.VideoUtils.extractVimeoId;
import static actingreel.core.utils.VideoUtils.getYoutubeJSON;
import static actingreel.core.utils.VideoUtils.getVimeoJSON;
import actingreel.core.models.objects.VideoData;

public class VideoContainerModel extends WCMUsePojo {
	
	private static final String SELECTION = "selection";
	// Class helper variables.
	private List<String> videoUrls;
	private ArrayList<HashMap<String,String>> dataWithDescription;
	
	// Main video data returned to videocontainer.html HTL.
	private List<VideoData> videoDataList;
	
	static Logger LOGGER = LoggerFactory.getLogger(VideoContainerModel.class);
	
	@Override
	public void activate() throws Exception {
		
			
			// Instantiate class level variables.
		 	videoUrls = new ArrayList<String>();
		 	videoDataList = new ArrayList<VideoData>();
		 	dataWithDescription = new ArrayList<HashMap<String,String>>();		 	
		 	
		 	// WCMUsePojo Resources.
		 	Resource videosNode;
		 	Resource videoContainer = getResource();
		 	Iterable<Resource> videos;
		 	
		 	// Get videos only if they exist.
		 	videosNode = videoContainer.hasChildren() ? videoContainer.getChild(VIDEOS) : null;
			if((videosNode!=null) && videosNode.hasChildren()) {
				videos = videosNode.getChildren();
				getVideos(videos);
			}
	}
	
	/*
	 *  Returns relevant provider URLs. (Mainly for testing.)
	 */
	public List<String> getUrls(){
		return videoUrls;
	}
	
	/*
	 *  Returns populated list of VideoData objects for HTL.
	 */
	public List<VideoData> getItems(){
		return videoDataList;
	}
	
	
	private void getVideos(Iterable<Resource> videos){
		for(Resource video : videos) {
			ValueMap videoMap = video.getValueMap();
			
			HashMap<String, String> map = new HashMap<String,String>();
			
			String videoUrl = videoMap.get(VIDEO_URL,EMPTY_STRING);
			String videoDescription = videoMap.get(VIDEO_DESC,EMPTY_STRING);
			
			String videoSelectType = videoMap.get("videoSelect","");
			

			// Only populate dataWithDescription if there is a video URL
			if(!(videoUrl.equals(EMPTY_STRING))) {
				
				videoUrls.add(videoUrl);
				
				// Add URL and author description to HashMap list (dataWithDescription).
				map.put(SELECTION,videoSelectType);
				map.put(VIDEO,videoUrl);
				map.put(DESCRIPTION,videoDescription);
				dataWithDescription.add(map);
			}
		}
		
		// Iterate through video URLs and extract video ID from full URL.
		for(HashMap<String, String> x : dataWithDescription) {
			String url = x.get(VIDEO);
			if(url.contains(VIDEO_IDENTIFIER)||url.contains("vimeo")) {
				String id = "";
				switch(x.get(SELECTION)) {
				case "youtube": {
						id = extractYoutubeId(url);
						break;
					}
				case "vimeo": {
						id = extractVimeoId(url);
						break;
					}
				default : {
						id = extractYoutubeId(url);
						break;
					}
				}
				
				LOGGER.info("**** VideoId: "+id);
				x.replace(VIDEO, id);
			};
		}
		
		// Populates videoData with VideoData objects.
		for(HashMap<String, String> x : dataWithDescription) {
			String videoid = x.get(VIDEO);
			
			
			// Parse JSON from HTTP request utility function get{ Video-Provider }JSON()
			JsonParser parser = new JsonParser();
			String json = "";
			switch(x.get(SELECTION)) {
			case "youtube": 
				json = getYoutubeJSON(videoid); 
				break;
			case "vimeo": 
				json = getVimeoJSON(videoid); 
				break;
			default: json = getYoutubeJSON(videoid);
			}
			JsonElement jsonTree = parser.parse(json);
			JsonObject videoJson = jsonTree.getAsJsonObject();
			
			// Populate a VideoData object with relevant data from JSON response.
			VideoData videoObj = new VideoData();
		    
				// Remove extra '\"'s from HTML JSON string.
				String htmlString = videoJson.get(HTML).toString().replace(HTML_VIDEO_TARGET, EMPTY_STRING);
			    
				// Set VideoData object fields.
				videoObj.setVideoid(videoid);
			    videoObj.setVideotitle(videoJson.get(TITLE).getAsString());
			    videoObj.setVideo_author_name(videoJson.get(AUTHOR_NAME).getAsString());
			    videoObj.setVideohtml(htmlString.substring(1,htmlString.length()-1));
			    videoObj.setVideo_description(x.get(DESCRIPTION));
			    
			    // Add VideoData object to class variable that is instance of List<VideoData>.
			    videoDataList.add(videoObj);
		}
	}
}