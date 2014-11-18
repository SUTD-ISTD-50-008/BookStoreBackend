package tk.bryanyap.bookstore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/customers")
public class Customers {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getCustomers() {
		return Database.getTableToXML("customers");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public String getCustomers(String input) {
		return input;
	}
}
