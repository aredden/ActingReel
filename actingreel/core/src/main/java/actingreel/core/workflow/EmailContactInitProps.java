package actingreel.core.workflow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
   
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
    
import javax.jcr.Repository; 
import javax.jcr.SimpleCredentials; 
import javax.jcr.Node; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
     
import org.apache.jackrabbit.commons.JcrUtils;
    
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.JcrUtils;
    
import javax.jcr.Session;
import javax.jcr.Node; 
import org.osgi.framework.Constants;
  
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
   
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory ; 
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.api.resource.Resource; 
import com.day.cq.wcm.api.Page; 



@Component(service=WorkflowProcess.class,
		   property= {
				   Constants.SERVICE_DESCRIPTION+"=Initialize Workflow Properties",
				   "process.label=Initialize Workflow Properties"
		   }
		)
public class EmailContactInitProps implements WorkflowProcess{

	@Reference
	ResourceResolverFactory resolverFactory;
	
	ResourceResolver resolver;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailContactInitProps.class);
	
	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
		try {
			LOGGER.info("***** Executing Document Collect and Delete.");
			
			resolver = session.adaptTo(ResourceResolver.class);
			
			WorkflowData data = item.getWorkflowData();
			MetaDataMap dataMap = data.getMetaDataMap();
			dataMap.put("emailchecked", "unset");
			dataMap.put("archivechecked","unset");
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
///libs/cq/inbox/gui/components/inbox/dialoginjection/render/render.jsp
//workitem.getWorkflow().getId()+"/data/metaData"