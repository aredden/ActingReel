package actingreel.core.constants;


import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;

public interface ServletConstants {
	public static final String TYPE_JSON = "=json";
	public static final String EMAIL_CONTACT_ALLOWED_METHODS = "=["+METHOD_POST+"]";
	public static final String EMAIL_CONTACT_SERVICE_DESC = "=Email Contact Servlet";
	public static final String EMAIL_CONTACT_SERVLET = "=EmailContactServlet";
	public static final String EMAIL_CONTACT_PATH = "=/bin/actingreel/documents";
	public static final String ACTINGREEL_DOCUMENTS = "/content/actingreel/documents";
	public static final String SUBSERVICE_NAME = "actingreel-emailcontact";
	public static final String EMAIL = "email";
	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
}
