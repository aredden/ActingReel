package actingreel.core.workflow;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import static actingreel.core.constants.ServletConstants.EMAIL;
import static com.day.cq.commons.jcr.JcrConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(service=WorkflowProcess.class,
	property= {
			   Constants.SERVICE_DESCRIPTION+"=Email Contact Process Step"
})
public class EmailContactProcessStep implements WorkflowProcess{

	private static final String RECIPIENT_ADDRESS = "recipientAddress";

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	MessageGatewayService messageGatewayService;
	
	private ResourceResolver resolver;
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static final String ARCHIVE_PATH = "/content/actingreel/documents/archive/";
	private String toAddress = "alexander.h.redden@gmail.com";
	private String emailTitle = "";
	private String emailMessage = "";
	
	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
		try {
			resolver = session.adaptTo(ResourceResolver.class);
			WorkflowData workflowMetaData = item.getWorkflowData();
			MetaDataMap itemDataMap = item.getMetaDataMap();
			
			String historyEntryPath = itemDataMap.get("historyEntryPath",String.class);
			Node payloadNode = ((Session)session.adaptTo(Session.class)).getNode(historyEntryPath+"/workItem/metaData");
			
			boolean email;
			boolean archive;
			boolean dialogFunctional =
					payloadNode.hasProperty("email@Delete") &&
					payloadNode.hasProperty("archive@Delete");
			
			if(dialogFunctional) {
				email = payloadNode.hasProperty(EMAIL);
				archive = payloadNode.hasProperty("archive");
			} else {
				throw new Exception("Dialog values not seen by workflow.");
			}

			String payloadPath = workflowMetaData.getPayload().toString();
			Resource payloadResource = resolver.getResource(workflowMetaData.getPayload().toString());

			if(payloadResource.getResourceType().equals("nt:emailfile")) {
				
				if(email) {
					sendEmail(payloadPath, session);
				}
				if(archive) {
					archiveEmail(payloadPath, session, payloadResource.getName());
				} else {
					deleteEmail(payloadPath, session);
				}
			}
		} catch (Exception e) {
			LOGGER.error("***** Error from:"+e.getMessage());
			LOGGER.info("***** entryset workflowargs on exception: "+args.entrySet().toString());
			LOGGER.info("***** item data entryset on exception: "+item.getWorkflowData().getMetaDataMap().entrySet().toString());
		}

	}

	private void deleteEmail(String payload, WorkflowSession session) 
			throws RepositoryException, 
				   PersistenceException {
		Session sessionFromWorkflow = session.adaptTo(Session.class);
		sessionFromWorkflow.removeItem(payload);
		sessionFromWorkflow.save();
	}

	private void archiveEmail(String payloadPath, WorkflowSession session, String emailTitle) 
			throws PersistenceException, 
				   RepositoryException {
		Session sessionFromWorkflow = session.adaptTo(Session.class);
		sessionFromWorkflow.move(payloadPath, ARCHIVE_PATH + emailTitle);
		sessionFromWorkflow.save();
	}

	private void sendEmail(String payload, WorkflowSession session) 
			throws RepositoryException, 
				   IOException, 
				   EmailException {
		// Email related variable instantiation.
		MessageGateway<Email> gateway;
		Email email = new SimpleEmail();
		
		// ResourceResolver for gathering data on payload.
		ResourceResolver resolver = session.adaptTo(ResourceResolver.class);
		
		
		
		// Collect email & jcr:content resources.s
		
		Resource emailResource = resolver.getResource(payload);
		Resource jcrContentResource = emailResource.getChild(JCR_CONTENT);
		
		String recipientAddress = (String) emailResource.getValueMap().get(RECIPIENT_ADDRESS);
		if(recipientAddress.equals("unset")) {
			recipientAddress = toAddress;
		}
		
		// Collect Email Binary from jcr:content node.
		InputStream dataStream = jcrContentResource.getValueMap().get(JCR_DATA, InputStream.class);
		InputStreamReader reader = new InputStreamReader(dataStream);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while(reader.ready()) {
			output.write(reader.read());
		}
		
		// Populate email global variables.
		emailTitle = emailResource.getName();
		emailMessage = output.toString();
		email.addTo(recipientAddress);
		email.setMsg(emailMessage);
		email.setSubject(emailTitle);

		gateway = messageGatewayService.getGateway(Email.class);
		
		// Send email
		gateway.send(email);
	}
	
}
