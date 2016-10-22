package bookmarks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

	public static void main(String[] args) {
		String[] users = { "user1", "user2", "user3" };
		SpringApplication.run(BookmarksProjectRestServiceApplication.class, users);
	}
}
