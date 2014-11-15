package tk.bryanyap.bookstore;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Database {
	private static String password = "password";
	private static String userid = "root";
	private static String ipAddress = "localhost";
	private static int port = 3306;
	private static String databaseName = "bookstore";

	public static String query(String query) {
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
					Object value = resultSet.getObject(ii);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode(value.toString()));
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
			return stringOut.toString();

		} catch (Exception e) {
			return error(e.toString());
		}
	}

	public static String getTable(String table) {
		return query("select * from " + table + ";");
	}

	/**
	 * Takes in a String representing the Exception and converts it to XML
	 * format with <Error> and </Error> tags
	 * 
	 * @param error
	 * @return errorXMLString
	 */
	private static String error(String error) {
		return "<Error>" + error + "</Error>";
	}
}
