package telran.ashkelon2018.forum.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exceptions.ForbiddenException;
import telran.ashkelon2018.forum.exceptions.PostNotFoundException;
import telran.ashkelon2018.forum.exceptions.UserNotFoundException;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	ForumRepository repository;
	
	@Autowired
	UserAccountRepository userRepository;
	
	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public Post addNewPost(NewPostDto newPost) {
		Post post = new Post(newPost.getTitle(), newPost.getContent(),
				newPost.getAuthor(), newPost.getTags());
		repository.save(post);
		return post;
	}

	@Override
	public Post getPost(String id) {
		return repository.findById(id).orElseThrow(PostNotFoundException::new);
	}

	@Override
	public Post removePost(String id, String token) {
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		UserAccount userAccountToken = userRepository
				.findById(credentials.getLogin())
				.orElseThrow(UserNotFoundException::new);
		Post post = getPost(id);
		if (!(userAccountToken.getLogin().equals(post.getAuthor())
				|| userAccountToken.getRoles().contains("Moderator")
				|| userAccountToken.getRoles().contains("Administrator"))) {
			throw new ForbiddenException();
		}
		
		repository.deleteById(id);
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto, String token) {
		AccountUserCredentials credentials = accountConfiguration
				.tokenDecode(token);
		Post post = repository.findById(postUpdateDto.getId()).orElseThrow(PostNotFoundException::new);
		if (!credentials.getLogin().equals(post.getAuthor())) {
			throw new ForbiddenException();
		}
		post.setContent(postUpdateDto.getContent());
		repository.save(post);
		return post;
	}

	@Override
	public boolean addLike(String id) {
		
		Post post = repository.findById(id).orElseThrow(PostNotFoundException::new);
		post.addLike();
		repository.save(post);
		return true;
	}
	
	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = repository.findById(id).orElseThrow(PostNotFoundException::new);
		Comment comment = new Comment(newComment.getUser(), newComment.getMessage());
			post.addComment(comment);
			repository.save(post);	
		return post;
	}

	@Override
	public Iterable<Post> findPostsByTags(List<String> tags) {
		return repository.findAllByTags(tags);
	}

	@Override
	public Iterable<Post> findPostsByAuthor(String author) {
		return repository.findAllByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datesDto) {
		return repository.findAllByDateCreatedBetween(LocalDate.parse(datesDto.getDateFrom()),
				LocalDate.parse(datesDto.getDateTo()));
	}

	

}
