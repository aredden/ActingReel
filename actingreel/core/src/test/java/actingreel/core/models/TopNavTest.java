package actingreel.core.models;

import static org.junit.Assert.*;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import javax.script.Bindings;

import io.wcm.testing.mock.aem.junit.AemContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.sling.api.SlingHttpServletRequest;

import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TopNavTest {

	@Mock
	protected TopNavModel topnav;
	
	@Mock
	protected Bindings bindings;
	
	@Mock
	SlingHttpServletRequest req;
	
	@Rule
	public AemContext ctx;
	
	@Before
	public void setUp() throws Exception {
		topnav = new TopNavModel();
		
		ctx = new AemContext();
		ctx.load().json("actingreel/core/models/TopNav.json","/content/actingreel");
		
		

		
	

		
	}

	@Test
	public void testSinglePage() {
		
		assertTrue(true);
	}

}
