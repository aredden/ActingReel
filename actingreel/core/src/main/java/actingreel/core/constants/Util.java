package actingreel.core.constants;

public class Util {
	
	public static final String ACCEPT = "accept";
	public static final String ERROR = "error";
	public static final String HTTP_FORMAT_JSON = "&format=json";
	public static final String ACCEPT_TYPE_JSON = "application/json";
	public static final String VIMEO_EMBED_PREFIX = "https://vimeo.com/api/oembed.json?url=https://vimeo.com/";
	public static final String VIMEO_EMBED_SUFFIX = "&dnt=true";
	public static final String YT_EMBED_PREFIX = "https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=";
	public static final String REGEX_EXTRACT_YTID = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
	public static final String REGEX_EXTRACT_VIMEOID = "https?://(?:www\\.|player\\.)?vimeo.com/(?:channels/(?:\\w+/)?|groups/([^/]*)/videos/|album/(\\d+)/video/|video/|)(\\d+)(?:$|/|\\?)";
	
	
}