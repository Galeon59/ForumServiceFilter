package telran.ashkelon2018;

import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.service.AccountService;
import telran.ashkelon2018.forum.service.ForumService;

@SpringBootApplication
public class ForumtServiceFilterApplication implements CommandLineRunner {

	@Autowired
	ForumService forumService;
	
	@Autowired
	UserAccountRepository repository;
	
	@Autowired
	AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(ForumtServiceFilterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	// created Admin	
		if(!repository.existsById("Admin")) {
			String hasPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
			UserAccount userAccount = UserAccount.builder()
					.login("Admin")
					.password(hasPassword)
					.firstName("Super")
					.lastName("Adm")
					.expDate(LocalDateTime.now()
							.plusYears(25))
					.role("Administrator")
					.build();
			repository.save(userAccount);
		}
		
//		//add Role Profiles
//		Set<String> rolePrivileges1 = new HashSet<>();
//		rolePrivileges1.add("Create yourself");
//		rolePrivileges1.add("Update yourself");
//		rolePrivileges1.add("Update yourself password");
//		rolePrivileges1.add("Delete yourself");
//		Set<String> rolePrivileges2 = new HashSet<>();
//		rolePrivileges2.add("Create yourself");
//		rolePrivileges2.add("Update yourself");
//		rolePrivileges2.add("Update yourself password");
//		rolePrivileges2.add("Delete yourself");
//		rolePrivileges2.add("Delete other user");
//		Set<String> rolePrivileges3 = new HashSet<>();
//		rolePrivileges3.add("Create yourself");
//		rolePrivileges3.add("Update yourself");
//		rolePrivileges3.add("Update yourself password");
//		rolePrivileges3.add("Delete yourself");
//		rolePrivileges3.add("Delete other user");
//		rolePrivileges3.add("Addition roles");
//		rolePrivileges3.add("Delete roles");
//		
//		accountService.addRoleProfile(new RoleProfile("User", rolePrivileges1));
//		accountService.addRoleProfile(new RoleProfile("Moderator", rolePrivileges2));
//		accountService.addRoleProfile(new RoleProfile("Administrator", rolePrivileges3));

		
		
//		//add New Posts
//		Set<String> tag1 = new HashSet<>();
//		tag1.add("Spring");
//		Set<String> tag2 = new HashSet<>();
//		tag2.add("file");
//		tag2.add("SpringBootServletInitializer");
//		Set<String> tag3 = new HashSet<>();
//		tag3.add("Application");
//		tag3.add("Boot");
//		tag3.add("Spring");
//
//		forumService.addNewPost(new NewPostDto("Traditional Deployment",
//				"Spring Boot supports traditional deployment as well as more modern forms of deployment.", "Peter", tag1));
//		forumService.addNewPost(new NewPostDto("Create a Deployable War File",
//				"The first step in producing a deployable war file is to provide a SpringBootServletInitializer subclass and override its configure method.",
//				"Bob", tag2));
//		forumService.addNewPost(new NewPostDto("Convert an Existing Application to Spring Boot",
//				"For a non-web application, it should be easy to convert an existing Spring application to a Spring Boot application.",
//				"Sarah", tag3));
	}
}
