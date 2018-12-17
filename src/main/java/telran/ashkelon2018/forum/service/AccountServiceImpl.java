package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl.Role;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.dao.UserRolesRepository;
import telran.ashkelon2018.forum.domain.RoleProfile;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exceptions.ForbiddenException;
import telran.ashkelon2018.forum.exceptions.UserConflictException;
import telran.ashkelon2018.forum.exceptions.UserNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	UserRolesRepository rolesRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration
					.tokenDecode(token);
			if (userRepository.existsById(credentials.getLogin())) {
				throw new UserConflictException();
			}
			String hashPassword = BCrypt.hashpw(credentials.getPassword(),
					BCrypt.gensalt());
				UserAccount userAccount = UserAccount.builder()
					.login(credentials.getLogin()).password(hashPassword)
					.firstName(userRegDto.getFirstName())
					.lastName(userRegDto.getLastName())
					.role("User")
					.expDate(LocalDateTime.now()
							.plusDays(accountConfiguration.getExpPeriod()))
					.build();
			userRepository.save(userAccount);
			return convertToUserProfileDto(userAccount);
	}

	private UserProfileDto convertToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder().firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.login(userAccount.getLogin()).roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccount = userRepository
				.findById(credentials.getLogin()).get();
		if (userRegDto.getFirstName() != null) {
			userAccount.setFirstName(userRegDto.getFirstName());
		}
		if (userRegDto.getLastName() != null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		// FIXME DELETE
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccountToken = userRepository
				.findById(credentials.getLogin())
				.orElseThrow(UserNotFoundException::new);
		if (!(userAccountToken.getLogin().equals(login)
				|| userAccountToken.getRoles().contains("Moderator")
				|| userAccountToken.getRoles().contains("Administrator"))) {
			throw new ForbiddenException();
		}
		if( login.equals("Admin") && !userAccountToken.getRoles().contains("Administrator")) {
			throw new ForbiddenException();
		}
		UserAccount userAccount = userRepository.findById(login)
				.orElseThrow(UserNotFoundException::new);
		userRepository.delete(userAccount);
	return convertToUserProfileDto(userAccount);
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		// FIXME PUT
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccountToken = userRepository
				.findById(credentials.getLogin())
				.orElseThrow(UserNotFoundException::new);
		if (!userAccountToken.getRoles().contains("Administrator")) {
			throw new ForbiddenException();
		}
		UserAccount userAccount = userRepository.findById(login)
				.orElseThrow(UserNotFoundException::new);
		userAccount.addRole(role);
		userRepository.save(userAccount);
		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		// FIXME DELETE
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccountToken = userRepository
				.findById(credentials.getLogin())
				.orElseThrow(UserNotFoundException::new);
		if (!userAccountToken.getRoles().contains("Administrator")) {
			throw new ForbiddenException();
		}
		UserAccount userAccount = userRepository.findById(login)
				.orElseThrow(UserNotFoundException::new);
		userAccount.removeRole(role);
		userRepository.save(userAccount);
		return userAccount.getRoles();
	}

	@Override
	public void changePassword(String password, String token) {
		try {
			AccountUserCredentials credentials = accountConfiguration
					.tokenDecode(token);
			UserAccount userAccount = userRepository
					.findById(credentials.getLogin()).get();
			String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			userAccount.setPassword(hashPassword);
			userAccount.setExpDate(LocalDateTime.now()
					.plusDays(accountConfiguration.getExpPeriod()));
			userRepository.save(userAccount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public UserProfileDto login(String token) {
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccount = userRepository
				.findById(credentials.getLogin()).get();
		return convertToUserProfileDto(userAccount);
	}

	
	
	//// the draft for Role Based Access Control (RBAC)
	
	@Override
	public RoleProfile addRolePrivilege(RoleProfile profile, String privilege) {
		//BAD SOLUTION!
		RoleProfile userProfiles = new RoleProfile(profile.getRole(),
				profile.getRolePrivileges());
		rolesRepository.save(profile);
		return userProfiles;
	}

////the draft for Role Based Access Control (RBAC)
	@Override
	public RoleProfile removeRolePrivilege(RoleProfile profile, String privilege,
			String token) {
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccountToken = userRepository
				.findById(credentials.getLogin())
				.orElseThrow(UserNotFoundException::new);
		if (!userAccountToken.getRoles().contains("Administrator")) {
			throw new ForbiddenException();
		}
		profile.removePrivilege(privilege);
		rolesRepository.save(profile);
		return profile;
	}

}
