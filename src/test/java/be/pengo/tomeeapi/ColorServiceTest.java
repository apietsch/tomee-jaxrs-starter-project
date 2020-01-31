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
    public void testDemo() throws Exception {
        // doing the get
        WebTarget webTarget = ClientBuilder.newClient().target(webappUrl.toURI());
        Instant start = Instant.now();
        Response response = webTarget.path("api/demo").request().get();
        Instant stop = Instant.now();
        long duration = Duration.between(start, stop).toMillis();
        System.out.println("the rest call took " + duration/1000 + " seconds in total to receive a response" );
        assertEquals("iAmTakingVeryLong", response.readEntity(String.class));
    }


    @Test
    public void convertMapToJson() throws IOException {
        Map<String, String> elements = new HashMap();
        elements.put("Key1", "Value1");
        elements.put("Key2", "Value2");
        elements.put("Key3", "Value3");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(elements);
            System.out.println("json = " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
}
