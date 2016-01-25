package neostarter.sample;

import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api(tags = "/hello")
@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Endpoint {

    @GET
    @Path("{name}")
    public String hello(@PathParam("name") String name) {

        throw new IllegalArgumentException("abc");
    }
}
