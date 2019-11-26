package actingreel.core.models;

import static org.junit.Assert.*;

import javax.script.Bindings;

import org.junit.Test;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class TestCarouselModel {
	
	@Rule
	public AemContext ctx = new AemContext();
	
	@Mock
	protected CarouselModel carousel;
	
	@Mock
	protected Resource resource = mock(Resource.class);
	
	@Mock
	protected Bindings bindings = mock(Bindings.class);
	
	private String currentPagePath;
	
	public void setUpTest() throws Exception {
		ctx.load().json("/actingreel/core/models/CarouselModel.json","/content/actingreel");
		carousel = new CarouselModel();
		
		when(bindings.get("resource"))
		.thenReturn(ctx.resourceResolver()
		.getResource(currentPagePath+"/jcr:content/root/carousel"));
	}
	
	@Test
	public void testNoImageReferenceProperty() {
		currentPagePath = "/content/actingreel/en";
		try {
			setUpTest();
		} catch (Exception e) {
			fail(e.toString());
		}
		carousel.init(bindings);
		List<String> actual = carousel.getItems();
		assertNotNull(actual);
		assertEquals(0,actual.size());
	}

	@Test
	public void testNoImages() {
		currentPagePath = "/content/actingreel/en/home/noreferences";
		try {
			setUpTest();
		} catch (Exception e) {
			fail(e.toString());
		}
		carousel.init(bindings);
		List<String> actual = carousel.getItems();
		assertNotNull(actual);
		assertEquals(0,actual.size());
	}
	
	@Test
	public void testCorrectImages() {
		currentPagePath = "/content/actingreel/en/home";
		try {
			setUpTest();
		} catch (Exception e) {
			fail(e.toString());
		}
		carousel.init(bindings);
		List<String> expected = new ArrayList<String>();
		expected.add("/content/dam/we-retail-client-app/mobile-app/screenshots/ios/screenshot-1477427860861.jpg");
		expected.add("/content/dam/we-retail-client-app/mobile-app/screenshots/ios/screenshot-1477427860523.jpg");
		List<String> actual = carousel.getItems();
		assertNotNull(actual);
		assert(actual.contains(expected.get(0)));
		assert(actual.contains(expected.get(1)));
		assertEquals(2,actual.size());
	}
	
	@Test
	public void testOneImage() {
		currentPagePath = "/content/actingreel/en/home/about";
		try {
			setUpTest();
		} catch (Exception e) {
			fail(e.toString());
		}
		carousel.init(bindings);
		List<String> expected = new ArrayList<String>();
		expected.add("/content/dam/we-retail-client-app/mobile-app/screenshots/ios/screenshot-1477427860861.jpg");
		List<String> actual = carousel.getItems();
		assertNotNull(actual);
		assertEquals(1,actual.size());
		assert(actual.contains(expected.get(0)));
		
	}

}
