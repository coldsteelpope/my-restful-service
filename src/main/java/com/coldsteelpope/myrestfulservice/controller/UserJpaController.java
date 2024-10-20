package com.coldsteelpope.myrestfulservice.controller;

import com.coldsteelpope.myrestfulservice.bean.Post;
import com.coldsteelpope.myrestfulservice.bean.User;
import com.coldsteelpope.myrestfulservice.exception.UserNotFoundException;
import com.coldsteelpope.myrestfulservice.repository.PostRepository;
import com.coldsteelpope.myrestfulservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
public class UserJpaController
{
    private UserRepository userRepository;
    private PostRepository postRepository;
    public UserJpaController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    // /jpa/users -> ResponseEntity로 고칠 수 없을까?
    @GetMapping("/users")
    public UsersResponse retrieveAllUsers()
    {
/*        Map<String, Object> map = new HashMap<>();
        List<User> users = userRepository.findAll();

        map.put("count", users.size());
        map.put("users", users);

        return ResponseEntity.ok(map);*/

        List<User> users = userRepository.findAll();
        UsersResponse usersResponse = new UsersResponse(users.size(), userRepository.findAll());
        return usersResponse;
    }

    @Getter
    class UsersResponse
    {
        Integer count;
        List<User> users;
        public UsersResponse(Integer count, List<User> users)
        {
            this.count = count;
            this.users = users;
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity retrieveUserById(@PathVariable int id)
    {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
        {
            throw new UserNotFoundException("id-" + id);
        }

        EntityModel entityModel = EntityModel.of(user.get());
        WebMvcLinkBuilder linTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(linTo.withRel("all-users"));

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id)
    {
        userRepository.deleteById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user)
    {
        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable int id)
    {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
        {
            throw new UserNotFoundException("id - " + id);
        }
        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity createPost(@PathVariable int id, @RequestBody Post post)
    {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent())
        {
            throw new UserNotFoundException("id = " + id);
        }

        User user = userOptional.get();
        post.setUser(user);

        postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
