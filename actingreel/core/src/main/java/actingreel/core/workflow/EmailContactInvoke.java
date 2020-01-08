package actingreel.core.workflow;

import java.util.Calendar;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jackrabbit.commons.JcrUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.osgi.service.component.annotations.*;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Session;
import javax.jcr.Node;



//Adobe CQ Workflow APIs
import com.day.cq.workflow.model.WorkflowModel;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.launcher.ConfigEntry;
import com.day.cq.workflow.launcher.WorkflowLauncher;


//This is a component so it can provide or consume services
@Component
public class EmailContactInvoke {

    @Reference
    private WorkflowService workflowService;

    private Session session;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailContactInvoke.class);
    
    private static final String WORKFLOW_NAME = "EmailContactWorkflow";

	public String StartWorkflow(String workflowContentPath) {
		
	try {
		
	    ResourceResolver resourceResolver = resolverFactory.getResourceResolver(null);
	    session = resourceResolver.adaptTo(Session.class);
	    Node workflowNode = session.getNode(workflowContentPath);
	    
	    WorkflowSession wfSession = workflowService.getWorkflowSession(session);
	    WorkflowModel wfModel = wfSession.getModel(WORKFLOW_NAME); 
	    WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", workflowContentPath);
	    
	    wfSession.startWorkflow(wfModel, wfData);
	    
	    return WORKFLOW_NAME +" has been successfully invoked on this content: " + workflowContentPath ; 
	    }
		catch(Exception e)
		{
		    e.printStackTrace();
		}
		return null;
	}
	
	protected void debug(Node workNode) {
		StringBuilder results = new StringBuilder();
		final String NEWLINE = "\n";
		try {
			Boolean isLive = session.isLive();
			results.append(NEWLINE);
			results.append(
					isLive ? 
					"***** Success: Session is live." + NEWLINE: 
					"***** Failure: Session did not start." + NEWLINE);
			
			// Properties of email /documents node.
			PropertyIterator props = workNode.getProperties();
			
			// Notify that following list is Node Properties.
			results.append("***** Info: Node properties:" + NEWLINE);
			while(props.hasNext()) {
				Property property = (Property) props.next();
				results.append(property.getName() + " " +property.getValue().toString() + NEWLINE);
			}
			LOGGER.info(results.toString());
		}
		catch( RepositoryException e) {
			results.append("***** Error: RepositoryException when trying to access node properties." + NEWLINE);
		}
		
		
	}

}
