package actingreel.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static actingreel.core.constants.Constants.*;

public class VideoUtils {


	
	// Requests data from Youtube in JSON format.
	public static String getYoutubeJSON(String videoid){
	    try {
	      DefaultHttpClient httpClient = new DefaultHttpClient();
	      HttpGet getRequest = new HttpGet(YT_EMBED_PREFIX + videoid + "&format=json");
		  getRequest.addHeader("accept", "application/json");
		  HttpResponse response = httpClient.execute(getRequest);
		  
		  if (response.getStatusLine().getStatusCode() != 200){
		    throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		  }
		  BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		  String myJSON = "";
		  String output;
		  while ((output = br.readLine()) != null) {
		    myJSON = myJSON + output;
		  }
	      httpClient.getConnectionManager().shutdown();
	      return myJSON;
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	// Extracts Youtube ID from full URL string.
	public static String extractYTId(String ytUrl) {
	    Pattern compiledPattern = Pattern.compile(REGEX_EXTRACT_YTID);
	    Matcher matcher = compiledPattern.matcher(ytUrl);
	    if(matcher.find()){
	        return matcher.group();
	    } else {
	        return "error";  
	    }
	}
	
}
