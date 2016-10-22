package bookmarks;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import bookmarks.model.Account;
import bookmarks.model.Bookmark;
import bookmarks.repository.AccountRepository;
import bookmarks.repository.BookmarkRepository;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BookmarksProjectRestServiceApplication.class)
@WebAppConfiguration
public class BookmarkRestControllerTest {
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private MockMvc mvc;
	private String userName = "user1";
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	private Account account;
	private List<Bookmark> bookmarklist = new ArrayList<>();
	@Autowired
	private BookmarkRepository bookmarkRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {
		List<HttpMessageConverter<?>> httpMessageConverterslist = Arrays.asList(converters);
		for (HttpMessageConverter<?> httpMessageConverter : httpMessageConverterslist) {
			if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
				this.mappingJackson2HttpMessageConverter = httpMessageConverter;
			}
		}
		Assert.assertNotNull("the Json message converter should not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		this.bookmarkRepository.deleteAllInBatch();
		this.accountRepository.deleteAllInBatch();

		this.account = accountRepository.save(new Account(userName, "password"));
		this.bookmarklist.add(
				bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + userName, "a description")));
		this.bookmarklist.add(
				bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + userName, "a description")));
	}

	@Test
	public void userNotFound() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/user2/bookmarks").contentType(contentType))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	// @Test
	public void readSingleBookmark() throws Exception {
		System.out.println(this.bookmarklist.get(0).getId());
		mvc.perform(MockMvcRequestBuilders.get("/" + userName + "/bookmarks/" + this.bookmarklist.get(0).getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(contentType))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(this.bookmarklist.get(0).getId())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.uri", Matchers.is("http://bookmark.com/1/" + userName)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("a description")));
	}

	// @Test
	public void readBookmarks() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/" + userName + "/bookmarks"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(contentType))
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(this.bookmarklist.get(0).getId())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].uri", Matchers.is("http://bookmark.com/1/" + userName)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("A description")))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(this.bookmarklist.get(1).getId())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].uri", Matchers.is("http://bookmark.com/2/" + userName)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is("A description")));
	}

	@Test
	public void create() throws Exception {
		String s = json(new Bookmark(this.account, "http://spring.io", "the website to get spring resources"));
		mvc.perform(MockMvcRequestBuilders.post("/" + userName + "/bookmarks/").contentType(contentType).content(s))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	protected String json(Object o) throws Exception {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
