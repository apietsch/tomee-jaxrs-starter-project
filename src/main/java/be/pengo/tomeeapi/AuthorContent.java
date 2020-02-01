package be.pengo.tomeeapi;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.List;
import java.util.Map;

@JsonbPropertyOrder({ "author_id", "author_detail", "author_posts"})
public class AuthorContent {

    @JsonbProperty("author_id")
    private long id;

    @JsonbProperty("author_detail")
    private Map<String, Object> authorDetail;

    @JsonbProperty("author_posts")
    private List<Map<String, Object>> authorPosts;


    public AuthorContent() {
    }

    public AuthorContent(Long authorId, Map<String, Object> authorDetail, List<Map<String, Object>> authorPosts) {
        this.id = authorId;
        this.authorDetail = authorDetail;
        this.authorPosts = authorPosts;
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

    public List<Map<String, Object>> getAuthorPosts() {
        return authorPosts;
    }

    public void setAuthorPosts(List<Map<String, Object>> authorPosts) {
        this.authorPosts = authorPosts;
    }
}
