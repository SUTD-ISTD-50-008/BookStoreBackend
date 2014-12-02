package tk.bryanyap.bookstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.Consumes;
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

@Path("/recommendations")
public class Recommendations {

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
		String isbn = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));
		NodeList nList = doc.getElementsByTagName("recommendation");

		for (int temp = 0; temp < 1; temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				isbn = eElement.getElementsByTagName("isbn").item(0)
						.getTextContent();
			}
		}

		// String query = "SELECT " + "o1.isbn13 AS chosen_book, "
		// + "b1.title AS chosen_book_title, "
		// + "o2.isbn13 AS also_bought, "
		// + "b2.title AS also_bought_title, "
		// + "COUNT(o2.isbn13) AS pair_sales_count " + "FROM books b1 "
		// + "LEFT JOIN orders o1 ON o1.ISBN13=b1.ISBN13 "
		// + "LEFT JOIN orders o2 ON "
		// + "(o1.login_name=o2.login_name AND o1.isbn13 <> o2.isbn13) "
		// + "LEFT JOIN books b2 ON o2.ISBN13=b2.ISBN13 "
		// + "WHERE o1.isbn13='" + isbn + "' " + "GROUP BY o2.isbn13 "
		// + "ORDER BY COUNT(o2.isbn13) " + "DESC;";

		String query2 = "SELECT o2.isbn13 AS also_bought, "
				+ "b2.title AS also_bought_title, "
				+ "b2.authors AS also_bought_authors, "
				+ "COUNT(o2.isbn13) AS pair_sales_count "
				+ "FROM books b1 LEFT JOIN orders o1 ON o1.ISBN13 = b1.ISBN13 "
				+ "LEFT JOIN orders o2 ON "
				+ "(o1.login_name = o2.login_name AND o1.isbn13 <> o2.isbn13) "
				+ "LEFT JOIN books b2 ON o2.ISBN13 = b2.ISBN13 "
				+ "WHERE o1.isbn13 = '" + isbn + "' " + "GROUP BY o2.isbn13 "
				+ "ORDER BY COUNT(o2.isbn13) " + "DESC;";

		return query2;

	}
}
