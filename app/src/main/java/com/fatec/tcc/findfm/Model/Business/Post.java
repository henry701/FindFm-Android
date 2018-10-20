package com.fatec.tcc.findfm.Model.Business;

import com.fatec.tcc.findfm.Model.Http.Response.FeedResponse;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class Post {

    private String id;
    private Usuario autor;
    private String titulo;
    private String descricao;
    private String cidade;
    private String uf;
    private List<FileReference> midias;
    private DateTime data;
    private Long likes;
    private Set<String> likesId;
    private boolean liked;
    private List<Comentario> comentarios;

    public Post(){}

    public Post(FeedResponse feedResponse){
        this.setId(feedResponse.getId());
        this.titulo = feedResponse.getTitulo();
        this.descricao = feedResponse.getDescricao();
        this.midias = new ArrayList<>();

        if(feedResponse.getMidias() != null) {
            for (FeedResponse.Midias midia : feedResponse.getMidias()) {
                midias.add(new FileReference()
                        .setId(midia.getId())
                        .setContentType(midia.getTipoMidia())
                );
            }
        }

        this.likesId = feedResponse.getUsuarioLikes();
        this.likes = likesId == null ? 0L : (long) likesId.size();

        Usuario usuario = new Usuario();
        usuario.setId(feedResponse.getAutor().getUsuario().getId());
        usuario.setFotoID(feedResponse.getAutor().getUsuario().getAvatar() != null ? feedResponse.getAutor().getUsuario().getAvatar().get_id() : null);
        usuario.setTipoUsuario(TiposUsuario.fromKind(feedResponse.getAutor().getUsuario().getKind()));
        usuario.setNomeCompleto(feedResponse.getAutor().getUsuario().getFullName());
        usuario.setEmail(feedResponse.getAutor().getUsuario().getEmail());

        if(feedResponse.getAutor().getUsuario().getTelefone() != null) {
            usuario.setTelefone( new Telefone(feedResponse.getAutor().getUsuario().getTelefone().getStateCode(),
                    feedResponse.getAutor().getUsuario().getTelefone().getNumber()));
        }

        if(feedResponse.getAutor().getUsuario().getEndereco() != null) {
            this.cidade = feedResponse.getAutor().getUsuario().getEndereco().getCidade();
            this.uf = feedResponse.getAutor().getUsuario().getEndereco().getEstado();
        }

        if(feedResponse.getComentarios() != null){
            this.comentarios = feedResponse.getComentarios();
        }

        this.autor = usuario;
        this.liked = likes != 0L ? likesId.contains(usuario.getId()) : false;
        this.data = feedResponse.getCriacao();
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

    public String getLocalizacaoFormatada() {
        return this.cidade + " - " + this.uf;
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

    public String getData()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        if(data == null)
        {
            data = new DateTime();
        }
        return sdf.format(data.toLocalDateTime().toDate());
    }

    public Post setData(DateTime data) {
        this.data = data;
        return this;
    }

    public String getLikes() {
        return likes.toString();
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public void setLikes(String likes) {
        this.likes = Long.parseLong(likes);
    }

    public Set<String> getLikesId() {
        return likesId;
    }

    public void setLikesId(Set<String> likesId) {
        this.likesId = likesId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getId() {
        return id;
    }

    public Post setId(String id) {
        this.id = id;
        return this;
    }

    public List<FileReference> getMidias() {
        return midias;
    }

    public Post setMidias(List<FileReference> midias) {
        this.midias = midias;
        return this;
    }
}
