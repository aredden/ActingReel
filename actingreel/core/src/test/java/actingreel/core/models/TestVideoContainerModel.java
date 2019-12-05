package actingreel.core.models;


import static org.junit.Assert.*;

import javax.script.Bindings;

import org.junit.Test;
import org.apache.sling.api.resource.Resource;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.*;

import actingreel.core.models.objects.YouTubeData;

import static org.mockito.Mockito.*;

import java.util.List;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class TestVideoContainerModel {
	
	@Rule
	public AemContext ctx = new AemContext();
	
	@Mock
	protected VideoContainerModel videoContainer;
	
	@Mock
	protected Resource resource = mock(Resource.class);
	
	@Mock
	protected Bindings bindings = mock(Bindings.class);
	
	private String currentPagePath;
	
	static Logger logger = LoggerFactory.getLogger(TestVideoContainerModel.class);
	
	public void setUp() throws Exception{
		ctx.load().json("/actingreel/core/models/VideoContainer.json","/content/en");
		videoContainer = new VideoContainerModel();
		
		
		when(bindings.get("resource"))
		.thenReturn(ctx.resourceResolver()
		.getResource(currentPagePath+"/jcr:content/root/responsivegrid/videocontainer"));
	}

	@Test
	public void testUrls() {
		currentPagePath="/content/en/home/actingreel";
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		videoContainer.init(bindings);
		List<String> videos = videoContainer.getUrls();
		String expected = "https://www.youtube.com/watch?v=OO9irWogFW0";
		assertEquals(1,videos.size());
		assertEquals(expected,videos.get(0));
		
	}
	
	@Test
	public void testItems() {
		currentPagePath="/content/en/home/actingreel";
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		videoContainer.init(bindings);
		List<YouTubeData> youtubeData = videoContainer.getItems();
		assertEquals(1,youtubeData.size());
		YouTubeData actual = youtubeData.get(0);
		assertEquals(actual.getVideoid(), "OO9irWogFW0");
		assert(actual.getVideohtml().contains("www.youtube.com/embed/OO9irWogFW0"));
		assertEquals(actual.getVideotitle(),"Why would Twitch streamers need a $20,000 server?? N3RDFUSION Visit");
		assertEquals(actual.getVideo_author_name(),"Linus Tech Tips");
	}
	
	@Test
	public void testTwoItems() {
		currentPagePath="/content/en/home/work";
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		videoContainer.init(bindings);
		List<YouTubeData> youtubeData = videoContainer.getItems();
		//test size is 2
		assertEquals(2,youtubeData.size());
		
		
		//test first youtube data object
		YouTubeData actual = youtubeData.get(0);
		assertEquals(actual.getVideoid(), "OO9irWogFW0");
		assert(actual.getVideohtml().contains("www.youtube.com/embed/OO9irWogFW0"));
		assertEquals(actual.getVideotitle(),"Why would Twitch streamers need a $20,000 server?? N3RDFUSION Visit");
		assertEquals(actual.getVideo_author_name(),"Linus Tech Tips");
		assertEquals(2,youtubeData.size());
		
		//test second youtube data object
		actual = youtubeData.get(1);
		assertEquals(actual.getVideoid(), "BQe-TgQdFzw");
		assert(actual.getVideohtml().contains("www.youtube.com/embed/BQe-TgQdFzw"));
		assertEquals(actual.getVideotitle(),"\"5G\" is \"Here\"");
		assertEquals(actual.getVideo_author_name(),"TechLinked");
	}

}
