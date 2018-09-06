package com.fatec.tcc.findfm.Model.Http.Response;

public class User {

    private String kind;
    private String fullName;
    private Avatar avatar;

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
