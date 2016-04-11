package conf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by atemnov on 11.04.2016.
 */
@Path("test")
public class TestResource {

    @GET
    public String hello() {
        return "Hello!";
    }
}
