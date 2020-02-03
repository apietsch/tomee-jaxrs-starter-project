package be.pengo.tomeeapi;

import org.apache.johnzon.mapper.JohnzonAny;
import org.apache.johnzon.mapper.JohnzonIgnore;

import java.util.Map;
import java.util.TreeMap;

public class AnyUserPost {
    private Long userId;
    private Long id;
    private String title;
    private String body;

    @JohnzonIgnore
    private Map<String, Object> unknownFields = new TreeMap<>();

    @JohnzonAny
    public Map<String, Object> getAny() {
        return unknownFields;
    }

    @JohnzonAny
    public void handle(final String key, final Object val) {
        this.unknownFields.put(key, val);
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "AnyUserPost{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", unknownFields=" + unknownFields +
                '}';
    }
}
