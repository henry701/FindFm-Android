package com.fatec.tcc.findfm.Model.Http.Response;

public class User {

    private String kind;
    private String fullName;
    private String email;
    private Avatar avatar;
    private Telefone telefone;
    private Endereco endereco;

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

    public class Telefone {

        private int countryCode;
        private int stateCode;
        private String number;

        public String getCountryCode() {
            return String.valueOf(countryCode);
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = Integer.parseInt(countryCode);
        }

        public String getStateCode() {
            return String.valueOf(stateCode);
        }

        public void setStateCode(String stateCode) {
            this.stateCode = Integer.parseInt(stateCode);
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    public class Endereco {

        private String estado;
        private String cidade;
        private String endereco;
        private String numero;

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getEndereco() {
            return endereco;
        }

        public void setEndereco(String endereco) {
            this.endereco = endereco;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }
    }
}
