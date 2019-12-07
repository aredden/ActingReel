package actingreel.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static actingreel.core.constants.Util.*;


public class VideoUtils {

	static Logger LOGGER = LoggerFactory.getLogger(VideoUtils.class);
	
	// Requests data from Youtube in JSON format.
	public static String getYoutubeJSON(String videoid){
	      String json =  getJSONFromHttpRequest(YT_EMBED_PREFIX + videoid + HTTP_FORMAT_JSON);
	      return json;

	}
	// Requests data from Vimeo in JSON format.
	public static String getVimeoJSON(String videoid) {
	      String json = getJSONFromHttpRequest(VIMEO_EMBED_PREFIX + videoid + VIMEO_EMBED_SUFFIX);
	      return json;
	}
	
	
	private static String getJSONFromHttpRequest(String requestAPIString) {
		LOGGER.info(requestAPIString);
		try {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet getRequest = new HttpGet(requestAPIString);
			getRequest.addHeader(ACCEPT,ACCEPT_TYPE_JSON);
			HttpResponse response = httpClient.execute(getRequest);
			
			if(response.getStatusLine().getStatusCode() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			  }
			  BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			  String myJSON = "";
			  String output;
			  while ((output = br.readLine()) != null) {
			    myJSON = myJSON + output;
			  }
		      httpClient.close();
		      return myJSON;
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		    return null;
	}
	
	public static String extractVimeoId(String vimeoUrl) {
	    Pattern compiledPattern = Pattern.compile(REGEX_EXTRACT_VIMEOID);
	    Matcher matcher = compiledPattern.matcher(vimeoUrl);
	    if(matcher.find()){
	        return matcher.group(3);
	    } else {
	        return ERROR;  
	    }
	}
	
	// Extracts Youtube ID from full URL string.
	public static String extractYoutubeId(String ytUrl) {
	    Pattern compiledPattern = Pattern.compile(REGEX_EXTRACT_YTID);
	    Matcher matcher = compiledPattern.matcher(ytUrl);
	    if(matcher.find()){
	        return matcher.group();
	    } else {
	        return ERROR;
	    }
	}
	
}
