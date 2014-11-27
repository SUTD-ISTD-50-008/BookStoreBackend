package tk.bryanyap.bookstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.naming.directory.InvalidAttributeValueException;
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
@Path("/buy")
public class Buy {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String buy() {
		return "<html><h1>Error!</h1><p>Please buy through the app.</p></html>";
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes
	public String buy(String input) {
		ArrayList<String> isbns = new ArrayList<String>();
		ArrayList<Integer> quantities = new ArrayList<Integer>();
		String login_name = "";

		// Parse the input

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(input
					.getBytes()));
			NodeList nList = doc.getElementsByTagName("buy");
			// If non-inserting operation detected
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					login_name = eElement.getElementsByTagName("login_name")
							.item(0).getTextContent();
					String isbn = eElement.getElementsByTagName("isbn").item(0)
							.getTextContent();
					String quantity = eElement.getElementsByTagName("quantity")
							.item(0).getTextContent();

					isbns.add(isbn);
					quantities.add(Integer.parseInt(quantity));
				}
			}
		} catch (ParserConfigurationException e) {
			return Database.error(e);
		} catch (SAXException e) {
			return Database.error(e);
		} catch (IOException e) {
			return Database.error(e);
		}

		try {
			int[] availabilityArray = availablilityCheck(isbns, quantities);
			for (int remainder : availabilityArray) {
				if (remainder < 0) {
					// If any of the requested books are not available, simply
					// return back the entire availabilityArray
					return Database.error(Arrays.toString(availabilityArray));
				}
			}

			// Proceed to purchase if no shortages detected
			// Get the current system time to set as the order time

			String oidInsertQuery = "insert into orderid_monitor values();";
			Database.insert(oidInsertQuery);
			int orderID = Integer
					.parseInt(Database
							.queryFirstResult("select OID from orderid_monitor order by OID desc limit 1"));

			// Process the order once the orderid_monitor table is modified and
			// orderID is retrieved
			return processOrder(isbns, quantities, orderID, login_name);

		} catch (InvalidAttributeValueException e) {
			return Database
					.error("Number of ISBNs and Quantities do not match up.");
		} catch (ClassNotFoundException e) {
			return Database.error(e.getMessage() + ",ClassNotFoundException");
		} catch (SQLException e) {
			return Database.error(e.getMessage() + ",SQLException");
		}

	}

	/**
	 * Process the orders book by book according to the input list. Processing
	 * is done by updating the quantities for each book in the book table
	 * followed with an insert into the order table. Each different book results
	 * in a round of updating and insertion.
	 * 
	 * @param isbns
	 * @param quantities
	 * @param orderID
	 * @throws InvalidAttributeValueException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private String processOrder(ArrayList<String> isbns,
			ArrayList<Integer> quantities, int orderID, String login_name)
			throws InvalidAttributeValueException, ClassNotFoundException,
			SQLException {
		if (isbns.size() != quantities.size()) {
			throw new InvalidAttributeValueException();
		}

		for (int i = 0; i < isbns.size(); i++) {
			// Not used: Database trigger automatically does this.
			// Update the Books table, decrease the quantity
			/*
			 * String booksUpdateQuery = "update";
			 * Database.update(booksUpdateQuery);
			 */

			// Insert order into the order table
			String ordersInsertQuery = "insert into orders (number_of_copies, OID, login_name, ISBN13) values ("
					+ quantities.get(i)
					+ ", "
					+ orderID
					+ ", '"
					+ login_name
					+ "', '" + isbns.get(i) + "');";
			Database.insert(ordersInsertQuery);
		}
		return Database.success();
	}

	/**
	 * Check the database for the availability of the books in the quantities
	 * requested. Return an integer array containing the remainder when the
	 * quantity requested for the book is subtracted from the quantity available
	 * in the database.
	 * 
	 * @param isbns
	 * @param quantities
	 * @return remainderArray
	 * @throws InvalidAttributeValueException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private int[] availablilityCheck(ArrayList<String> isbns,
			ArrayList<Integer> quantities)
			throws InvalidAttributeValueException, ClassNotFoundException,
			SQLException {
		if (isbns.size() != quantities.size()) {
			throw new InvalidAttributeValueException();
		}

		int[] results = { 0, 0, 0 };

		for (int i = 0; i < isbns.size(); i++) {
			ResultSet resultSet = Database
					.queryToResultSet("select * from books where ISBN13='"
							+ isbns.get(i) + "';");
			ResultSetMetaData rsmd = resultSet.getMetaData();

			int colCount = rsmd.getColumnCount();

			// Check that the result from the database has sufficient quantity
			// for a particular book
			while (resultSet.next()) {
				for (int ii = 1; ii <= colCount; ii++) {
					if (rsmd.getColumnName(ii) == "copies_in_inventory") {
						int quantity = Integer.parseInt(resultSet.getObject(ii)
								.toString());
						results[i] = quantity - quantities.get(i);
					}
				}
			}
		}

		return results;
	}
}
