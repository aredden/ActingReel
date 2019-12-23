package actingreel.core.servlets;



import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.plugins.value.BinaryBasedBlob;
import org.apache.jackrabbit.oak.util.NodeUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.Principal;
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


	static final String TITLE = "title";
	static final String MESSAGE = "message";
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	private ResourceResolver resourceResolver;
	private static final long serialVersionUID = 1L;
	private static final String SUBSERVICE_NAME = "actingreel-emailcontact";
	
	public Logger LOGGER = LoggerFactory.getLogger(EmailContactServlet.class);
	
	protected void doPost(SlingHttpServletRequest req,
					   SlingHttpServletResponse res)
				throws IOException,
					   ServletException
	{
		
		try {
			resourceResolver = getAuthenticatedResourceResolver();
			Session session = resourceResolver.adaptTo(Session.class);
			Node node = session.getNode(ACTINGREEL_DOCUMENTS);
			HashMap<String, String> jsonMap = parseJSON(req);
			Node fileNode = node.addNode(jsonMap.get(TITLE),NT_FILE);
			Node resNode = fileNode.addNode(JCR_CONTENT,NT_RESOURCE);
			Binary value = session.getValueFactory().createBinary(new ByteArrayInputStream(jsonMap.get(MESSAGE).getBytes()));
			resNode.setProperty(JCR_MIMETYPE, APPLICATION_OCTET_STREAM);
			resNode.setProperty(JCR_DATA, value);
			Calendar lastModified = Calendar.getInstance();
			lastModified.setTimeInMillis(lastModified.getTimeInMillis());
			resNode.setProperty(JCR_LASTMODIFIED, lastModified.getTimeInMillis());
			session.save();
			res.addHeader("EmailStatus", "Success");
			
			
		} catch (LoginException e) {
			res.setStatus(401);
			res.getWriter().write("LoginException Error" + e.toString());
			LOGGER.error("**** Login Error: ", e);
		} catch (JsonIOException e) {
			res.setStatus(500);
			res.getWriter().write("JsonIOException Error" + e.toString());
			LOGGER.error("**** Json IO Error: ", e);
		} catch (PathNotFoundException e) {
			res.setStatus(500);
			res.getWriter().write("PathNotFoundException Error" + e.toString());
		} catch (RepositoryException e) {
			res.setStatus(500);
			res.getWriter().write("RepositoryException Error" + e.toString());
		}
	}
	
	protected void doGet(SlingHttpServletRequest req,
			   		  SlingHttpServletResponse res)
			   throws IOException,
				   	  ServletException
	{
		res.setHeader("wedidit", "yes");
	}
	
	private HashMap<String, String> parseJSON(SlingHttpServletRequest req) throws JsonIOException, JsonSyntaxException, IOException {
		HashMap<String, String> dataMap = new HashMap<String,String>();
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(req.getReader()).getAsJsonObject();
		dataMap.put(TITLE, json.get(TITLE).getAsString());
		dataMap.put("message", json.get("message").getAsString());
		dataMap.put("email", json.get("email").getAsString());
		return dataMap;
	}
	
	private ResourceResolver getAuthenticatedResourceResolver() throws LoginException{
		Map<String,Object> map = new HashMap<String,Object>();
		ResourceResolver resolver = null;
		map.put(ResourceResolverFactory.SUBSERVICE,SUBSERVICE_NAME);
		resolver = resourceResolverFactory.getServiceResourceResolver(map);
		return resolver;
	}
	


}
