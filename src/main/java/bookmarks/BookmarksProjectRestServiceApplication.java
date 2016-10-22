package bookmarks;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import bookmarks.model.Account;
import bookmarks.model.Bookmark;
import bookmarks.repository.AccountRepository;
import bookmarks.repository.BookmarkRepository;

@SpringBootApplication
public class BookmarksProjectRestServiceApplication {

	@Bean
	CommandLineRunner init(final AccountRepository accountRepository, final BookmarkRepository bookmarkRepository) {
		System.out.println("in init");
		return new CommandLineRunner() {

			@Override
			public void run(String... users) throws Exception {
				for (String user : users) {
					Account account = new Account(user, "password");
					accountRepository.save(account);
					Bookmark bookmark1 = new Bookmark(account, "http://bookmark/1/" + user, "a description");
					Bookmark bookmark2 = new Bookmark(account, "http://bookmark/2/" + user, "a description");
					bookmarkRepository.save(bookmark1);
					bookmarkRepository.save(bookmark2);
				}

			}
		};

	}

	@Bean
	FilterRegistrationBean corsFilter(@Value("${tagit.origin:http://localhost:9000}") final String origin) {
		return new FilterRegistrationBean(new Filter() {

			@Override
			public void init(FilterConfig filterConfig) throws ServletException {
				// TODO Auto-generated method stub

			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				HttpServletRequest httpServletRequest = (HttpServletRequest) request;
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				String method = httpServletRequest.getMethod();
				httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
				httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
				httpServletResponse.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
				httpServletResponse.setHeader("Access-Control-Allow-Credential", "true");
				httpServletResponse.setHeader("Access-Control-Allow-Headers",
						"Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
				if ("OPTIONS".equals(method)) {
					httpServletResponse.setStatus(HttpStatus.OK.value());
				} else {
					chain.doFilter(httpServletRequest, httpServletResponse);
				}
			}

			@Override
			public void destroy() {

			}
		});
	}

	public static void main(String[] args) {
		String[] users = { "user1", "user2", "user3" };
		SpringApplication.run(BookmarksProjectRestServiceApplication.class, users);
	}
}
