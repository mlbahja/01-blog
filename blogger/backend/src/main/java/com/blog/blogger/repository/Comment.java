package com.blog.blogger.repository;
import  java.util.List;

public class Comment {
    private int id;
    private String author;
    private String content;
    private int like;
    private List<Reponse> reponses;

    public Comment(int id, String author, String content, int like, List<Reponse> reponses){
        this.id = id;
        this.author = author;
        this.content = content;
        this.like = like;
        this.reponses = reponses;
    }
    public int getId(){return  id ;}
    public String getAuthor(){return author;}
    public String getContent(){return content;}
    public int getLike(){return like;}
    public List<Reponse> getReponses(){return reponses;}
    

}
