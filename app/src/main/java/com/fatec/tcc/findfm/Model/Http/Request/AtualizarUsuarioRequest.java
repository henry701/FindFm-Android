package com.fatec.tcc.findfm.Model.Http.Request;

import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;

import java.util.Date;
import java.util.List;

public class AtualizarUsuarioRequest {

    private String nomeUsuario;
    private String nomeCompleto;
    private boolean confirmado;
    private boolean premium;
    private String telefone;
    private String email;
    private String senha;
    private String foto;
    private TiposUsuario tipoUsuario;
    //Comum
    private String cidade;
    private String uf;

    //Banda
    private int numeroIntegrantes;
    private Date formacao;

    private List<Musico> musicos;

    //Contratante
    private int capacidadeLocal;
    private Date inauguracao;
    private String endereco;
    private int numero;

    //Musico
    private Date nascimento;
    private List<Instrumento> instrumentos;

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public TiposUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TiposUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public int getNumeroIntegrantes() {
        return numeroIntegrantes;
    }

    public void setNumeroIntegrantes(int numeroIntegrantes) {
        this.numeroIntegrantes = numeroIntegrantes;
    }

    public Date getFormacao() {
        return formacao;
    }

    public void setFormacao(Date formacao) {
        this.formacao = formacao;
    }

    public List<Musico> getMusicos() {
        return musicos;
    }

    public void setMusicos(List<Musico> musicos) {
        this.musicos = musicos;
    }

    public int getCapacidadeLocal() {
        return capacidadeLocal;
    }

    public void setCapacidadeLocal(int capacidadeLocal) {
        this.capacidadeLocal = capacidadeLocal;
    }

    public Date getInauguracao() {
        return inauguracao;
    }

    public void setInauguracao(Date inauguracao) {
        this.inauguracao = inauguracao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public List<Instrumento> getInstrumentos() {
        return instrumentos;
    }

    public void setInstrumentos(List<Instrumento> instrumentos) {
        this.instrumentos = instrumentos;
    }
}
