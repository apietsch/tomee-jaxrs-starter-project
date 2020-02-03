package be.pengo.tomeeapi;

import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.apache.johnzon.jsonb.JohnzonBuilder;


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
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

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
        System.out.println(String.format("Service method getAnyUser() lives in Thread %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        AnyUser anyUser = queryAnyUser(userId);
        if (anyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<AnyUserPost> anyUserPosts = queryAnyUsersPosts(userId);
        if (anyUserPosts != null) {
            anyUser.setPostList(anyUserPosts);
        }

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Service Method getAnyUser() took " + runtime + " milliseconds to complete");

        return Response.ok().entity(anyUser).build();
    }

    @GET
    @Path("asyncanyuser/{userid}")
    @Produces({APPLICATION_JSON})
    public Response getAnyUserAsync(@PathParam("userid") Long userId) {
        System.out.println(String.format("Service method getAnyUserAsync() lives in Thread %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        AnyUser responseAnyUser = null;
        try {
            CompletableFuture<AnyUser> anyUserFuture = supplyAsync(() -> queryAnyUser(userId)).
                    exceptionally(e -> {
                        System.out.println("An exception happened in queryAnyUser: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }).
                    thenCombine(supplyAsync(() -> queryAnyUsersPosts(userId)).
                                    exceptionally(e -> {
                                        System.out.println("An exception happened in queryAnyUsersPosts: " + e.getMessage());
                                        e.printStackTrace();
                                        return null;
                                    }),
                            (anyUser, anyUserPosts) -> {
                                anyUser.setPostList(anyUserPosts);
                                return anyUser;
                            }).exceptionally(e -> {
                System.out.println("An exception happened in combine: " + e.getMessage());
                e.printStackTrace();
                return null;
            });
            responseAnyUser = anyUserFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Something went wrong for anyUserFuture CompletableFuture: " + e.getMessage());
            e.printStackTrace();
        }

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Service Method getAnyUserAsync() took " + runtime / 1000 + " seconds to complete");

        if (responseAnyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(responseAnyUser).build();
    }

    private AnyUser queryAnyUser(Long authorId) {
        System.out.println(String.format("Method queryAnyUser() lives in Thread %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        threadSleepForSeconds(3);
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Response response = target.path("users").
                path(authorId.toString()).
                request().
                accept(MediaType.APPLICATION_JSON).get();

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method queryAnyUser() took " + runtime / 1000 + " seconds to complete");

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            Mapper mapper = new MapperBuilder().build();
            return mapper.readObject(asString, AnyUser.class);
        }

        return null;
    }

    private List<AnyUserPost> queryAnyUsersPosts(Long authorId) {
        System.out.println(String.format("Method queryAnyUsersPosts() lives in Thread %s", Thread.currentThread().getName()));
        Instant start = Instant.now();
        threadSleepForSeconds(3);
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Response response = target.path("posts").
                queryParam("userId", authorId).
                request().accept(MediaType.APPLICATION_JSON).get();

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method queryAnyUsersPosts() took " + runtime / 1000 + " seconds to complete");

        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            Jsonb jsonb = new JohnzonBuilder().build();
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
