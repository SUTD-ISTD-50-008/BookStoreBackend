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
@Path("/books")
public class Books {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getBooks() {
		return Database.getTableToXML("books_ratings_view");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String getBooks(String input) {
		try {
			return Database.queryToXML(this.generateQuery(input));
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

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));
		NodeList nList = doc.getElementsByTagName("search");
		NodeList search_by_isbn_nList = doc
				.getElementsByTagName("search_by_isbn");

		// Standard search
		if (search_by_isbn_nList.getLength() == 0 && nList.getLength() == 1) {

			String title = "";
			String authors = "";
			String publisher = "";
			String subject = "";
			String year_or_rating = "";
			String order_by = "";

			for (int temp = 0; temp < 1; temp++) {
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
					year_or_rating = eElement
							.getElementsByTagName("year_or_rating").item(0)
							.getTextContent();
					order_by = eElement.getElementsByTagName("order_by")
							.item(0).getTextContent();
				}
			}

			// Set year_or_rating and order_by to the valid sql commands
			if (year_or_rating.equals("rating")) {
				year_or_rating = "average_review";
			} else {
				year_or_rating = "publication_year";
			}
			if (order_by.equals("descending")) {
				order_by = "desc";
			} else {
				order_by = "";
			}
			String orderByQuery = "order by " + year_or_rating + " " + order_by;

			String query = "select * from books_ratings_view where title like '%"
					+ title
					+ "%' and authors like '%"
					+ authors
					+ "%' and publisher like '%"
					+ publisher
					+ "%' and subject like '%"
					+ subject
					+ "%'"
					+ orderByQuery
					+ ";";

			return query;

		} else if (search_by_isbn_nList.getLength() == 1
				&& nList.getLength() == 0) {
			// Search by isbn

			String isbn = "";

			for (int temp = 0; temp < 1; temp++) {
				Node nNode = search_by_isbn_nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					isbn = eElement.getElementsByTagName("isbn").item(0)
							.getTextContent();
				}
			}

			String query = "select * from books_ratings_view where ISBN13='" + isbn + "';";

			return query;

		} else {

			throw new InvalidInputException();

		}

	}
}
