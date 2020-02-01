package be.pengo.tomeeapi;

import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/async")
@Stateless
public class MyLongTakingService {
    
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
    public Response getAuthorContent (@PathParam("AUTHOR_ID") Long authorId){
        String baseUrl = "http://jsonplaceholder.typicode.com";
        String authorDetailPath = "users/" + authorId;
        Map<String, Object> authorDetails = queryRestService(baseUrl, authorDetailPath);
        String authorPostsPath = "posts";
        List<Map<String, Object>> authorsPosts = queryAuthorsPosts(baseUrl, authorPostsPath, authorId);
        return Response.ok().entity(new AuthorContent(authorId, authorDetails, authorsPosts)).build();
    }

    private Map<String, Object> queryRestService(String baseUrl, String path){
        WebTarget target = ClientBuilder.newClient().target(baseUrl);
        Response response = target.path(path).request().accept(MediaType.APPLICATION_JSON).get();
        String asString = response.readEntity(String.class);
        Jsonb JSONB = JsonbBuilder.create();
        Map<String, Object> map = (Map<String, Object>) JSONB.fromJson(asString, Object.class);
        return map;
    }

    private List<Map<String, Object>> queryAuthorsPosts(String baseUrl, String path, Long authorId){
        WebTarget target = ClientBuilder.newClient().target(baseUrl);
        Response response = target.path(path).
                queryParam("userId", authorId).
                request().accept(MediaType.APPLICATION_JSON).get();
        String asString = response.readEntity(String.class);
        Jsonb JSONB = JsonbBuilder.create();
        List<Map<String, Object>> listOfPosts = (List<Map<String, Object>>) JSONB.fromJson(asString, Object.class);
        return listOfPosts;
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
