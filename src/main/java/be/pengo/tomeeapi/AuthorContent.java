package be.pengo.tomeeapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({ "author_id", "author_detail"})
public class AuthorContent {

    @JsonProperty("author_id")
    private long id;

    @JsonProperty("author_detail")
    private Map<String, Object> authorDetail;

    public AuthorContent() {
    }

    public AuthorContent(Long authorId, Map<String, Object> authorDetail) {
        this.id = authorId;
        this.authorDetail = authorDetail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> getAuthorDetail() {
        return authorDetail;
    }

    public void setAuthorDetail(Map<String, Object> authorDetail) {
        this.authorDetail = authorDetail;
    }
}
