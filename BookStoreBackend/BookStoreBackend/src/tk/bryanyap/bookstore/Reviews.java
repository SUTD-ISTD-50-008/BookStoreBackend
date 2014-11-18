package tk.bryanyap.bookstore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/reviews")
public class Reviews {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getReviews() {
		return Database.getTableToXML("reviews");
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public String getReviews(String input) {
		return input;
	}
}
