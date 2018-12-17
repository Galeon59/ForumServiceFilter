package telran.ashkelon2018.forum.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Document(collection = "Forum_roles_Security_2018")
public class RoleProfile {
	@Id
	String role;
	@Singular
	Set<String> rolePrivileges;

	public void addPrivilege(String privilege) {
		rolePrivileges.add(privilege);
	}

	public void removePrivilege(String privilege) {
		rolePrivileges.remove(privilege);
	}
}
