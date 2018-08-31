package com.fatec.tcc.findfm.Model.Business;

public class Usuario {

    private String nomeUsuario;
    private String nomeCompleto;
    private boolean confirmado;
    private boolean premium;
    public String telefone;
    public String email;
    public String senha;
    public String foto;
    private TiposUsuario tipoUsuario;

    public Usuario(){}

    /**
     * Para logar
     * @param nomeUsuario
     * @param senha
     */
    public Usuario(String nomeUsuario, String senha){
        this.setNomeUsuario(nomeUsuario);
        this.senha = senha;
    }

    /**
     * Completo
     * @param nomeUsuario
     * @param nomeCompleto
     * @param senha
     * @param email
     * @param telefone
     * @param foto
     * @param confirmado
     * @param premium
     */
    public Usuario(String nomeUsuario, String nomeCompleto, String senha, String email, String telefone, String foto, boolean confirmado, boolean premium, TiposUsuario tipoUsuario){
        this.setNomeUsuario(nomeUsuario);
        this.nomeCompleto = nomeCompleto;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
        this.confirmado = confirmado;
        this.premium = premium;
        this.tipoUsuario = tipoUsuario;

    }

    public String getUsuario() {
        return getNomeUsuario();
    }

    public void setUsuario(String nomeUsuario) {
        this.setNomeUsuario(nomeUsuario);
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public TiposUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TiposUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
