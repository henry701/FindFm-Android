package com.fatec.tcc.findfm.Model.Http.Response;

public class User {

    private String kind;
    private String fullName;
    private String avatar;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getFullName() {
        if(fullName == null){
            return "Visitante";
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
