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
@Path("/ratings")
public class Ratings {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getRatings() {
		return Database.getTableToXML("rates");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String getRatings(String input) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(input
					.getBytes()));
			NodeList rateList = doc.getElementsByTagName("rate");
			NodeList searchList = doc.getElementsByTagName("search");
			NodeList search_by_login = doc
					.getElementsByTagName("search_by_login");

			if ((rateList.getLength() == 1 && searchList.getLength() == 0 && search_by_login
					.getLength() == 0)) {
				return Database.insert(generateQueryInsert(input));
			} else if ((rateList.getLength() == 0
					&& searchList.getLength() == 1 && search_by_login
						.getLength() == 0)) {
				return Database.queryToXML(generateQuerySelect(input));
			} else if (rateList.getLength() == 0 && searchList.getLength() == 0
					&& search_by_login.getLength() == 1) {
				return Database.queryToXML(generateQuerySelectByLogin(input));
			} else {
				throw new InvalidInputException();
			}

		} catch (ParserConfigurationException e) {
			return Database.error(e);
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		} catch (ClassNotFoundException e) {
			return Database.error(e);
		} catch (SQLException e) {
			return Database.error(e);
		} catch (InvalidInputException e) {
			return Database.error(e);
		}

	}

	private String generateQueryInsert(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		String numerical_score = "";
		String login_name_review_writer = "";
		String login_name_review_rater = "";
		String isbn = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));
		NodeList nList = doc.getElementsByTagName("rate");

		for (int temp = 0; temp < 1; temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				numerical_score = eElement
						.getElementsByTagName("numerical_score").item(0)
						.getTextContent();
				login_name_review_writer = eElement
						.getElementsByTagName("login_name_review_writer")
						.item(0).getTextContent();
				login_name_review_rater = eElement
						.getElementsByTagName("login_name_review_rater")
						.item(0).getTextContent();
				isbn = eElement.getElementsByTagName("isbn").item(0)
						.getTextContent();
			}
		}

		String query = "insert into rates values (" + numerical_score + ", '"
				+ login_name_review_writer + "', '" + login_name_review_rater
				+ "', '" + isbn + "'" + ");";

		return query;

	}

	private String generateQuerySelectByLogin(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		String login_name = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));
		NodeList nList = doc.getElementsByTagName("search");

		for (int temp = 0; temp < 1; temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				login_name = eElement.getElementsByTagName("login_name")
						.item(0).getTextContent();
			}
		}

		String query = "select * from rates where login_name_review_writer='"
				+ login_name + "';";

		return query;
	}

	private String generateQuerySelect(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		String isbn = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString
				.getBytes()));
		NodeList nList = doc.getElementsByTagName("search");

		for (int temp = 0; temp < 1; temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				isbn = eElement.getElementsByTagName("isbn").item(0)
						.getTextContent();
			}
		}

		String query = "select * from rates where ISBN13='" + isbn + "';";

		return query;
	}
}
