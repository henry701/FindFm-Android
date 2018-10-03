package com.fatec.tcc.findfm.Model.Http.Response;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public class PostResponse {

    private String titulo;
    private String descricao;
    private DateTime criacao;
    private Autor autor;
    private Set<String> UsuarioLikes;
    private String cidade;
    private String UF;
    private List<Midias> midias;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Set<String> getUsuarioLikes() {
        return UsuarioLikes;
    }

    public void setUsuarioLikes(Set<String> usuarioLikes) {
        UsuarioLikes = usuarioLikes;
    }

    public List<Midias> getMidias() {
        return midias;
    }

    public void setMidias(List<Midias> midias) {
        this.midias = midias;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public DateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(DateTime criacao) {
        this.criacao = criacao;
    }

    public class Autor {

        private User usuario;

        public User getUsuario() {
            return usuario;
        }

        public void setUsuario(User usuario) {
            this.usuario = usuario;
        }
    }

    public class Midias {
        private String id;
        private String tipoMidia;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTipoMidia() {
            return tipoMidia;
        }

        public void setTipoMidia(String tipoMidia) {
            this.tipoMidia = tipoMidia;
        }
    }
}
