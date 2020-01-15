package actingreel.core.models;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import static actingreel.core.constants.Constants.*;
public class EmailContactModel extends WCMUsePojo{

	Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	ResourceResolver resolver;
	
	private String id;
	private String recipientEmail;
	
	@Override
	public void activate() throws Exception {
		id = "emailcontact-" + String.valueOf(Math.abs(getResource().getPath().hashCode() - 1));
		recipientEmail = getProperties().get(RECIPIENT_EMAIL, null);
		setIdMapId();
	}
	
	private void setIdMapId() throws RepositoryException, PersistenceException {
		resolver = getResourceResolver();
		Resource idRes = resolver.getResource(ID_MAP_PATH);
		ModifiableValueMap idmap = idRes.adaptTo(ModifiableValueMap.class);
		if(idmap.containsKey(id)) {
			if(!idmap.get(id).equals(recipientEmail)) {
				idmap.replace(id,recipientEmail);
			}
		} else {
			idmap.put(id,recipientEmail);
		}
		resolver.commit();
		
	}
	
	public String getId() {
	    return id;
	}

}
