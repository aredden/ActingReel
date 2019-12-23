package actingreel.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.lang.Throwable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;
import static com.day.cq.commons.jcr.JcrConstants.*;
import static actingreel.core.constants.ServletConstants.*;
import static org.osgi.framework.Constants.*;
import static org.apache.jackrabbit.vault.util.MimeTypes.*;


import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component(
	service=Servlet.class,
    property={
            SERVICE_DESCRIPTION + EMAIL_CONTACT_SERVICE_DESC,
            SLING_SERVLET_METHODS+EMAIL_CONTACT_ALLOWED_METHODS,
            SLING_SERVLET_PATHS+EMAIL_CONTACT_PATH,
            SLING_SERVLET_NAME+EMAIL_CONTACT_SERVLET
    })
public class EmailContactServlet extends SlingAllMethodsServlet{
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	private ResourceResolver resourceResolver;

	public Logger LOGGER = LoggerFactory.getLogger(EmailContactServlet.class);
	
	protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse res) 
				throws IOException,
					   ServletException {
		try {
			resourceResolver = getEmailContactAuthenticatedResourceResolver();
			Session session = resourceResolver.adaptTo(Session.class);
			Node node = session.getNode(ACTINGREEL_DOCUMENTS);
			createFileFromSession(session, req, node);
			session.save();
			res.addHeader("EmailStatus", "Success");

		} catch (LoginException e) {
			res.setStatus(401);
			res.getWriter().write("LoginException Error \n" + e.toString());
		} catch (JsonIOException e) {
			res.setStatus(500);
			res.getWriter().write("JsonIOException Error \n" + e.toString());
		} catch (PathNotFoundException e) {
			res.setStatus(500);
			res.getWriter().write("PathNotFoundException Error \n" + e.toString());
		} catch (RepositoryException e) {
			res.setStatus(500);
			res.getWriter().write("RepositoryException Error \n" + e.toString());
		} catch (IncorrectPOSTParametersException e) {
			res.setStatus(400);
			res.getWriter().write("Body must include email, title, & message.");
		}
	}
	
	private void createFileFromSession(Session session, SlingHttpServletRequest req, Node parentNode) 
			 throws ItemExistsException, 
					PathNotFoundException, 
					NoSuchNodeTypeException, 
					LockException, 
					VersionException, 
					ConstraintViolationException, 
					RepositoryException, 
					JsonIOException, 
					JsonSyntaxException, 
					IOException, 
					IncorrectPOSTParametersException {
		HashMap<String, String> jsonMap = parsePOSTEmailJSON(req);
		String title = findAppropriateNodeName(parentNode,new StringBuilder().append(jsonMap.get(TITLE)));
		Node fileNode = parentNode.addNode(title, NT_FILE);
		Node resNode = fileNode.addNode(JCR_CONTENT,NT_RESOURCE);
		Binary value = session.getValueFactory().createBinary(
				new ByteArrayInputStream(jsonMap.get(MESSAGE).getBytes())
		);
		resNode.setProperty(JCR_MIMETYPE, APPLICATION_OCTET_STREAM);
		resNode.setProperty(JCR_DATA, value);
		Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis(lastModified.getTimeInMillis());
		resNode.setProperty(JCR_LASTMODIFIED, lastModified.getTimeInMillis());
	}
	
	private HashMap<String, String> parsePOSTEmailJSON(SlingHttpServletRequest req) 
			throws JsonIOException,
					JsonSyntaxException, 
					IOException, 
					IncorrectPOSTParametersException {
		
		HashMap<String, String> dataMap = new HashMap<String,String>();
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(req.getReader()).getAsJsonObject();
		if(!(json.has(TITLE)&&json.has(MESSAGE)&&json.has(EMAIL))) {
			throw new IncorrectPOSTParametersException();
		}
		dataMap.put(TITLE, json.get(TITLE).getAsString());
		dataMap.put(MESSAGE, json.get(MESSAGE).getAsString());
		dataMap.put(EMAIL, json.get(EMAIL).getAsString());
		return dataMap;
	}
	
	private ResourceResolver getEmailContactAuthenticatedResourceResolver() 
			throws LoginException {
		Map<String,Object> map = new HashMap<String,Object>();
		ResourceResolver resolver = null;
		map.put(ResourceResolverFactory.SUBSERVICE,SUBSERVICE_NAME);
		resolver = resourceResolverFactory.getServiceResourceResolver(map);
		return resolver;
	}
	
	private String findAppropriateNodeName(Node parentNode, StringBuilder title) 
			throws RepositoryException {
		int i = 0;
		final String string = title.toString();
		while(parentNode.hasNode(title.toString())) {
			title = new StringBuilder(string);
			title.append(i);
			i++;
		}
		return title.toString();
	}
	
}

class IncorrectPOSTParametersException extends Throwable{
	
	private static final long serialVersionUID = -2664393461911032915L;
	
}