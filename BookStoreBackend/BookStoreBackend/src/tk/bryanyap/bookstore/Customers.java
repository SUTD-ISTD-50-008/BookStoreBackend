package tk.bryanyap.bookstore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Bryan Yap
 *
 */
@Path("/customers")
public class Customers {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getCustomers() {
		return Database.getTableToXML("customers");
	}
}