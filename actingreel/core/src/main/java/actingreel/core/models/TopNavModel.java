package actingreel.core.models;

import java.util.*;
import java.util.Iterator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.sightly.WCMUsePojo;

public class TopNavModel extends WCMUsePojo{
    private List<Page> items = new ArrayList<Page>();
    private Page rootPage;
    private PageManager pageMgr;
    private String rootString;
    // Initializes the navigation
    @Override
    public void activate() throws Exception {
		rootString = getProperties().get("parentPage",null);
    	
        findRootPage(rootString);
        Iterator<Page> childPages = rootPage.listChildren(new PageFilter(getRequest()));
        while (childPages.hasNext()) {
			items.add(childPages.next());
	   	}
    }

	private void findRootPage(String rootString) {
        if (rootString == null) {
        	rootPage = getCurrentPage();
        } else {
        	pageMgr = getPageManager();
        	rootPage = pageMgr.getPage(rootString).adaptTo(Page.class);
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