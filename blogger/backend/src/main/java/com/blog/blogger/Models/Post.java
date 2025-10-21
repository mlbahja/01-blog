package com.blog.blogger.Models;

import java.util.List;

public class Post {
    private int id;
    private String title;
    private String content;
    private String author;
    private int like;
    private List<String> tags;
    private List<Comment> comments;

    public Post(int id, String title, String content, String author,int like ,List<String> tags, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.like = like;
        this.comments = comments;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public List<String> getTags() { return tags; }
    public  int getLike(){
        return like;
    }
    public List<Comment> getComments(){return  comments;}

}
