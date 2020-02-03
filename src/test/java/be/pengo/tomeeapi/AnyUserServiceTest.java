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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * These test run against a arquillian-tomee-embedded. The test are about calling
 * two rest endpoints. Each endpoint calls two external rest services.  One in synchronous
 * (blocking) order. The other endpoint uses CompletableFuture to call the external resource in an
 * asynchronous way (delegating the work into the ForkJoinPool.commonPool()).
 * By intentionally extending the time to finish the external calls, one can observe that
 * the asynchronous tests {@link AnyUserServiceTest#testAsyncAnyUserService()} will finish in
 * more or less half of the time than {@link AnyUserServiceTest#testAnyUserService()}
 */
@RunWith(Arquillian.class)
public class AnyUserServiceTest extends Assert {

    @ArquillianResource
    private URL webappUrl;

    @Test
    @InSequence(1)
    public void testAnyUserService() throws Exception {
        AnyUser anyUser = getResponse("api/async/anyuser/1", AnyUser.class);
        assertNotNull(anyUser);
        assertEquals(10, anyUser.getPostList().size());
        assertEquals(Long.valueOf(1), anyUser.getId());
        assertEquals("Leanne Graham", anyUser.getName());
    }

    @Test
    @InSequence(2)
    public void testAsyncAnyUserService() throws Exception {
        AnyUser anyUser = getResponse("api/async/asyncanyuser/1", AnyUser.class);
        assertNotNull(anyUser);
        assertEquals(10, anyUser.getPostList().size());
        assertEquals(Long.valueOf(1), anyUser.getId());
        assertEquals("Leanne Graham", anyUser.getName());
    }

    @Test
    @InSequence(3)
    public void testAnyUserJsonResponse() throws URISyntaxException {
        AnyUser anyUser = getResponse("api/async/asyncanyuser/1", AnyUser.class);
        assertNotNull(anyUser);
        assertEquals(10, anyUser.getPostList().size());
        assertEquals(Long.valueOf(1), anyUser.getId());
        assertEquals("Leanne Graham", anyUser.getName());
    }

    @Test
    @InSequence(4)
    public void testUnknownAnyUserJsonResponse() throws URISyntaxException {
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        Response response = webTarget.path("api/async/asyncanyuser/300").
                request().
                accept(MediaType.APPLICATION_JSON).
                get();
        assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    @InSequence(5)
    public void testAnyUserAttributeOrderFromJSONResponse() throws URISyntaxException {
        Response response = getResponse("api/async/asyncanyuser/1");
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

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addAsManifestResource("log4j.properties", "log4j.properties")
                .addPackages(true, new String[]{"be.pengo.tomeeapi"})
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(webArchive.toString(true));
        return webArchive;
    }
}
