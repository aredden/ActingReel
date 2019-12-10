package actingreel.core.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class FooterModel extends WCMUsePojo{

	private String text;
	private Page aboutUs;
	private Page work;
	private Page resume;
	private Page social;
	
	@Override
	public void activate() throws Exception {
		
		text = getProperties().get("footerText",null);
		
		PageManager pageMgr = getPageManager();
		aboutUs = pageMgr.getPage("/content/actingreel/en/home/about");
		work = pageMgr.getPage("/content/actingreel/en/home/work");
		resume = pageMgr.getPage("/content/actingreel/en/home/resume");
		social = pageMgr.getPage("/content/actingreel/en/home/socialmedia");
	}
	
	public String getFooterText(){
		return text;
	}
	
	public Page getAboutUs() {
		return aboutUs;
	}
	
	public Page getWork() {
		return work;
	}
	
	public Page getResume() {
		return resume;
	}
	
	public Page getSocial() {
		return social;
	}
	
	
}