package apps.actingreel.components.structure.topnav;

import java.util.*;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;

import com.adobe.cq.sightly.WCMUsePojo;

public class TopNav extends WCMUsePojo{
    private List<Page> items = new ArrayList<Page>();
    private Page rootPage;
    private PageManager pageMgr;
    private String rootString;
    private Page currentPage;
    // Initializes the navigation
    @Override
    public void activate() throws Exception {
    	
        rootString = getProperties().get("parentPage",null);
        currentPage = getCurrentPage();
        pageMgr = getPageManager();
        
        findRootPage(rootString);
        populatePageList();

    }
    
    private void populatePageList() {
        Iterator<Page> childPages = rootPage.listChildren(new PageFilter(getRequest()));
        while (childPages.hasNext()) {
			items.add(childPages.next());
	   	}
    }
    
    private void findRootPage(String rootStr) {
        if (rootString == null) {
        	rootPage = currentPage;
        } else {
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