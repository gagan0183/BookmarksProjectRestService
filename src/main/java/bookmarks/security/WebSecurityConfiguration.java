package bookmarks.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import bookmarks.exception.UserNotFoundException;
import bookmarks.model.Account;
import bookmarks.repository.AccountRepository;

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Account account = accountRepository.findByUsername(username);
				if (account == null) {
					throw new UserNotFoundException("could not find the user : " + username);
				}
				User user = new User(username, account.getPassword(), true, true, true, true,
						AuthorityUtils.createAuthorityList("USER", "write"));
				return user;
			}
		};
	}
}
