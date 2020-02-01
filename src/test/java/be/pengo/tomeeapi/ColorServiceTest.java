/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package be.pengo.tomeeapi;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Arquillian will start the container, deploy all @Deployment bundles, then run all the @Test methods.
 *
 * A strong value-add for Arquillian is that the test is abstracted from the server.
 * It is possible to rerun the same test against multiple adapters or server configurations.
 *
 * A second value-add is it is possible to build WebArchives that are slim and trim and therefore
 * isolate the functionality being tested.  This also makes it easier to swap out one implementation
 * of a class for another allowing for easy mocking.
 *
 */
@RunWith(Arquillian.class)
public class ColorServiceTest extends Assert {

    @PersistenceContext(name = "myPU")
    protected EntityManager entityManager;
    
    /**
     * ShrinkWrap is used to create a war file on the fly.
     *
     * The API is quite expressive and can build any possible
     * flavor of war file.  It can quite easily return a rebuilt
     * war file as well.
     *
     * More than one @Deployment method is allowed.
     */
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addPackages(true, new String[]{"be.pengo.tomeeapi"})
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * This URL will contain the following URL data
     *
     *  - http://<host>:<port>/<webapp>/
     *
     * This allows the test itself to be agnostic of server information or even
     * the name of the webapp
     *
     */
    @ArquillianResource
    private URL webappUrl;

    @Test
    public void testAsynService() throws Exception {
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        Instant start = Instant.now();
        Response response = webTarget.path("api/async/long_taking_service").request().get();
        Instant stop = Instant.now();
        long duration = Duration.between(start, stop).toMillis();
        assertTrue(duration > 25000);
        String readEntity = response.readEntity(String.class);
        assertThat(readEntity, CoreMatchers.containsString("end long processing in"));
    }

    @Test
    public void testAuthorContentJsonResponse() throws URISyntaxException {
        AuthorContent authorContent = getResponse("api/async/content/1", AuthorContent.class);
        assertNotNull(authorContent);
    }

    @Test
    public void testAuthorContentAttributeOrderFromJSONResponse() throws URISyntaxException {
        Response response = getResponse("api/async/content/1");
        String readEntity = response.readEntity(String.class);
        assertEquals("{\"author_id\":1,\"author_detail\":{\"id\":1,\"name\":\"Leanne Graham\",\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},\"author_posts\":[{\"userId\":1,\"id\":1,\"title\":\"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\"body\":\"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"},{\"userId\":1,\"id\":2,\"title\":\"qui est esse\",\"body\":\"est rerum tempore vitae\\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\\nqui aperiam non debitis possimus qui neque nisi nulla\"},{\"userId\":1,\"id\":3,\"title\":\"ea molestias quasi exercitationem repellat qui ipsa sit aut\",\"body\":\"et iusto sed quo iure\\nvoluptatem occaecati omnis eligendi aut ad\\nvoluptatem doloribus vel accusantium quis pariatur\\nmolestiae porro eius odio et labore et velit aut\"},{\"userId\":1,\"id\":4,\"title\":\"eum et est occaecati\",\"body\":\"ullam et saepe reiciendis voluptatem adipisci\\nsit amet autem assumenda provident rerum culpa\\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\\nquis sunt voluptatem rerum illo velit\"},{\"userId\":1,\"id\":5,\"title\":\"nesciunt quas odio\",\"body\":\"repudiandae veniam quaerat sunt sed\\nalias aut fugiat sit autem sed est\\nvoluptatem omnis possimus esse voluptatibus quis\\nest aut tenetur dolor neque\"},{\"userId\":1,\"id\":6,\"title\":\"dolorem eum magni eos aperiam quia\",\"body\":\"ut aspernatur corporis harum nihil quis provident sequi\\nmollitia nobis aliquid molestiae\\nperspiciatis et ea nemo ab reprehenderit accusantium quas\\nvoluptate dolores velit et doloremque molestiae\"},{\"userId\":1,\"id\":7,\"title\":\"magnam facilis autem\",\"body\":\"dolore placeat quibusdam ea quo vitae\\nmagni quis enim qui quis quo nemo aut saepe\\nquidem repellat excepturi ut quia\\nsunt ut sequi eos ea sed quas\"},{\"userId\":1,\"id\":8,\"title\":\"dolorem dolore est ipsam\",\"body\":\"dignissimos aperiam dolorem qui eum\\nfacilis quibusdam animi sint suscipit qui sint possimus cum\\nquaerat magni maiores excepturi\\nipsam ut commodi dolor voluptatum modi aut vitae\"},{\"userId\":1,\"id\":9,\"title\":\"nesciunt iure omnis dolorem tempora et accusantium\",\"body\":\"consectetur animi nesciunt iure dolore\\nenim quia ad\\nveniam autem ut quam aut nobis\\net est aut quod aut provident voluptas autem voluptas\"},{\"userId\":1,\"id\":10,\"title\":\"optio molestias id quia eum\",\"body\":\"quo et expedita modi cum officia vel magni\\ndoloribus qui repudiandae\\nvero nisi sit\\nquos veniam quod sed accusamus veritatis error\"}]}", readEntity);
    }

    @Test
    public void postAndGet() throws Exception {

        // POST
        {
            final WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
            System.out.println("-------> " + webTarget.getUri().toString());
            final Response response = webTarget.path("api/color/green").request().post(null);

            assertEquals(204, response.getStatus());
        }

        // GET
        {
            final WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
            final Response response = webTarget.path("api/color").request().get();

            assertEquals(200, response.getStatus());

            final String content = response.readEntity(String.class);

            assertEquals("green", content);
        }
    }

    @Test
    public void getColorObject() throws Exception {

        final WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());

        final Color color = webTarget.path("api/color/object").request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Color.class);

        assertNotNull(color);
        assertEquals("orange", color.getName());
        assertEquals(0xE7, color.getR());
        assertEquals(0x71, color.getG());
        assertEquals(0x00, color.getB());
    }

    private <T> T getResponse(String path, Class<T> dtoClazz) throws URISyntaxException {
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        return webTarget.path(path).
                request().
                accept(MediaType.APPLICATION_JSON).
                get(dtoClazz);
    }

    private Response getResponse(String path) throws URISyntaxException {
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        return webTarget.path(path).
                request().
                accept(MediaType.APPLICATION_JSON).
                get();
    }

}
