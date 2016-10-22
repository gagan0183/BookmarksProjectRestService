package bookmarks.hateoas;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import bookmarks.controller.BookmarkRestController;
import bookmarks.model.Bookmark;

public class BookmarkResource extends ResourceSupport {
	private final Bookmark bookmark;

	public BookmarkResource(Bookmark bookmark) {
		this.bookmark = bookmark;
		String username = bookmark.getAccount().getUsername();
		this.add(new Link(bookmark.getUri(), "bookmark-uri"));
		this.add(ControllerLinkBuilder.linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
		this.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(BookmarkRestController.class, username)
				.readBookmark(username, bookmark.getId())).withSelfRel());
	}

	public Bookmark getBookmark() {
		return bookmark;
	}
}
