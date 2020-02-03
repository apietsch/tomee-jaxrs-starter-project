package be.pengo.tomeeapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceClient;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Path("/demo")
@Stateless
public class MyLongTakingService {
    
    @GET
    public Response iAmTakingVeryLong() throws JsonProcessingException {
        String baseUrl = "http://jsonplaceholder.typicode.com";
        String path = "users/1";
        queryRestService(baseUrl, path);
//        System.out.println("Start rs method: " + Thread.currentThread().getName());
//        getResultOverNetwork();
//        Instant start = Instant.now();
//        doCompletableFutureStuff();
//        Instant stop = Instant.now();
//        long runtime = Duration.between(start, stop).toMillis();
//        System.out.println("doStuff took " + runtime / 1000 + " seconds to complete");
//        System.out.println("Processing is over, return response.");
        return Response.ok().entity("iAmTakingVeryLong").build();
    }
    
    private JSONObject queryRestService(String baseUrl, String path) throws JsonProcessingException {
        
        WebTarget target = ClientBuilder.newClient().target(baseUrl);
        Response response = target.path(path).request().accept(MediaType.APPLICATION_JSON).get();
        
        String entity = response.readEntity(String.class);
        System.out.println("response:");
        System.out.println(entity);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(entity, Map.class);
        System.out.println("As Map:");
        System.out.println(map);

        Map<String, Object> jsonMap = mapper.readValue(entity,
                new TypeReference<Map<String,Object>>(){});
        System.out.println("jsonMap:");
        System.out.println(jsonMap);
        // convert map to JSON string
        String json = mapper.writeValueAsString(map);
        System.out.println("Map to JSON: ");
        System.out.println(json);   // compact-print
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        System.out.println("Pretty printed:");
        System.out.println(json);   // pretty-print

        JSONObject jsonObject =new JSONObject(entity);
        System.out.println("jsonObject:");
        System.out.println(jsonObject);
        
        return jsonObject; 
        
    }

    private void doCompletableFutureStuff() {
        CompletableFuture<Void> future = supplyAsync(() -> getFruits()).
                thenCombine(supplyAsync(() -> getVeggies()),
                        (fruits, veggies) -> {
                            System.out.println("doCompletableFutureStuff combine Thread is: " + Thread.currentThread().getName());
                            Stream<String> concat = Stream.concat(Arrays.stream(fruits), Arrays.stream(veggies));
                            return concat;
                        }
                ).thenAcceptAsync(items -> {
                    System.out.println("thenAcceptAsync Thread is: " + Thread.currentThread().getName());
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

    private void delay(int i, String name) {
        try {
            System.out.println(name + " Latency simulation (Thread goes to sleep .. " + Thread.currentThread().getName());
            Thread.sleep(i);
            System.out.println(name + " Latency simulation ends (Thread ends working up ..) " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String[] getFruits() {
        Instant start = Instant.now();
        delay(30000, "getFruits"); //simulate network latency

        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("getFruits took " + runtime / 1000 + " seconds to complete");
        return new String[]{"apple", "apricot", "banana"};
    }

    public String[] getVeggies() {
        Instant start = Instant.now();
        delay(10000, "getVeggies"); //simulate network latency
        Instant stop = Instant.now();
        long runtime = Duration.between(start, stop).toMillis();
        System.out.println("getVeggies took " + runtime / 1000 + " seconds to complete");

        return new String[]{"broccoli", "brussels sprout"};
    }
}
