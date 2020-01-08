package actingreel.core.workflow;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
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

import static com.day.cq.commons.jcr.JcrConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

@Component(service=WorkflowProcess.class,
	property= {
			   Constants.SERVICE_DESCRIPTION+"=Email Contact Process Step"
})
public class EmailContactProcessStep implements WorkflowProcess{

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
			
			WorkflowData workflowMetaData = item.getWorkflowData();
			MetaDataMap itemDataMap = item.getMetaDataMap();
			MetaDataMap dataMap = workflowMetaData.getMetaDataMap();
			
			
			LOGGER.info("***** Logger Logging!");
			String historyEntryPath = itemDataMap.get("historyEntryPath",String.class);
			Node payloadNode = ((Session)session.adaptTo(Session.class)).getNode(historyEntryPath+"/workItem/metaData");
			
			Property emailFromHistory = payloadNode.getProperty("sendEmail");
			Property archiveFromHistory = payloadNode.getProperty("archiveEmail");
			
			// DEBUG
			debugBasic(dataMap,workflowMetaData);

			debugMetaDataMap(itemDataMap);
			System.out.println("************-- Workflow Data Map -- *********");
			debugMetaDataMap(dataMap);
			System.out.println("************-- args --*************");
			debugMetaDataMap(args);
			System.out.println("*****``````` history Email: "+ emailFromHistory.getString() + "  -  "+ emailFromHistory.getClass());
			System.out.println("*****``````` archive Email: "+ archiveFromHistory.getString() + "  -  "+ archiveFromHistory.getClass());			
			
			if(workflowMetaData.getPayloadType().equals(NT_FILE)) {
				Boolean email = (Boolean) dataMap.get("email",Boolean.class);
				Boolean archive = (Boolean) dataMap.get("archive",Boolean.class);
				
				if(email) {
					sendEmail(workflowMetaData, session);
				}
				if(archive) {
					archiveEmail(workflowMetaData, session);
				} else {
					deleteEmail(workflowMetaData, session);
				}
			} else {
				LOGGER.error(workflowMetaData.getPayloadType() +
						" " + 
						workflowMetaData.getPayload().toString());
				throw new IllegalArgumentException("Payload type mismatch.");
			}

		} catch (Exception e) {
			LOGGER.error("******* Error from"+e.getMessage());
			System.out.println(args.entrySet().toString());
			System.out.println(item.getWorkflowData().getMetaDataMap().entrySet().toString());
			e.printStackTrace();
			
		}
		
	}
	
	private void debugBasic(MetaDataMap dataMap, WorkflowData workflowData) {
		System.out.println("***** Payload Type: "+workflowData.getPayloadType());
		System.out.println("***** Email property: "+dataMap.get("email",String.class));
		System.out.println("***** Archive property: "+dataMap.get("archive",String.class));
	}

	private void debugMetaDataMap(MetaDataMap dataMap) {
		Set<Entry<String,Object>> entrySet = dataMap.entrySet();
		for(Entry entry : entrySet) {
			System.out.println("***** Argument in map: "+entry.getKey().toString()+" - "+entry.getValue().toString());
				System.out.println("***** Class of previous item: "+entry.getClass());
		}
	}

	private void deleteEmail(WorkflowData workflowData, WorkflowSession session) 
			throws RepositoryException, 
				   PersistenceException {
		Node emailNode = (Node) workflowData.getPayload();
		resolver = session.adaptTo(ResourceResolver.class);
		Resource toBeDeleted = resolver.getResource(emailNode.getPath());
		resolver.delete(toBeDeleted);
		resolver.commit();
	}

	private void archiveEmail(WorkflowData workflowData, WorkflowSession session) 
			throws PersistenceException, 
				   RepositoryException {
		Node emailNode = (Node) workflowData.getPayload();
		resolver = session.adaptTo(ResourceResolver.class);
		resolver.move(emailNode.getPath(), ARCHIVE_PATH + emailNode.getName());
		resolver.commit();
		
	}

	private void sendEmail(WorkflowData workflowData, WorkflowSession session) 
			throws RepositoryException, 
				   IOException, 
				   EmailException {
		
		MessageGateway<Email> gateway;
		Email email = new SimpleEmail();
		
		// Collect Email nt:file and jcr:content node.
		Node emailNode = (Node) workflowData.getPayload();
		Node jcrContentNode = emailNode.getNode(JCR_CONTENT);
		
		// Collect Email Binary from jcr:content node.
		Binary data = (Binary) jcrContentNode.getProperty(JCR_DATA);
		InputStream stream = data.getStream();
		InputStreamReader reader = new InputStreamReader(stream);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while(reader.ready()) {
			output.write(reader.read());
		}
		
		// Populate email global variables.
		emailTitle = emailNode.getName();
		emailMessage = output.toString();
		email.addTo(toAddress);
		email.setMsg(emailMessage);
		email.setSubject(emailTitle);
	
		gateway = messageGatewayService.getGateway(Email.class);
		gateway.send(email);
	}

}
