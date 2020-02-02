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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/async")
@Stateless
public class AnyUserService {

    private static final String HTTP_JSONPLACEHOLDER_TYPICODE_COM = "http://jsonplaceholder.typicode.com";

    @GET
    @Path("long_taking_service")
    @Produces({APPLICATION_JSON})
    public Response getLongTaskResult() {
        System.out.println("Handle rest request in Thread: " + Thread.currentThread().getName());
        getResultOverNetwork();
        Instant start = Instant.now();
        doCompletableFutureStuff();
        Instant stop = Instant.now();
        long duration = Duration.between(start, stop).toMillis();
        return Response.ok().entity("end long processing in " + duration / 1000 + " seconds.").build();
    }

    @GET
    @Path("content/{AUTHOR_ID}")
    @Produces({APPLICATION_JSON})
    public Response getAnyUser(@PathParam("AUTHOR_ID") Long authorId){
        AnyUser anyUser = queryAnyUser(authorId);
        if (anyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<AnyUserPost> anyUserPosts = queryAnyUsersPosts(authorId);
        if (anyUserPosts != null) {
            anyUser.setPostList(anyUserPosts);
        }

        if (anyUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(anyUser).build();
    }

    private AnyUser queryAnyUser(Long authorId){
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Response response = target.path("users").path(authorId.toString()).request().accept(MediaType.APPLICATION_JSON).get();

        if(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            Mapper mapper = new MapperBuilder().build();
            AnyUser anyUser = mapper.readObject(asString, AnyUser.class);
            return anyUser;
        }
        return null;
    }

    private List<AnyUserPost> queryAnyUsersPosts(Long authorId){
        WebTarget target = ClientBuilder.newClient().target(HTTP_JSONPLACEHOLDER_TYPICODE_COM);
        Response response = target.path("posts").
                queryParam("userId", authorId).
                request().accept(MediaType.APPLICATION_JSON).get();
        if(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            String asString = response.readEntity(String.class);
            Jsonb jsonb = new JohnzonBuilder().build();
            List<AnyUserPost> personList = jsonb.fromJson(asString, new ArrayList<AnyUserPost>(){}.getClass().getGenericSuperclass());
            return personList;
        }
        return null;
    }

    private void doCompletableFutureStuff() {
        CompletableFuture<Void> future = supplyAsync(() -> getFruits()).
                thenCombine(supplyAsync(() -> getVeggies()),
                        (fruits, veggies) -> {
                            System.out.println("Method doCompletableFutureStuff() combine Thread is: " + Thread.currentThread().getName());
                            Stream<String> concat = Stream.concat(Arrays.stream(fruits), Arrays.stream(veggies));
                            return concat;
                        }
                ).thenAcceptAsync(items -> {
                    System.out.println("Method thenAcceptAsync() Thread is: " + Thread.currentThread().getName());
                    items.forEach(System.out::println);
                }
        );
        future.join();
    }

    public Future<String> getResultOverNetwork() {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            delay(2000, "getResultOverNetwork"); //simulate network call
            future.complete("{\"result\":\"success\"}");
        }).start();
        return future;
    }

    private void delay(int sleepInMillis, String name) {
        try {
            System.out.println(String.format("Latency simulation for method %s (Thread %s goes to sleep for %s seconds..)", name, Thread.currentThread().getName(), sleepInMillis / 1000));
            Thread.sleep(sleepInMillis);
            System.out.println(String.format("Latency simulation for method %s ends (Thread  %s wakes up (end of work unit) ..) ", name , Thread.currentThread().getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String[] getFruits() {
        Instant start = Instant.now();
        delay(30000, "Method getFruits()"); //simulate network latency
        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method getFruits() took " + runtime / 1000 + " seconds to complete");
        return new String[]{"apple", "apricot", "banana"};
    }

    public String[] getVeggies() {
        Instant start = Instant.now();
        delay(10000, "Method getVeggies()"); //simulate network latency
        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("Method getVeggies() took " + runtime / 1000 + " seconds to complete");

        return new String[]{"broccoli", "brussels sprout"};
    }
}
