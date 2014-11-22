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
@Path("/reviews")
public class Reviews {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getReviews() {
		return Database.getTableToXML("review_avgrating_view");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String getReviews(String input) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(input
					.getBytes()));
			NodeList reviewList = doc.getElementsByTagName("review");
			NodeList searchList = doc.getElementsByTagName("search");

			if (reviewList.getLength() == 1 && searchList.getLength() == 0) {
				return Database.insert(generateQueryInsert(input));
			} else if (reviewList.getLength() == 0
					&& searchList.getLength() == 1) {
				return Database.getTableToXML(generateQuerySelect(input));
			} else {
				throw new InvalidInputException();
			}

		} catch (ParserConfigurationException e) {
			return Database.error(e);
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		} catch (InvalidInputException e) {
			return Database.error(e);
		} catch (ClassNotFoundException e) {
			return Database.error(e);
		} catch (SQLException e) {
			return Database.error(e);
		}

	}

	private String generateQueryInsert(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {
		String numerical_score = "";
		String short_text = "";
		String isbn = "";
		String login_name = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));

		NodeList nList = doc.getElementsByTagName("review");

		for (int temp = 0; temp < 1; temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				numerical_score = eElement
						.getElementsByTagName("numerical_score").item(0)
						.getTextContent();
				short_text = eElement.getElementsByTagName("short_text")
						.item(0).getTextContent();
				isbn = eElement.getElementsByTagName("isbn").item(0)
						.getTextContent();
				login_name = eElement.getElementsByTagName("login_name")
						.item(0).getTextContent();

			}
		}

		String query = "insert into reviews (numerical_score, short_text, ISBN13, login_name) values ("
				+ numerical_score
				+ ", '"
				+ short_text
				+ "', '"
				+ isbn
				+ "', '"
				+ login_name + "');";

		return query;

	}

	private String generateQuerySelect(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		String isbn = "";
		String limit = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));

		NodeList nList = doc.getElementsByTagName("search");

		// If non-inserting operation detected
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				isbn = eElement.getElementsByTagName("isbn").item(0)
						.getTextContent();
				limit = eElement.getElementsByTagName("limit").item(0)
						.getTextContent();
			}
		}

		String query = "select * from review_avgrating_view where ISBN13='"
				+ isbn
				+ "' (order by case when average_rating_score = 'no ratings given yet' then average_rating_score = 0 end), average_rating_score desc limit "
				+ limit + ";";

		return query;

	}

}
