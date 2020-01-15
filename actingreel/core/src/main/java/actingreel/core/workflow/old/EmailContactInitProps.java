package actingreel.core.workflow.old;

   
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
    
import org.osgi.framework.Constants;
  
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;


   
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory ; 
import org.apache.sling.api.resource.ResourceResolver; 



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