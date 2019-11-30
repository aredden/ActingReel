package actingreel.core.models;

import java.util.*;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import com.adobe.cq.sightly.WCMUsePojo;


public class CarouselModel extends WCMUsePojo {

	private List<String> imageData;
	
	@Override
	public void activate() throws Exception {
		 	imageData = new ArrayList<String>();
		 	Resource imagesNode;
		 	Iterable<Resource> images;
		 	Resource carousel = getResource();
		 	imagesNode = carousel.hasChildren() ? carousel.getChild("images") : null;
			if((imagesNode!=null) && imagesNode.hasChildren()) {
				images = imagesNode.getChildren();
				imageData = getImages(images);
			}
		
	}
	
	private List<String> getImages(Iterable<Resource> images){
		for(Resource image : images) {
			ValueMap imageMap = image.getValueMap();
			String map = imageMap.get("fileReference","");
			/*
			 * If there is no title, fileReference,
			 * or the fileReference is empty, don't add
			 * the resource to the list of images.
			 */
			if(!(map == null) && !(map == "")) {
				imageData.add(map);
			}
		}
		return imageData;
	}
	
	public List<String> getItems(){
		return imageData;
	}
	
	public Boolean isEmpty() {
		return imageData.isEmpty();
	}
	
	public ValueMap getValueMap() {
		return getResource().getValueMap();
	}
	
	
	
}

