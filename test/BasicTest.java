import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }
    
    @Test
    public void createAndRetrieveUser() {
    	new User("tom@mail.com", "123456", "tom").save();
    	User tom = User.find("byEmail", "tom@mail.com").first();
    	assertNotNull(tom);
    	assertEquals("tom", tom.fullname);
    }

    @Test
    public void tryConnectAsUser() {
    	new User("tom@mail.com", "123456", "tom").save();
    	assertNotNull(User.connect("tom@mail.com", "123456"));
    	assertNull(User.connect("tom@mail.com", "wrongpassword"));
    	assertNull(User.connect("unknown_user", "123456"));
    }
    
    @Test
    public void createPost() {
    	User tom = new User("tom@mail.com", "123456", "tom").save();
    	new Post(tom, "first post", "hello world").save();
    	assertEquals(1, Post.count());
    	
    	List<Post> tomPosts = Post.find("byAuthor", tom).fetch();
    	assertEquals(1, tomPosts.size());
    	Post firstPost = tomPosts.get(0);
    	assertNotNull(firstPost);
    	assertEquals(tom, firstPost.author);
    	assertEquals("first post", firstPost.title);
    	assertEquals("hello world", firstPost.content);
    	assertNotNull(firstPost.postedAt);
    	
    	
    }
    
    @Test
    public void postComments() {
    	User tom = new User("tom@mail.com", "123456", "tom").save();
    	Post tomPost = new Post(tom, "first post", "hello world").save();
    	
    	new Comment(tomPost, "alice", "like it").save();
    	new Comment(tomPost, "bob", "no way"). save();
    	
    	List<Comment> tomPostComments = Comment.find("byPost", tomPost).fetch();
    	
    	assertEquals(2, tomPostComments.size());
    	
    	Comment firstComment = tomPostComments.get(0);
    	assertNotNull(firstComment);
    	assertEquals(tom, firstComment.post.author);
    	assertEquals("alice", firstComment.author);
    	assertEquals("like it", firstComment.content);
    	assertNotNull(firstComment.postedAt);
    	
    	Comment secondComment = tomPostComments.get(1);
    	assertNotNull(secondComment);
    	assertEquals(tom, secondComment.post.author);
    	assertEquals("bob", secondComment.author);
    	assertEquals("no way", secondComment.content);
    	assertNotNull(secondComment.postedAt);

    }
    
    @Test
    public void useTheCommentsRelation() {
    	User tom = new User("tom@mail.com", "123456", "tom").save();
    	Post tomPost = new Post(tom, "first post", "hello world").save();
    	
    	tomPost.addComment("alice", "like it");
    	tomPost.addComment("bob", "no way");
    	
    	assertEquals(1, User.count());
    	assertEquals(1, Post.count());
    	assertEquals(2, Comment.count());
    	
    	tomPost = Post.find("byAuthor", tom).first();
    	assertNotNull(tomPost);
    	assertEquals(2, tomPost.comments.size());
    	assertEquals("alice", tomPost.comments.get(0).author);
    	assertEquals("bob", tomPost.comments.get(1).author);
    	
    	tomPost.delete();
    	
    	assertEquals(1, User.count());
    	assertEquals(0, Post.count());
    	assertEquals(0, Comment.count());
    	
    }
    
    @Test
    public void fullTest() {
        Fixtures.load("data.yml");
     
        // Count things
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());
     
        // Try to connect as users
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("jeff@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
     
        // Find all of Bob's posts
        List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, bobPosts.size());
     
        // Find all comments related to Bob's posts
        List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, bobComments.size());
     
        // Find the most recent post
        Post frontPost = Post.find("order by postedAt desc").first();
        assertNotNull(frontPost);
        assertEquals("About the model layer", frontPost.title);
     
        // Check that this post has two comments
        assertEquals(2, frontPost.comments.size());
     
        // Post a new comment
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());
    }
}
