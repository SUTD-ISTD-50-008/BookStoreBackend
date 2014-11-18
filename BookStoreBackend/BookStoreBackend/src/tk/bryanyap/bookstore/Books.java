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

@Path("/books")
public class Books {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getBooks() {
		return Database.getTableToXML("books");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public String getBooks(String input) {
		try {
			return Database.queryToXML(this.generateQuery(input));
		} catch (SAXException e) {
			return Database.error(e.getMessage() + ",SAXException");
		} catch (IOException e) {
			return Database.error(e.getMessage() + ",IOException");
		} catch (ParserConfigurationException e) {
			return Database.error(e.getMessage() + ",ParserConfigurationException");
		}
	}

	private String generateQuery(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {
		String title = "";
		String authors = "";
		String publisher = "";
		String subject = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
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
				publisher = eElement.getElementsByTagName("publisher").item(0)
						.getTextContent();
				subject = eElement.getElementsByTagName("subject").item(0)
						.getTextContent();
			}
		}

		String query = "select * from books where title like '%"
				+ title + "%' and authors like '%" + authors
				+ "%' and publisher like '%" + publisher
				+ "%' and subject like '%" + subject + "%';";

		return query;
	}

}
