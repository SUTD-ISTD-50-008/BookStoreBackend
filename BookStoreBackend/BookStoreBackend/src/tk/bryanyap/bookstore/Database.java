package tk.bryanyap.bookstore;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Database class. Used to handle database operations such as inserting,
 * updating and selecting records.
 * 
 * @author Bryan Yap
 *
 */
public class Database {
	private static String password = "password";
	private static String userid = "api_user";
	private static String ipAddress = "localhost";
	private static int port = 3306;
	private static String databaseName = "bookstore";

	/**
	 * Used to run a query and output to an XML String
	 * 
	 * @param query
	 * @return xmlString
	 */
	public static String queryToXML(String query) {
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			// Load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://" + ipAddress
					+ ":" + port + "/" + databaseName, userid, password);

			// Statements the issue of SQL queries to the database
			statement = connect.createStatement();

			// resultSet gets the result of the SQL query
			resultSet = statement.executeQuery(query);
			ResultSetMetaData rsmd = resultSet.getMetaData();

			// Create XML Document object
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element results = doc.createElement("results");
			doc.appendChild(results);

			int colCount = rsmd.getColumnCount();

			// Add the data from the ResultSet into the XML Document object
			while (resultSet.next()) {
				Element row = doc.createElement("row");
				results.appendChild(row);
				for (int ii = 1; ii <= colCount; ii++) {
					String columnName = rsmd.getColumnName(ii);
					String value = "";

					// Check if a record in the the resultSet is null on a
					// particular attribute.
					// If it is, set it to an empty String.
					if (resultSet.getString(ii) == null) {
						value = "";
					} else {
						value = resultSet.getString(ii);
					}

					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode(value));
					row.appendChild(node);

				}
			}

			// Serialize DOM
			OutputFormat format = new OutputFormat(doc);

			// as a String
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			serial.serialize(doc);

			// Close everything when done
			resultSet.close();
			statement.close();
			connect.close();

			// Display the XML
			if (stringOut.equals(null) || stringOut.equals("")) {
				return "<results>No results</results>";
			} else {
				return stringOut.toString();
			}

		} catch (SQLException e) {
			return error(e);
		} catch (ParserConfigurationException e) {
			return error(e);
		} catch (IOException e) {
			return error(e);
		} catch (ClassNotFoundException e) {
			return error(e);
		}
	}

	/**
	 * Used to query an entire Table and output to an XML String
	 * 
	 * @param table
	 * @return xmlString
	 */
	public static String getTableToXML(String table) {
		return queryToXML("select * from " + table + ";");
	}

	/**
	 * Used to run a query and output to a ResultSet object
	 * 
	 * @param query
	 * @return resultSet
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static ResultSet queryToResultSet(String query) throws SQLException,
			ClassNotFoundException {
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;

		// Load the MySQL driver, each DB has its own driver

		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":"
				+ port + "/" + databaseName, userid, password);

		// Statements the issue of SQL queries to the database
		statement = connect.createStatement();

		// resultSet gets the result of the SQL query
		resultSet = statement.executeQuery(query);

		return resultSet;

	}

	/**
	 * Used to get the first result of a query.
	 * 
	 * @param query
	 * @return firstResultString
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static String queryFirstResult(String query) throws SQLException,
			ClassNotFoundException {
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String result = "";

		// Load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":"
				+ port + "/" + databaseName, userid, password);

		// Statements the issue of SQL queries to the database
		statement = connect.createStatement();

		// resultSet gets the result of the SQL query
		resultSet = statement.executeQuery(query);

		// Get the first result in the ResultSet
		if (resultSet.next()) {
			result = resultSet.getString(1);
		}
		return result;
	}

	/**
	 * Used to run an update into the database
	 * 
	 * @param query
	 * @return successXMLString
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static String update(String query) throws ClassNotFoundException,
			SQLException {
		Connection connect = null;
		Statement statement = null;

		// Load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":"
				+ port + "/" + databaseName, userid, password);

		// Statements the issue of SQL queries to the database
		statement = connect.createStatement();

		// Execute the query
		int numberOfRows = statement.executeUpdate(query);

		if (numberOfRows == 1) {
			return success();
		} else {
			return error("You updated " + numberOfRows
					+ " rows. You should only update 1 row.");
		}

	}

	/**
	 * Used to run an insert query into the database
	 * 
	 * @param query
	 * @return successXMLString
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static String insert(String query) throws SQLException,
			ClassNotFoundException {
		Connection connect = null;
		Statement statement = null;

		// Load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":"
				+ port + "/" + databaseName, userid, password);

		// Statements the issue of SQL queries to the database
		statement = connect.createStatement();

		// Execute the query
		int numberOfRows = statement.executeUpdate(query);

		if (numberOfRows == 1) {
			return success();
		} else {
			return error("You inserted " + numberOfRows
					+ " rows. You should only insert 1 row.");
		}
	}

	/**
	 * Get the current server time and place within <success> tags to symbolize
	 * success on an operation.
	 * 
	 * @return successXMLString
	 */
	public static String success() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return "<success>" + dateFormat.format(cal.getTime()) + "</success>";
	}

	/**
	 * Takes in a String representing the Exception and converts it to XML
	 * format with <Error> and </Error> tags
	 * 
	 * @param error
	 * @return errorXMLString
	 */
	public static String error(String error) {
		return "<Error>" + error + "</Error>";
	}

	/**
	 * Takes in an Exception and converts it to XML format with <Error> and
	 * </Error> tags
	 * 
	 * @param e
	 * @return errorXMLString
	 */
	public static String error(Exception e) {
		return error(e.getMessage());
	}

	/**
	 * Takes in an Exception and description as a String and concatenates both
	 * to form an XML String with <Error> and </Error> tags
	 * 
	 * @param e
	 * @param description
	 * @return errorXMLString
	 */
	public static String error(Exception e, String description) {
		return error(e.getMessage() + "," + description);
	}
}
