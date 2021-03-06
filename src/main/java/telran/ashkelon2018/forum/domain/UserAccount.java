package telran.ashkelon2018.forum.domain;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"login"})
@Document(collection = "Forum_users_Security_2018")

public class UserAccount {
	@Id
	String login;
	String password;
	String firstName;
	String lastName;
	@Singular
	Set<String> roles;
//	RoleProfile roles;
	LocalDateTime expDate;
	
	
	public void addRole(String role) {
		roles.add(role);
	}

	public void removeRole(String role) {
		roles.remove(role);
	}

}
