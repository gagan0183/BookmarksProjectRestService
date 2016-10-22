package bookmarks.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String userId) {
		super("could not found user " + userId);
	}
}
