package com.fatec.tcc.findfm.Model.Http.Response;

import java.util.Date;
import java.util.List;

public class PostResponse {

    private String titulo;
    private String descricao;
    private Autor autor;
    private Long likes;
    private Date data;
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

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public class Autor {

        private String kind;
        private String _id;
        private String fullName;
        private Date startDate;
        private Avatar avatar;
        private boolean emailConfirmed;
        private String email;
        private Long premiumLevel;
        private User.Telefone telefone;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Avatar getAvatar() {
            return avatar;
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public boolean isEmailConfirmed() {
            return emailConfirmed;
        }

        public void setEmailConfirmed(boolean emailConfirmed) {
            this.emailConfirmed = emailConfirmed;
        }

        public Long getPremiumLevel() {
            return premiumLevel;
        }

        public void setPremiumLevel(Long premiumLevel) {
            this.premiumLevel = premiumLevel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public User.Telefone getTelefone() {
            return telefone;
        }

        public void setTelefone(User.Telefone telefone) {
            this.telefone = telefone;
        }

        public class Avatar {
            private User.Avatar.MediaMetadata fileMetadata;
            private String _id;

            public User.Avatar.MediaMetadata getFileMetadata() {
                return fileMetadata;
            }

            public void setFileMetadata(User.Avatar.MediaMetadata fileMetadata) {
                this.fileMetadata = fileMetadata;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }
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
