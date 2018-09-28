package com.fatec.tcc.findfm.Model.Business;

import com.fatec.tcc.findfm.Model.Http.Response.PostResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Post {

    private Usuario autor;
    private String titulo;
    private String descricao;
    private String cidade;
    private String idFoto;
    private List<String> idFotos;
    private byte[] fotoBytes;
    private String idVideo;
    private List<String> idVideos;
    private byte[] videoBytes;
    private Date data;
    private Long likes;
    private List<Comentario> comentarios;

    public Post(){}

    public Post(PostResponse postResponse){
        this.setIdFotos(new ArrayList<>());
        this.setIdVideos(new ArrayList<>());
        this.titulo = postResponse.getTitulo();
        this.descricao = postResponse.getDescricao();
        //TODO: talvez colocar
        //this.cidade
        //this.UF
        for(PostResponse.Midias midia: postResponse.getMidias()){
            if(midia.getTipoMidia().equals("img"))
                this.getIdFotos().add(midia.getId());
            else
                this.getIdVideos().add(midia.getId());
        }

        Usuario usuario = new Usuario();
        usuario.setId(postResponse.getAutor().get_id());
        usuario.setFotoID(postResponse.getAutor().getAvatar().get_id());
        usuario.setTipoUsuario(TiposUsuario.fromKind(postResponse.getAutor().getKind()));
        usuario.setNomeCompleto(postResponse.getAutor().getFullName());
        this.autor = usuario;
        //TODO: colocar esses campos
        this.data = postResponse.getCriacao();
        usuario.setEmail(postResponse.getAutor().getEmail());
        if(postResponse.getAutor().getTelefone() != null)
            usuario.setTelefone(postResponse.getAutor().getTelefone().getStateCode() + postResponse.getAutor().getTelefone().getNumber());

    }

    public String getTitulo() {
        return titulo;
    }

    public Post setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public Post setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public String getCidade() {
        return cidade;
    }

    public Post setCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Post setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        return this;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Post setAutor(Usuario autor) {
        this.autor = autor;
        return this;
    }

    public String getData() {
        if(data != null) {
            return new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss", Locale.US).format(data);
        }else
        {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        }
    }

    public Post setData(Date data) {
        this.data = data;
        return this;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public Post setIdFoto(String idFoto) {
        this.idFoto = idFoto;
        return this;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public Post setIdVideo(String idVideo) {
        this.idVideo = idVideo;
        return this;
    }

    public List<String> getIdFotos() {
        return idFotos;
    }

    public void setIdFotos(List<String> idFotos) {
        this.idFotos = idFotos;
    }

    public List<String> getIdVideos() {
        return idVideos;
    }

    public void setIdVideos(List<String> idVideos) {
        this.idVideos = idVideos;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public byte[] getFotoBytes() {
        return fotoBytes;
    }

    public void setFotoBytes(byte[] fotoBytes) {
        this.fotoBytes = fotoBytes;
    }

    public byte[] getVideoBytes() {
        return videoBytes;
    }

    public void setVideoBytes(byte[] videoBytes) {
        this.videoBytes = videoBytes;
    }
}
