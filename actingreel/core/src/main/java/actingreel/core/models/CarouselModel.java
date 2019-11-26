package actingreel.core.models;

import java.util.*;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;


public class CarouselModel extends WCMUsePojo {

	private List<String> imageData = new ArrayList<String>();
	
	@Override
	public void activate() throws Exception {
			Iterable<Resource> images = getResource().getChildren();
			
			for(Resource x : images) {
				String map = x.getValueMap().get("fileReference",null);
				imageData.add(map);
			}
	}
	
	public List<String> getItems(){
		return imageData;
	}

}