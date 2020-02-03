package be.pengo.tomeeapi;

import org.apache.johnzon.jsonb.JohnzonBuilder;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;

import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/async")
@Stateless
public class AnyUserService {

    private static final String HTTP_JSONPLACEHOLDER_TYPICODE_COM = "http://jsonplaceholder.typicode.com";

    @GET
    @Path("anyuser/{userid}")
    @Produces({APPLICATION_JSON})
    public Response getAnyUser(@PathParam("userid") Long userId) {
        System.out.println(String.format("Service method getAnyUser() lives in Thread: %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Mapper mapper = new MapperBuilder().build();
        Jsonb jsonb = new JohnzonBuilder().build();

        AnyUser anyUser = queryAnyUser(target, mapper, userId);
        if (anyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<AnyUserPost> anyUserPosts = queryAnyUsersPosts(target, jsonb, userId);
        if (anyUserPosts != null) {
            anyUser.setPostList(anyUserPosts);
        }

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Service Method getAnyUser() took " + runtime / 1000 + " seconds to complete.");

        return Response.ok().entity(anyUser).build();
    }

    @GET
    @Path("asyncanyuser/{userid}")
    @Produces({APPLICATION_JSON})
    public Response getAnyUserAsync(@PathParam("userid") Long userId) {
        System.out.println(String.format("Service method getAnyUserAsync() lives in Thread: %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        AnyUser responseAnyUser = null;
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Mapper mapper = new MapperBuilder().build();
        Jsonb jsonb = new JohnzonBuilder().build();

        try {
            CompletableFuture<AnyUser> anyUserFuture = supplyAsync(() -> queryAnyUser(target, mapper, userId)).
                    exceptionally(e -> {
                        System.out.println("An exception happened in queryAnyUser: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }).
                    thenCombine(supplyAsync(() -> queryAnyUsersPosts(target, jsonb, userId)).
                                    exceptionally(e -> {
                                        System.out.println("An exception happened in queryAnyUsersPosts: " + e.getMessage());
                                        e.printStackTrace();
                                        return null;
                                    }),
                            (anyUser, anyUserPosts) -> {
                                if (anyUser != null) {
                                    anyUser.setPostList(anyUserPosts);
                                }
                                return anyUser;
                            }).exceptionally(e -> {
                System.out.println("An exception happened in combine: " + e.getMessage());
                e.printStackTrace();
                return null;
            });
            System.out.println(".. after async processes.");
            responseAnyUser = anyUserFuture.get(10, TimeUnit.SECONDS);
            System.out.println(".. anyUserFuture.get() finished.");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Something went wrong for anyUserFuture CompletableFuture: ");
            e.printStackTrace();
        }

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Service Method getAnyUserAsync() took " + runtime / 1000 + " seconds to complete.");

        if (responseAnyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(responseAnyUser).build();
    }

    /**
     * Query external rest api (jsonplaceholder.typicode.com) and map response to {@link be.pengo.tomeeapi.AnyUser}
     * For educational reasons, this method has a call to a thread sleep method for an additional delay to finish the request.
     *
     * @param userId The user id to query the external api for
     * @return The AnyUser with some fields populated from the external service json or null if something went wrong
     * or if there was no external service user with that id.
     */
    private AnyUser queryAnyUser(WebTarget target, Mapper mapper, Long userId) {
        System.out.println(String.format("Method queryAnyUser() lives in Thread: %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        threadSleepForSeconds(3);

        Response response = target.path("users").
                path(userId.toString()).
                request().
                accept(MediaType.APPLICATION_JSON).get();

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method queryAnyUser() took " + runtime / 1000 + " seconds to complete.");

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            return mapper.readObject(asString, AnyUser.class);
        }

        return null;
    }

    /**
     * Query external rest api (jsonplaceholder.typicode.com) and map response to List of {@link be.pengo.tomeeapi.AnyUserPost}
     * For educational reasons, this method has a call to a thread sleep method for an additional delay to finish the request.
     *
     * @param userId The user id to query the external api for
     * @return A List of {@link be.pengo.tomeeapi.AnyUserPost} or null if there were no posts for the given userid
     */
    private List<AnyUserPost> queryAnyUsersPosts(WebTarget target, Jsonb jsonb, Long userId) {
        System.out.println(String.format("Method queryAnyUsersPosts() lives in Thread %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        threadSleepForSeconds(3);
        Response response = target.path("posts").
                queryParam("userId", userId).
                request().accept(MediaType.APPLICATION_JSON).get();

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method queryAnyUsersPosts() took " + runtime / 1000 + " seconds to complete");

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            return jsonb.fromJson(asString,
                    new ArrayList<AnyUserPost>() {
                    }.getClass().getGenericSuperclass());
        }
        return null;
    }

    private void threadSleepForSeconds(int seconds) {
        System.out.println(String.format("Intentional sleep for %s seconds in Thread: %s ", seconds, Thread.currentThread().getName()));
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
