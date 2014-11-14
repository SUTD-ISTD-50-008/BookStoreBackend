package tk.bryanyap.myfirstproject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/books")
public class Books {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getBooks() {
		return Database.getTable("books");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public String getBooks(String input) {
		return input;
	}

}
