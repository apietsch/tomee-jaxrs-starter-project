package be.pengo.tomeeapi;

import org.apache.johnzon.mapper.JohnzonAny;
import org.apache.johnzon.mapper.JohnzonIgnore;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AnyUser {
    private Long id;
    private String name; // Regular serialization for the known 'name' field
    private String username;
    private List<AnyUserPost> postList = Collections.emptyList();

    @JohnzonIgnore
    private Map<String, Object> unknownFields = new TreeMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @JohnzonAny
    public Map<String, Object> getAny() {
        return unknownFields;
    }

    @JohnzonAny
    public void handle(final String key, final Object val) {
        this.unknownFields.put(key, val);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AnyUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", postList=" + postList +
                ", unknownFields=" + unknownFields +
                '}';
    }

    public List<AnyUserPost> getPostList() {
        return postList;
    }

    public void setPostList(List<AnyUserPost> postList) {
        this.postList = postList;
    }
}