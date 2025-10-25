package com.blog.blogger.service;

import java.util.ArrayList;
import java.util.List;

import com.blog.blogger.repository.Comment;
import com.blog.blogger.repository.Post;
import com.blog.blogger.repository.Reponse;

public class PostService {
    private List<Post> posts = new ArrayList<>();

    public PostService() {
        posts.add(
    new Post(
        2,
        "Second Post",
        "Learning Spring Boot!",
        "Admin",
        13,
        List.of("java", "spring", "api"),
        List.of(
            new Comment(
                1,
                "Ana",
                "comments1",
                2,
                List.of(
                    new Reponse(1, "reponse of comment", "I don’t like this comment")
                )
            ),
            new Comment(
                2,
                "Bob",
                "Nice tutorial!",
                2,
                List.of(
                    new Reponse(2, "reply", "Thanks Bob! Glad you liked it 😊"),
                    new Reponse(3, "reply", "I’ll post more soon!")
                )
            )
        )
    )
);

        posts.add(new Post(3, "Second Post", "Learning Spring Boot!", "Admin", 13,List.of("java", "spring", "api"),List.of(new Comment(1,"ana","comments1",2,List.of(new Reponse(1, "reponse of comment", "I dont like this comment"))))));
    }

    public List<Post> getAllPosts() {
        return posts;
    }
}
