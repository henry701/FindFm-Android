package com.fatec.tcc.findfm.Model.Http.Response;

import com.fatec.tcc.findfm.Model.Business.FileReference;
import com.fatec.tcc.findfm.Model.Business.Musica;
import com.fatec.tcc.findfm.Model.Business.Musico;

import java.util.ArrayList;
import java.util.List;

public class TrabalhoResponse {

    private String id;
    private String nome;
    private String descricao;
    private boolean original;
    private List<FileReference> midias;
    private List<Musica> musicas;
    private List<User> musicos;

    public String getNome() {
        return nome;
    }

    public TrabalhoResponse setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public boolean isOriginal() {
        return original;
    }

    public String getOriginal() {
        return String.valueOf(original);
    }

    public TrabalhoResponse setOriginal(boolean original) {
        this.original = original;
        return this;
    }

    public TrabalhoResponse setOriginal(String original) {
        this.original = Boolean.valueOf(original);
        return this;
    }

    public List<Musico> getMusicos() {
        List<Musico> musicos = new ArrayList<>();
        for(User musico : this.musicos){
            Musico usuario = new Musico();
            usuario.setId(musico.getId());
            if(musico.getAvatar() != null) {
                usuario.setFotoID(musico.getAvatar().get_id());
            }
            usuario.setNomeCompleto(musico.getFullName());
            usuario.setSobre(musico.getSobre());
            musicos.add(usuario);
        }
        return musicos;
    }

    public TrabalhoResponse setMusicos(List<User> musicos) {
        this.musicos = musicos;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public TrabalhoResponse setId(String id) {
        this.id = id;
        return this;
    }

    public List<FileReference> getMidias() {
        return midias;
    }

    public TrabalhoResponse setMidias(List<FileReference> midias) {
        this.midias = midias;
        return this;
    }

    public List<Musica> getMusicas() {
        return musicas;
    }

    public TrabalhoResponse setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
        return this;
    }
}
