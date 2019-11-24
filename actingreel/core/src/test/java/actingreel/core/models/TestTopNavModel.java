package actingreel.core.models;

import actingreel.core.models.TopNavModel;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import com.day.cq.wcm.api.Page;
import java.util.List;
import javax.script.Bindings;
import io.wcm.testing.mock.aem.junit.AemContext;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.sling.api.SlingHttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestTopNavModel {

	@Mock
	protected TopNavModel topnav;
	
	@Mock
	protected Bindings bindings = mock(Bindings.class);
	
	@Mock
	SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
	
	@Rule
	public AemContext ctx = new AemContext();

	String currentPagePath;
	
	public void setUpTest() throws Exception {
		ctx.load().json("/actingreel/core/models/TopNav.json","/content/actingreel");
		topnav = new TopNavModel();
		
		when(bindings.get("request"))
			.thenReturn(request);
		when(bindings.get("properties"))
			.thenReturn(ctx.resourceResolver()
			.getResource(currentPagePath+"/jcr:content/root/topnav")
			.getValueMap());
		when(bindings.get("pageManager"))
			.thenReturn(ctx.pageManager());
		when(bindings.get("currentPage"))
			.thenReturn(ctx.pageManager()
			.getPage(currentPagePath));
	}
	
	@Test
	public void testGetRootNotNull() {
		
		currentPagePath = "/content/actingreel/en";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		topnav.init(bindings);
		Page p = topnav.getRoot();
		assertNotNull(p);
	}

	@Test
	public void testGetRootIsCorrect() {
		
		currentPagePath = "/content/actingreel/en";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String expected = "/content/actingreel/en";
		topnav.init(bindings);
		String actual = topnav.getRoot().getPath();
		assertEquals(expected,actual);
	}
	
	@Test
	public void testGetItemsNotNull() {
		
		currentPagePath = "/content/actingreel/en/home";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		topnav.init(bindings);
		List<Page> actual = topnav.getItems();
		assertNotNull(actual);
	}

	@Test
	public void testGetItemsGreaterZero() {

		currentPagePath = "/content/actingreel/en/home";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		topnav.init(bindings);
		List<Page> pageList = topnav.getItems();
		assert(pageList.size()>0);
	}
	
	@Test
	public void testGetItemsZero() {

		currentPagePath = "/content/actingreel/en/home/about";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		topnav.init(bindings);
		List<Page> pageList = topnav.getItems();
		assert(pageList.size()==0);
	}
	
	@Test
	public void testGetListCorrect() {

		currentPagePath = "/content/actingreel/en/home";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		topnav.init(bindings);
		List<Page> actual = topnav.getItems();
		assertEquals(1,actual.size());
		assertEquals("About",actual.get(0).getTitle());
	}
	
	@Test
	public void testGetListCorrectDepth() {

		currentPagePath = "/content/actingreel/en";
		try {
			setUpTest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		topnav.init(bindings);
		List<Page> actual = topnav.getItems();
		assertEquals(1,actual.size());
		assertEquals("Home",actual.get(0).getTitle());
	}
}
