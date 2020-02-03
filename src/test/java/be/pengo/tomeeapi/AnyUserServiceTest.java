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
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@RunWith(Arquillian.class)
public class AnyUserServiceTest extends Assert {

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
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addPackages(true, new String[]{"be.pengo.tomeeapi"})
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(webArchive.toString(true));
        return webArchive;
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
        AnyUser anyUser = getResponse("api/async/content/1", AnyUser.class);
        assertNotNull(anyUser);
        assertEquals(10, anyUser.getPostList().size());
        assertEquals(Long.valueOf(1), anyUser.getId());
        assertEquals("Leanne Graham", anyUser.getName());
    }

    @Test
    public void testUnknownAuthorContentJsonResponse() throws URISyntaxException {
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        Response response = webTarget.path("api/async/content/300").
                request().
                accept(MediaType.APPLICATION_JSON).
                get();
        assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testAuthorContentAttributeOrderFromJSONResponse() throws URISyntaxException {
        Response response = getResponse("api/async/content/1");
        String readEntity = response.readEntity(String.class);
        assertEquals("{\"id\":1,\"name\":\"Leanne Graham\",\"postList\":[{\"body\":\"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\",\"id\":1,\"title\":\"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\"userId\":1},{\"body\":\"est rerum tempore vitae\\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\\nqui aperiam non debitis possimus qui neque nisi nulla\",\"id\":2,\"title\":\"qui est esse\",\"userId\":1},{\"body\":\"et iusto sed quo iure\\nvoluptatem occaecati omnis eligendi aut ad\\nvoluptatem doloribus vel accusantium quis pariatur\\nmolestiae porro eius odio et labore et velit aut\",\"id\":3,\"title\":\"ea molestias quasi exercitationem repellat qui ipsa sit aut\",\"userId\":1},{\"body\":\"ullam et saepe reiciendis voluptatem adipisci\\nsit amet autem assumenda provident rerum culpa\\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\\nquis sunt voluptatem rerum illo velit\",\"id\":4,\"title\":\"eum et est occaecati\",\"userId\":1},{\"body\":\"repudiandae veniam quaerat sunt sed\\nalias aut fugiat sit autem sed est\\nvoluptatem omnis possimus esse voluptatibus quis\\nest aut tenetur dolor neque\",\"id\":5,\"title\":\"nesciunt quas odio\",\"userId\":1},{\"body\":\"ut aspernatur corporis harum nihil quis provident sequi\\nmollitia nobis aliquid molestiae\\nperspiciatis et ea nemo ab reprehenderit accusantium quas\\nvoluptate dolores velit et doloremque molestiae\",\"id\":6,\"title\":\"dolorem eum magni eos aperiam quia\",\"userId\":1},{\"body\":\"dolore placeat quibusdam ea quo vitae\\nmagni quis enim qui quis quo nemo aut saepe\\nquidem repellat excepturi ut quia\\nsunt ut sequi eos ea sed quas\",\"id\":7,\"title\":\"magnam facilis autem\",\"userId\":1},{\"body\":\"dignissimos aperiam dolorem qui eum\\nfacilis quibusdam animi sint suscipit qui sint possimus cum\\nquaerat magni maiores excepturi\\nipsam ut commodi dolor voluptatum modi aut vitae\",\"id\":8,\"title\":\"dolorem dolore est ipsam\",\"userId\":1},{\"body\":\"consectetur animi nesciunt iure dolore\\nenim quia ad\\nveniam autem ut quam aut nobis\\net est aut quod aut provident voluptas autem voluptas\",\"id\":9,\"title\":\"nesciunt iure omnis dolorem tempora et accusantium\",\"userId\":1},{\"body\":\"quo et expedita modi cum officia vel magni\\ndoloribus qui repudiandae\\nvero nisi sit\\nquos veniam quod sed accusamus veritatis error\",\"id\":10,\"title\":\"optio molestias id quia eum\",\"userId\":1}],\"username\":\"Bret\",\"address\":{\"zipcode\":\"92998-3874\",\"geo\":{\"lng\":\"81.1496\",\"lat\":\"-37.3159\"},\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"street\":\"Kulas Light\"},\"company\":{\"name\":\"Romaguera-Crona\",\"bs\":\"harness real-time e-markets\",\"catchPhrase\":\"Multi-layered client-server neural-net\"},\"email\":\"Sincere@april.biz\",\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\"}", readEntity);
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
