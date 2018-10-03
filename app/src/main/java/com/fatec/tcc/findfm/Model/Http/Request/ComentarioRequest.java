package com.fatec.tcc.findfm.Model.Http.Request;

import com.fatec.tcc.findfm.Model.Http.Response.User;

import java.util.Date;
import java.util.Set;

public class ComentarioRequest {

    private User commenter;
    private String text;
    private Set<String> likes;
    private Date date;

    public User getCommenter() {
        return commenter;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
