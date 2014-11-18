package tk.bryanyap.bookstore;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.naming.directory.InvalidAttributeValueException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	@Consumes(MediaType.APPLICATION_XML)
	public String buy(String input) {
		String[] isbns = null;
		String[] quantities = null;

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
			Timestamp orderTime = new Timestamp(System.currentTimeMillis());

			String oidInsertQuery = "insert into orderid_monitor values()";
			Database.insert(oidInsertQuery);
			int orderID = Integer.parseInt(Database
					.queryFirstResult("select max() from orderid_monitor"));

			// Process the order once the orderid_monitor table is modified and
			// orderID is retrieved
			processOrder(isbns, quantities, orderID, orderTime);

		} catch (InvalidAttributeValueException e) {
			return Database
					.error("Number of ISBNs and Quantities do not match up.");
		} catch (ClassNotFoundException e) {
			return Database.error(e.getMessage() + ",ClassNotFoundException");
		} catch (SQLException e) {
			return Database.error(e.getMessage() + ",SQLException");
		}

		return "<success>" + input + "</success>";
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
	 * @param orderTime
	 * @throws InvalidAttributeValueException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void processOrder(String[] isbns, String[] quantities, int orderID,
			Timestamp orderTime) throws InvalidAttributeValueException,
			ClassNotFoundException, SQLException {
		if (isbns.length != quantities.length) {
			throw new InvalidAttributeValueException();
		}

		for (int i = 0; i < isbns.length; i++) {
			// Update the Books table, decrease the quantity
			String booksUpdateQuery = "update";
			Database.update(booksUpdateQuery);

			// Insert order into the order table
			String ordersInsertQuery = "insert into";
			Database.insert(ordersInsertQuery);
		}
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
	private int[] availablilityCheck(String[] isbns, String[] quantities)
			throws InvalidAttributeValueException, ClassNotFoundException,
			SQLException {
		if (isbns.length != quantities.length) {
			throw new InvalidAttributeValueException();
		}

		int[] results = { 0, 0, 0 };

		for (int i = 0; i < isbns.length; i++) {
			ResultSet resultSet = Database.queryToResultSet("");
			ResultSetMetaData rsmd = resultSet.getMetaData();

			int colCount = rsmd.getColumnCount();

			// Check that the result from the database has sufficient quantity
			// for a particular book
			while (resultSet.next()) {
				for (int ii = 1; ii <= colCount; ii++) {
					if (rsmd.getColumnName(ii) == "copies_in_inventory") {
						int quantity = Integer.parseInt(resultSet.getObject(ii)
								.toString());
						results[i] = quantity - Integer.parseInt(quantities[i]);
					}
				}
			}
		}

		return results;
	}
}
