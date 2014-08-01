/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.Param;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Vitor Rifane
 */
@Entity
@Table(name = "TURMAS")
@NamedQueries({
    @NamedQuery(name = "TurmasDoProfessor",
               query = "  From Turma t"
                     + " Where t = :id ")})
@Views({
    /**
    * Minhas Turmas
    */
   @View(name = "MinhasTurmas",
        title = "Minhas Turmas",
      members = "Turma[codigoTurma;nomeTurma;qtdAlunos,qtdConteudos;enviarEmail(),enviarConteudo(),conteudosDaTurma();alunosDaTurma();]",
      namedQuery = "From br.edu.Turma t where t.usuario = :user",
      params = {@Param(name = "user", value = "#{context.currentUser}")},
     template = "@TABLE+@PAGE",
     roles = "Professor"),
    /**
     * Cadastro de turmas
     */
    @View(name = "CadastrarTurmas",
         title = "Cadastrar Turmas",
       members = "Turmas[#nomeTurma];cadastrarTurma()",
       namedQuery = "Select new br.edu.Turma()",       
       roles = "Professor")
})
public class Turma implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(length = 6)
    private String codigoTurma;
    
    @Column(length = 40)
    @NotEmpty(message = "Nome da turma não informado")
    private String nomeTurma;
    
    @Column(precision = 2)
    private Integer qtdAlunos;
    
    @Column(precision = 2)
    private Integer qtdConteudos;
    
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    public Turma(){
    }
    
    public Turma(String nomeTurma, Usuario usuario) {
        this.nomeTurma = nomeTurma;
        this.usuario = usuario;
        this.codigoTurma = RandomStringUtils.randomAlphanumeric(6);
    }
    
    public String cadastrarTurma() {

        Usuario usu = (Usuario) Context.getCurrentUser();
        Turma novaTurma = new Turma(nomeTurma, usu);
        novaTurma.setCodigoTurma(RandomStringUtils.randomAlphanumeric(6));               
        Repository.save(novaTurma);

        return "go:br.edu.Turma@MinhasTurmas";
    }
    
    public String enviarEmail() {
        GMailBuilder.enviarEmailTeste();
        return "go:home";
    }
    
    public String enviarConteudo() {
        Context.setValue("turmaContext", this);
        return "go:br.edu.Arquivo@EnviarConteudo";
    }
    
    public String alunosDaTurma() {
        Context.setValue("turmaContext", this);
        return "go:br.edu.AlunosTurma@AlunosDaTurma";
    }
    
    public String conteudosDaTurma(){
        Context.setValue("alunoTurmaContext", this);
        return "go:br.edu.Arquivo@ConteudosTurma";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public void setCodigoTurma(String codigoTurma) {
        this.codigoTurma = codigoTurma;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getQtdAlunos() {
        return qtdAlunos;
    }

    public void setQtdAlunos(Integer qtdAlunos) {
        this.qtdAlunos = qtdAlunos;
    }

    public Integer getQtdConteudos() {
        return qtdConteudos;
    }

    public void setQtdConteudos(Integer qtdConteudos) {
        this.qtdConteudos = qtdConteudos;
    }    
}
