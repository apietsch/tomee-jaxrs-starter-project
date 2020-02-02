package be.pengo.tomeeapi;

import org.apache.johnzon.jsonb.JohnzonBuilder;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AnyUserJsonMappingTest {

    @Test
    public void parseAnyUserWithJohnzonMapper() {
        String input = "{\n" +
                "  \"id\": 2,\n" +
                "  \"name\": \"Ervin Howell\",\n" +
                "  \"username\": \"Antonette\",\n" +
                "  \"email\": \"Shanna@melissa.tv\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"Victor Plains\",\n" +
                "    \"suite\": \"Suite 879\",\n" +
                "    \"city\": \"Wisokyburgh\",\n" +
                "    \"zipcode\": \"90566-7771\",\n" +
                "    \"geo\": {\n" +
                "      \"lat\": \"-43.9509\",\n" +
                "      \"lng\": \"-34.4618\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"phone\": \"010-692-6593 x09125\",\n" +
                "  \"website\": \"anastasia.net\",\n" +
                "  \"company\": {\n" +
                "    \"name\": \"Deckow-Crist\",\n" +
                "    \"catchPhrase\": \"Proactive didactic contingency\",\n" +
                "    \"bs\": \"synergize scalable supply-chains\"\n" +
                "  }\n" +
                "}";


        final Mapper mapper = new MapperBuilder().build();
        final AnyUser anyUser = mapper.readObject(input, AnyUser.class);
        assertEquals("Ervin Howell", anyUser.getName());
        System.out.println(anyUser);
    }

    @Test
    public void parseAnyUserPostWithJohnzonMapper() {
        String input = "[\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 1,\n" +
                "    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n" +
                "    \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 2,\n" +
                "    \"title\": \"qui est esse\",\n" +
                "    \"body\": \"est rerum tempore vitae\\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\\nqui aperiam non debitis possimus qui neque nisi nulla\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 3,\n" +
                "    \"title\": \"ea molestias quasi exercitationem repellat qui ipsa sit aut\",\n" +
                "    \"body\": \"et iusto sed quo iure\\nvoluptatem occaecati omnis eligendi aut ad\\nvoluptatem doloribus vel accusantium quis pariatur\\nmolestiae porro eius odio et labore et velit aut\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 4,\n" +
                "    \"title\": \"eum et est occaecati\",\n" +
                "    \"body\": \"ullam et saepe reiciendis voluptatem adipisci\\nsit amet autem assumenda provident rerum culpa\\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\\nquis sunt voluptatem rerum illo velit\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 5,\n" +
                "    \"title\": \"nesciunt quas odio\",\n" +
                "    \"body\": \"repudiandae veniam quaerat sunt sed\\nalias aut fugiat sit autem sed est\\nvoluptatem omnis possimus esse voluptatibus quis\\nest aut tenetur dolor neque\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 6,\n" +
                "    \"title\": \"dolorem eum magni eos aperiam quia\",\n" +
                "    \"body\": \"ut aspernatur corporis harum nihil quis provident sequi\\nmollitia nobis aliquid molestiae\\nperspiciatis et ea nemo ab reprehenderit accusantium quas\\nvoluptate dolores velit et doloremque molestiae\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 7,\n" +
                "    \"title\": \"magnam facilis autem\",\n" +
                "    \"body\": \"dolore placeat quibusdam ea quo vitae\\nmagni quis enim qui quis quo nemo aut saepe\\nquidem repellat excepturi ut quia\\nsunt ut sequi eos ea sed quas\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 8,\n" +
                "    \"title\": \"dolorem dolore est ipsam\",\n" +
                "    \"body\": \"dignissimos aperiam dolorem qui eum\\nfacilis quibusdam animi sint suscipit qui sint possimus cum\\nquaerat magni maiores excepturi\\nipsam ut commodi dolor voluptatum modi aut vitae\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 9,\n" +
                "    \"title\": \"nesciunt iure omnis dolorem tempora et accusantium\",\n" +
                "    \"body\": \"consectetur animi nesciunt iure dolore\\nenim quia ad\\nveniam autem ut quam aut nobis\\net est aut quod aut provident voluptas autem voluptas\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"id\": 10,\n" +
                "    \"title\": \"optio molestias id quia eum\",\n" +
                "    \"body\": \"quo et expedita modi cum officia vel magni\\ndoloribus qui repudiandae\\nvero nisi sit\\nquos veniam quod sed accusamus veritatis error\"\n" +
                "  }\n" +
                "]";

        Jsonb jsonb = new JohnzonBuilder().build();
        List<AnyUserPost> personList = jsonb.fromJson(input, new ArrayList<AnyUserPost>(){}.getClass().getGenericSuperclass());

        assertFalse(personList.isEmpty());
        assertEquals(10, personList.size());
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", personList.get(0).getTitle());
    }

    @Test
    public void serializeAnyUserWithItsPosts() {
        AnyUser anyUser = new AnyUser();
        anyUser.setId(1L);
        anyUser.setName("anyname");
        anyUser.setUsername("anyusername");
        AnyUserPost anyUserPost = new AnyUserPost();
        anyUserPost.setUserId(anyUser.getId());
        anyUserPost.setId(1L);
        anyUserPost.setTitle("anyposttitle");
        ArrayList<AnyUserPost> anyUserPosts = new ArrayList<>();
        anyUserPosts.add(anyUserPost);
        anyUser.setPostList(anyUserPosts);

        JsonbConfig jsonbConfig = new JsonbConfig()
                .withNullValues(false)
                .withFormatting(false);

        Jsonb jsonb = JsonbBuilder.create(jsonbConfig);
        String jsonAnyUser = jsonb.toJson(anyUser);
        assertEquals("{\"id\":1,\"name\":\"anyname\",\"postList\":[{\"id\":1,\"title\":\"anyposttitle\",\"userId\":1}],\"username\":\"anyusername\"}", jsonAnyUser);
    }
}
