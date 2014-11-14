package tk.bryanyap.myfirstproject;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

@Path("/status")
public class Status {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getStatus() throws Exception {
		// Read the database
		Document doc = readDataBase();

		// Serialize DOM
		OutputFormat format = new OutputFormat(doc);

		// as a String
		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);
		serial.serialize(doc);

		// Display the XML
		return stringOut.toString();
	}

	public Document readDataBase() throws Exception {
		try {
			// Load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/test", "root", "password");

			// Statements the issue of SQL queries to the database
			statement = connect.createStatement();
			// resultSet gets the result of the SQL query
			resultSet = statement.executeQuery("select * from test_table");
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
			return doc;

		} catch (Exception e) {
			throw e;
		} finally {
			// Close everything when done
			resultSet.close();
			statement.close();
			connect.close();
		}

	}
}
