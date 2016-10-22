package bookmarks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import bookmarks.exception.UserNotFoundException;
import bookmarks.hateoas.BookmarkResource;
import bookmarks.model.Account;
import bookmarks.model.Bookmark;
import bookmarks.repository.AccountRepository;
import bookmarks.repository.BookmarkRepository;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class BookmarkRestController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable("userId") String userId, @RequestBody Bookmark input) {
		validateUser(userId);
		Account account = accountRepository.findByUsername(userId);
		Bookmark bookmark = new Bookmark(account, input.getUri(), input.getDescription());
		bookmark = bookmarkRepository.save(bookmark);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(bookmark.getId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BookmarkResource readBookmark(@PathVariable String userId, @PathVariable Long id) {
		validateUser(userId);
		return new BookmarkResource(this.bookmarkRepository.findOne(id));
	}

	@RequestMapping(method = RequestMethod.GET)
	public Resources<BookmarkResource> readBookmarks(@PathVariable String userId) {
		validateUser(userId);
		Collection<Bookmark> bookmarks = this.bookmarkRepository.findByAccountUsername(userId);
		List<BookmarkResource> bookmarkResources = new ArrayList<>();
		for (Bookmark bookmark : bookmarks) {
			bookmarkResources.add(new BookmarkResource(bookmark));
		}
		Resources<BookmarkResource> resources = new Resources<>(bookmarkResources);
		return resources;
	}

	private void validateUser(String userId) {
		Account account = accountRepository.findByUsername(userId);
		if (account == null) {
			throw new UserNotFoundException(userId);
		}
	}
}
