package com.fatec.tcc.findfm.Model.Business;

public class Usuario {

    private String id;
    private String nomeCompleto;
    private boolean confirmado;
    private boolean premium;
    private Telefone telefone;
    private String email;
    private String senha;
    private String fotoID;
    private String foto;
    private TiposUsuario tipoUsuario;

    public Usuario(){}

    /**
     * Para logar
     * @param nomeCompleto
     * @param senha
     */
    public Usuario(String nomeCompleto, String senha){
        this.setNomeCompleto(nomeCompleto);
        this.senha = senha;
    }

    /**
     * Completo
     * @param nomeCompleto
     * @param senha
     * @param email
     * @param telefone
     * @param foto
     * @param confirmado
     * @param premium
     */
    public Usuario(String nomeCompleto, String senha, String email, Telefone telefone, String foto, boolean confirmado, boolean premium, String id, String fotoID, TiposUsuario tipoUsuario){
        this.nomeCompleto = nomeCompleto;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
        this.confirmado = confirmado;
        this.premium = premium;
        this.id = id;
        this.fotoID = fotoID;
        this.tipoUsuario = tipoUsuario;

    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public Usuario setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
        return this;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public TiposUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TiposUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFotoID() {
        return fotoID;
    }

    public void setFotoID(String fotoID) {
        this.fotoID = fotoID;
    }
}
