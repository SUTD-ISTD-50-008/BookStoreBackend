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
@Path("/reviews")
public class Reviews {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getReviews() {
		return Database.getTableToXML("reviews");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String getReviews(String input) {
		return input;
	}
	
	private String generateQuery(String xmlString) {
		String title = "";
		String authors = "";
		String publisher = "";
		String subject = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
					.getBytes()));
			NodeList nList = doc.getElementsByTagName("search");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					title = eElement.getElementsByTagName("title").item(0)
							.getTextContent();
					authors = eElement.getElementsByTagName("authors").item(0)
							.getTextContent();
					publisher = eElement.getElementsByTagName("publisher")
							.item(0).getTextContent();
					subject = eElement.getElementsByTagName("subject").item(0)
							.getTextContent();
				}
			}

		} catch (ParserConfigurationException e) {
			return Database.error(e);
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		}

		String query = "select * from books where title like '%" + title
				+ "%' and authors like '%" + authors
				+ "%' and publisher like '%" + publisher
				+ "%' and subject like '%" + subject + "%';";

		return query;
	}
}
