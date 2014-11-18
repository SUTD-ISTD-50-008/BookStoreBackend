package tk.bryanyap.bookstore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
public class Status {
	String statusTable = "status_view";

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getStatus() {
		return Database.getTableToXML(statusTable);
	}

}
