package apps.actingreel.components.content.footer;

import com.adobe.cq.sightly.WCMUsePojo;

public class Footer extends WCMUsePojo{

	private String text;
	@Override
	public void activate() throws Exception {
		
		text = getProperties().get("footerText",null);
		System.out.println(text);
	}
	
	public String getFooterText(){
		return text;
	}
	
}