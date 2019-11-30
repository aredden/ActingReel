package actingreel.core.models;

import com.adobe.cq.sightly.WCMUsePojo;

public class FooterModel extends WCMUsePojo{

	private String text;
	
	@Override
	public void activate() throws Exception {
		
		text = getProperties().get("footerText",null);

	}
	
	public String getFooterText(){
		return text;
	}
	
	
}