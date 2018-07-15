package com.michaelfmnk.aldrin.controllers;


import com.michaelfmnk.aldrin.dtos.CommentDto;
import com.michaelfmnk.aldrin.dtos.PostDto;
import com.michaelfmnk.aldrin.dtos.params.PageSortParams;
import com.michaelfmnk.aldrin.entities.Comment;
import com.michaelfmnk.aldrin.repositories.CommentRepository;
import com.michaelfmnk.aldrin.repositories.PostRepository;
import com.michaelfmnk.aldrin.repositories.UserRepository;
import com.michaelfmnk.aldrin.security.JwtUser;
import com.michaelfmnk.aldrin.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(Api.ROOT_PATH)
public class PostController {


    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private final PostService postService;


    @GetMapping(Api.Posts.POST_BY_ID)
    public PostDto getPostById(@PathVariable("post_id") Integer id){
        return postService.findPostById(id);
    }


    @PutMapping(Api.Posts.POST_BY_ID)
    public PostDto updatePost(@PathVariable("post_id") Integer id, @RequestBody PostDto postDto){
        return postService.updatePost(id, postDto);
    }


//    @PostMapping("")
//    public ResponseEntity<?> postPost(@RequestParam("file") MultipartFile file,
//                                      @RequestPart("title") String title,
//                                      Authentication authentication,
//                                      RedirectAttributes redirectAttributes){
//        Path path = storageService.store(file);
//        User user = userRepository.findUserByUsername(authentication.getName());
//        Photo photo = new Photo();
//        Post post = new Post();
//        photo.setUrl(path.toString()); //setting url to uploaded image
//        post.setTitle(title);
//        post.setAuthor(user);
//
//        post.setPhoto(photo);  //setting relations between post and photo
//        photo.setPost(post);
//        postRepository.save(post);
//
//        return ResponseEntity.ok(post);
//
//    }


    @GetMapping(Api.Posts.POST_COMMENTS)
    public List<CommentDto> getComments(@PathVariable("post_id") Integer postId, @ModelAttribute PageSortParams params){
        return postService.getCommentsForPost(postId, params);
    }


    @PostMapping(Api.Posts.POST_COMMENTS)
    public PostDto postComment(@PathVariable("post_id") Integer postId,
                                         @RequestBody @Valid CommentDto commentDto,
                                         Authentication auth){
        commentDto = commentDto.toBuilder()
                .userId(getUserIdFromAuthentication(auth))
                .postId(postId)
                .build();
        return postService.addCommentToPost(postId, commentDto);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Api.Posts.POST_LIKES)
    public void setLike(@PathVariable("post_id") Integer id,
                                     Authentication auth){
        postService.addLikeForPost(id, getUserIdFromAuthentication(auth));
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(Api.Posts.POST_LIKES)
    public void deleteLike(@PathVariable("post_id") Integer id,
                                        Authentication auth){
        postService.deleteLikeForPost(id, getUserIdFromAuthentication(auth));
    }


    private Integer getUserIdFromAuthentication(Authentication auth) {
        return ((JwtUser) auth.getPrincipal()).getId();
    }
}