package com.fatec.tcc.findfm.Model.Http.Response;

import com.fatec.tcc.findfm.Model.Business.Endereco;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musica;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.Model.Business.Telefone;
import com.fatec.tcc.findfm.Model.Business.Trabalho;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String id;
    private String kind;
    private String fullName;
    private String email;
    private Avatar avatar;
    private Telefone telefone;
    private Endereco endereco;
    private Date date;
    private String sobre;
    private LinkedTreeMap<String, Double> habilidades;
    private List<Musica> musicas;
    private String visits;
    private String musicsRadio;
    private List<TrabalhoResponse> trabalhos;

    public ArrayList<Instrumento> getIntrumentos() {
        ArrayList<Instrumento> instrumentos = new ArrayList<>();

        if(getHabilidades() != null) {
            for (String key : getHabilidades().keySet()) {
                instrumentos.add(
                        new Instrumento(key.toUpperCase().charAt(0) + key.substring(1, key.length()),
                                NivelHabilidade.from(getHabilidades().get(key).intValue())));
            }
        }
        return instrumentos;
    }

    public void setHabilidades(LinkedTreeMap<String, Double> habilidades) {
        this.habilidades = habilidades;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSobre() {
        return sobre;
    }

    public void setSobre(String sobre) {
        this.sobre = sobre;
    }

    public String getVisits() {
        return visits == null ? "0" : visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public LinkedTreeMap<String, Double> getHabilidades() {
        return habilidades;
    }

    public List<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
    }

    public String getMusicsRadio() {
        return musicsRadio;
    }

    public void setMusicsRadio(String musicsRadio) {
        this.musicsRadio = musicsRadio;
    }

    public List<Trabalho> getTrabalhos() {
        List<Trabalho> trabalhos = new ArrayList<>();
        for(TrabalhoResponse trabalhoResponse : this.trabalhos){
            Trabalho trabalho = new Trabalho();
            trabalho.setId(trabalhoResponse.getId());
            trabalho.setMusicos(trabalhoResponse.getMusicos());
            trabalho.setDescricao(trabalhoResponse.getDescricao());
            trabalho.setNome(trabalhoResponse.getNome());
            trabalho.setOriginal(trabalhoResponse.getOriginal());
            trabalho.setMidias(trabalhoResponse.getMidias());
            trabalho.setMusicas(trabalhoResponse.getMusicas());
            trabalhos.add(trabalho);
        }

        return trabalhos;
    }

    public void setTrabalhos(List<TrabalhoResponse> trabalhos) {
        this.trabalhos = trabalhos;
    }

    public class Avatar {

        private String _id;
        private MediaMetadata mediaMetadata;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public MediaMetadata getMediaMetadata() {
            return mediaMetadata;
        }

        public void setMediaMetadata(MediaMetadata mediaMetadata) {
            this.mediaMetadata = mediaMetadata;
        }

        public class MediaMetadata{

            private int mediaType;
            private String contentType;

            public int getMediaType() {
                return mediaType;
            }

            public void setMediaType(int mediaType) {
                this.mediaType = mediaType;
            }

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }
        }
    }
}
