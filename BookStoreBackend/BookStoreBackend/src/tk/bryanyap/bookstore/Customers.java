package tk.bryanyap.bookstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
@Path("/customers")
public class Customers {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getCustomers() {
		return Database.getTableToXML("customers");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String getCustomer(String input) {
		try {
			return Database.queryToXML(generateQuery(input));
		} catch (ParserConfigurationException e) {
			return Database.error(e);
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		} catch (InvalidInputException e) {
			return Database.error(e);
		}
	}

	private String generateQuery(String xmlString)
			throws ParserConfigurationException, SAXException, IOException,
			InvalidInputException {
		String login_name = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc;
		doc = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
		NodeList nList = doc.getElementsByTagName("customer");
		if (nList.getLength() > 1) {
			throw new InvalidInputException();
		}

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				login_name = eElement.getElementsByTagName("login_name")
						.item(0).getTextContent();

			}
		}

		String query = "select * from customers where login_name='"
				+ login_name + "';";

		return query;

	}
}
