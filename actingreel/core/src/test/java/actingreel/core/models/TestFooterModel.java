package actingreel.core.models;

import static org.junit.Assert.*;

import javax.script.Bindings;

import org.junit.Test;
import org.apache.sling.api.resource.Resource;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import static org.mockito.Mockito.*;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class TestFooterModel {
	
	@Rule
	public AemContext ctx = new AemContext();
	
	@Mock
	protected FooterModel footer;
	
	@Mock
	protected Resource resource = mock(Resource.class);
	
	@Mock
	protected Bindings bindings = mock(Bindings.class);
	
	Logger LOGGER = LoggerFactory.getLogger(TestFooterModel.class);
	
	public void footerSetUp(String pagePath) {
		try { setUp(pagePath); }
		catch(Exception e){
			LOGGER.debug(e.getMessage(), pagePath.toString());
		}
	}
	
	public void setUp(String pagePath) throws Exception{
		ctx.load().json("/actingreel/core/models/Footer.json","/content/actingreel/en");
		footer = new FooterModel();
		
		when(bindings.get("properties"))
		.thenReturn(ctx.resourceResolver()
		.getResource(pagePath+"/jcr:content/root/responsivegrid/footer")
		.getValueMap());
		when(bindings.get("pageManager"))
		.thenReturn(ctx.pageManager());
	}

	@Test
	public void testGetFooterText() {
		footerSetUp("/content/actingreel/en/home");
		footer.init(bindings);
		String actual = footer.getFooterText();
		String expected = "All rights reserved Juliana Redden (tm) 2019 2020";
		LOGGER.info("**** Footer text: "+footer.getFooterText());
		assertEquals(expected,actual);
	}
	
	@Test
	public void testGetPages() {
		footerSetUp("/content/actingreel/en/home");
		footer.init(bindings);
		Page work = footer.getWork();
		Page about = footer.getAboutUs();
		Page social = footer.getSocial();
		Page resume = footer.getResume();
		assertNotNull(work);
		assertNotNull(about);
		assertNotNull(social);
		assertNotNull(resume);
	}
}
