/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.PropertyDescriptor;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Vitor Rifane
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ConsultarTurma",
               query = "  From Turma t"
                     + " Where t.codigoTurma = :codigoTurma ")})
@Views({    
/**
 * Turmas do professor
 */
/*@View(name = "MinhasTurmasTA",
     title = "Minhas Turmas TA",
   members = "AlunosTurma[turma.codigoTurma;"
           + "  turma.nomeTurma;enviarEmail();]",
  namedQuery = "From br.edu.AlunosTurma at where at.turma.usuario = :user",
  params = {@Param(name = "user", value = "#{context.currentUser}")},
  template = "@TABLE+@PAGER",
  roles = "Professor"),*/
    
/**
 * Turmas do Aluno
 */
@View(name = "MinhasDisciplinas",
     title = "Minhas Disciplinas",
   members = "AlunosTurma[turma.codigoTurma;"
           + "  turma.nomeTurma;"
           + "  quantidadeAtividade, quantidadeFalta;"
           + "  enviarEmail(),enviarArquivo();]",
  template = "@TABLE+@PAGER",
  roles = "Aluno"),
        
/**
 * Aluno se cadastrando na turma
 */
@View(name = "CadastrarNaTurma",
     title = "Cadastrar-me na turma",
   members = "AlunosTurma[Turma[#turma.codigoTurma];#matricula;cadastrarNaTurma()]",
  namedQuery = "Select new br.edu.AlunosTurma()",
  roles = "Aluno")
})
public class AlunosTurma implements Serializable {
    
    @Id
    @GeneratedValue    
    @PropertyDescriptor(index = 1, hidden = true)
    private Integer id;
    
    @ManyToOne(optional = false)
    private Turma turma = new Turma();
    
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    @Column(length = 30)
    private String matricula;
    
    @Column(precision = 3)
    private Integer quantidadeFalta;
    
    @Column(precision = 3)
    private Integer quantidadeAtividade;
    
    private String codTurma;
    
    public AlunosTurma(){
        this.usuario = (Usuario) Context.getCurrentUser();
        this.quantidadeFalta = 0;
        this.quantidadeAtividade = 0;
    }
    
    public AlunosTurma(Turma turma, Usuario usuario, String matricula) {
        this.turma = turma;
        this.usuario = usuario;
        this.quantidadeFalta = 0;
        this.quantidadeAtividade = 0;
    }
    
    public String cadastrarNaTurma() {
        
        List<Turma> turmas = Repository.query("ConsultarTurma", turma.getCodigoTurma());
        
                
         if (turmas.size() == 1) {
            Turma turmaDoAluno = turmas.get(0);
            Usuario usu = (Usuario) Context.getCurrentUser();
            AlunosTurma alunoTurma = new AlunosTurma(turmaDoAluno,usu, matricula);
            Repository.save(alunoTurma);
            turmaDoAluno.setQtdAlunos(turmaDoAluno.getQtdAlunos()+1);
            Repository.save(turmaDoAluno);
            //return "go:domain.User@Users";
        }else {
            throw new SecurityException("Turma não encontrada");
        }
         
        return "go:br.edu.AlunosTurma@MinhasDisciplinas";
    }
    
    public String enviarEmail() {
        return "go:home";
    }
    
    public String enviarArquivo() {
        return "go:br.edu.Arquivo@EnviarAtividade";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getQuantidadeFalta() {
        return quantidadeFalta;
    }

    public void setQuantidadeFalta(Integer quantidadeFalta) {
        this.quantidadeFalta = quantidadeFalta;
    }

    public Integer getQuantidadeAtividade() {
        return quantidadeAtividade;
    }

    public void setQuantidadeAtividade(Integer quantidadeAtividade) {
        this.quantidadeAtividade = quantidadeAtividade;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodTurma() {
        return codTurma;
    }

    public void setCodTurma(String codTurma) {
        this.codTurma = codTurma;
    }
    
    
}
