package com.fatec.tcc.findfm.Model.Http.Request;

import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.Telefone;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;

import java.util.Date;
import java.util.List;

public class AtualizarUsuarioRequest {

    private String id;
    private String nomeCompleto;
    private Telefone telefone;
    private String email;
    private String senha;
    private String foto;
    private TiposUsuario tipoUsuario;
    private String sobre;
    //Comum
    private String cidade;
    private String uf;

    //Contratante
    private int capacidadeLocal;
    private Date inauguracao;

    private String endereco;
    private int numero;

    //Musico
    private Date nascimento;
    private List<Instrumento> instrumentos;

    public AtualizarUsuarioRequest(){ }

    public AtualizarUsuarioRequest(Musico musico) {
        this.id = musico.getId();
        this.nomeCompleto = musico.getNomeCompleto();
        this.telefone = musico.getTelefone();
        this.email = musico.getEmail();
        this.foto = musico.getFoto();
        this.senha = musico.getSenha();
        this.nascimento = musico.getNascimento();
        this.cidade = musico.getCidade();
        this.uf = musico.getUf();
        this.instrumentos = musico.getInstrumentos();
        this.setSobre(musico.getSobre());
    }

    public AtualizarUsuarioRequest(Contratante contratante) {
        this.id = contratante.getId();
        this.nomeCompleto = contratante.getNomeCompleto();
        this.telefone = contratante.getTelefone();
        this.email = contratante.getEmail();
        this.foto = contratante.getFoto();
        this.senha = contratante.getSenha();
        this.inauguracao = contratante.getInauguracao();
        this.cidade = contratante.getCidade();
        this.uf = contratante.getUf();
        this.endereco = contratante.getEndereco();
        this.numero = contratante.getNumero();
        this.capacidadeLocal = contratante.getCapacidadeLocal();
        this.setSobre(contratante.getSobre());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
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

    public String getSobre() {
        return sobre;
    }

    public void setSobre(String sobre) {
        this.sobre = sobre;
    }
}
