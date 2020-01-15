package actingreel.core.workflow.old;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
   
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

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

import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.EmailException;
   
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import com.day.cq.wcm.api.Page;

@Component(service=WorkflowProcess.class,
property= {
		   Constants.SERVICE_DESCRIPTION+"=Document Created Notify Admin",
		   "process.label=Document Created Notify Admin"
		   })
public class EmailContactNotifyAdmin implements WorkflowProcess{

@Reference
ResourceResolverFactory resolverFactory;

@Reference
MessageGatewayService messageGatewayService;

ResourceResolver resolver;

private static final Logger LOGGER = LoggerFactory.getLogger(EmailContactNotifyAdmin.class);

	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
		try {
			LOGGER.info("***** Executing: Document Created - Notify Admin.");
			
			WorkflowData data = item.getWorkflowData();
			MessageGateway<Email> gateway; 
			
			Email email = new SimpleEmail();
			
			email.addTo("alexander.h.redden@gmail.com");
			email.setSubject("AEM workflow test email");
			email.setMsg(
					"Hello this is a great message I have sent to you via email.\n"
					+ "I think we should meet in real life. (testing email)\n"
					+ item.getContentPath() 
					+ item.getWorkflowData().toString());
			
			gateway = messageGatewayService.getGateway(Email.class);
			
			gateway.send(email);
			
		} catch(EmailException | MailingException e) {
			
			LOGGER.error(e.getMessage());
			
		}
	
	}
}
