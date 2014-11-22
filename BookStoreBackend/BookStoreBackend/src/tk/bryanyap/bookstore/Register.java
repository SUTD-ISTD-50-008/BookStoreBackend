package tk.bryanyap.bookstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

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
@Path("/register")
public class Register {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String register() {
		return "<html><h1>Error!</h1><p>Please buy through the app.</p></html>";
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String register(String input) {
		try {
			return Database.insert(generateQuery(input));
		} catch (ClassNotFoundException e) {
			return Database.error(e);
		} catch (SQLException e) {
			return Database.error(e);
		}
	}

	private String generateQuery(String xmlString) {
		String first_name = "";
		String last_name = "";
		String password = "";
		String credit_card_number = "";
		String address = "";
		String phone_number = "";
		String login_name = "";

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder
					.parse(new ByteArrayInputStream(xmlString.getBytes()));
			NodeList nList = doc.getElementsByTagName("register");
			if (nList.getLength() > 1) {
				throw new InvalidInputException();
			}

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					first_name = eElement.getElementsByTagName("first_name")
							.item(0).getTextContent();
					last_name = eElement.getElementsByTagName("last_name")
							.item(0).getTextContent();
					password = eElement.getElementsByTagName("password")
							.item(0).getTextContent();
					credit_card_number = eElement
							.getElementsByTagName("credit_card_number").item(0)
							.getTextContent();
					address = eElement.getElementsByTagName("address").item(0)
							.getTextContent();
					phone_number = eElement
							.getElementsByTagName("phone_number").item(0)
							.getTextContent();
					login_name = eElement.getElementsByTagName("login_name")
							.item(0).getTextContent();

				}
			}

			String query = "insert into customers values ('" + first_name
					+ "', '" + last_name + "', '" + password + "', '"
					+ credit_card_number + "', '" + address + "', '"
					+ phone_number + "', '" + login_name + "'" + ");";

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
