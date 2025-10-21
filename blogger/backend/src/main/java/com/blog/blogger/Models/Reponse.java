package com.blog.blogger.Models;

public class Reponse {
    private int id;
    private String author;
    private String content;
    
    public Reponse(int id,String author, String content){
        this.id = id;
        this.author = author;
        this.content = content;
    }
    public int getId(){
        return  id;
    }
    public String getAuthor(){
        return author;
    }
    public String getContent(){
        return  content;
    }
}
