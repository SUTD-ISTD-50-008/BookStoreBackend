package tk.bryanyap.bookstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Bryan Yap
 *
 */
@Path("/login")
public class Login {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String login() {
		return "<html><h1>Error!</h1><p>Please buy through the app.</p></html>";
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String login(String input) {
		ResultSet results;
		try {
			results = Database.queryToResultSet(generateQuery(input));
			int count = 0;
			while (results.next()) {
				++count;
			}
			// If the result set contains only one entry, return a Success
			// message
			if (count == 1) {
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				return "<Success>" + dateFormat.format(cal.getTime())
						+ "</Success>";
			} else {
				return "<Fail>Invalid userid or password.\nPlease register first.</Fail>";
			}
		} catch (ClassNotFoundException e) {
			return Database.error(e);
		} catch (SQLException e) {
			return Database.error(e);
		}

		// Count the number of results retrieved, return error message if
		// ResultSet is empty

	}

	private String generateQuery(String xmlString) {
		String login_name = "";
		String password = "";

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder
					.parse(new ByteArrayInputStream(xmlString.getBytes()));
			NodeList nList = doc.getElementsByTagName("login");
			if (nList.getLength() > 1) {
				throw new InvalidInputException();
			}

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					login_name = eElement.getElementsByTagName("login_name")
							.item(0).getTextContent();
					password = eElement.getElementsByTagName("password")
							.item(0).getTextContent();
				}
			}

			String query = "select * from customers where login_name='"
					+ login_name + "' and password='" + password + "';";

			return query;
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		} catch (InvalidInputException e) {
			return Database.error(e);
		} catch (ParserConfigurationException e) {
			return Database.error(e);
		}

	}
}
