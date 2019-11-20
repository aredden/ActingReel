package apps.actingreel.components.structure.topnav;

import java.util.*;
import java.util.Iterator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.sightly.WCMUsePojo;

public class TopNav extends WCMUsePojo{
    private List<Page> items = new ArrayList<Page>();
    private Page rootPage;
    private PageManager pageMgr;
    private String rootString;
    private ResourceResolver resolver;
    // Initializes the navigation
    @Override
    public void activate() throws Exception {
    	
        rootString = getProperties().get("parentPage",null);
        
        if (rootString == null) {
        	rootPage = getCurrentPage();
        } else {
        	resolver = getRequest().getResourceResolver();
        	pageMgr = resolver.adaptTo(PageManager.class);
        	rootPage = pageMgr.getPage(rootString).adaptTo(Page.class);
        }
        
        Iterator<Page> childPages = rootPage.listChildren(new PageFilter(getRequest()));
        while (childPages.hasNext()) {
			items.add(childPages.next());
	   	}
    }

    // Returns the navigation items
    public List<Page> getItems() {
        return items;
    }
    // Returns the navigation root
    public Page getRoot() {
    	if(rootPage.equals(null)) {
    		return(getCurrentPage());
    	} else return rootPage;
    }
}