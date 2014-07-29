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
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Past;

/**
 *
 * @author br0600106533
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ConsultarAlunoTurma",
               query = "  From AlunosTurma at"
                     + " Where at.turma.codigoTurma = :codigoTurma ")})
@Views({
/**
 * Professor enviando conteúdo para a turma
 */
@View(name = "EnviarConteudo",
     title = "Enviar conteúdo",
    members = "Arquivo[Turma[#turma.codigoTurma];Arquivo[#arquivo];enviarConteudo()]",
    namedQuery = "Select new br.edu.Arquivo()",
    roles = "Professor"),
/**
 * Aluno enviando conteúdo para a turma
 */
@View(name = "EnviarAtividade",
     title = "Enviar atividade",
    members = "Arquivo[Turma[#turma.codigoTurma];Arquivo[#arquivo];enviarAtividade()]",
    namedQuery = "Select new br.edu.Arquivo()",
    roles = "Aluno"),
/**
* Minhas Turmas
*/
@View(name = "MeusConteudos",
     title = "Meus conteúdos",
    members = "Arquivo[turma.nomeTurma;arquivo;dataEnvio;novoConteudo();]",
    namedQuery = "From br.edu.Arquivo a where a.usuario = :user",
    params = {@Param(name = "user", value = "#{context.currentUser}")},
    template = "@TABLE+@PAGE",
    roles = "Professor"),
/**
* Minhas Turmas
*/
@View(name = "MinhasAtividades",
     title = "Minhas atividades",
    members = "Arquivo[turma.nomeTurma;arquivo;dataEnvio;novaAtividade();]",
    namedQuery = "From br.edu.Arquivo a where a.usuario = :user",
    params = {@Param(name = "user", value = "#{context.currentUser}")},
    template = "@TABLE+@PAGE",
    roles = "Aluno")
})
public class Arquivo implements Serializable {
    
    @Id
    @GeneratedValue    
    private Integer id;
    
    @Lob
    private byte[] arquivo;
    
    @Past
    @Temporal(TemporalType.DATE)
    private Date dataEnvio; 
    
    @ManyToOne(optional = false)
    private Turma turma = new Turma();
    
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    public Arquivo(){
    }
    
    public Arquivo(byte[] arquivo, Turma turma, Usuario usuario) {
        this.arquivo = arquivo;
        this.turma = turma;
        this.usuario = usuario;
        this.dataEnvio = new Date();
    }
    
    public String enviarAtividade() {
        
        List<AlunosTurma> alunosTurma = Repository.query("ConsultarAlunoTurma", turma.getCodigoTurma());        
        
         if (alunosTurma.size() == 1) {
            AlunosTurma alunoTurma = alunosTurma.get(0);
            Usuario usu = (Usuario) Context.getCurrentUser();
            Arquivo arquivoNovo = new Arquivo(arquivo, alunoTurma.getTurma(),usu);
            Repository.save(arquivoNovo);
            alunoTurma.setQuantidadeAtividade(alunoTurma.getQuantidadeAtividade()+1);
            Repository.save(alunoTurma);
        }else {
            throw new SecurityException("Turma não encontrada");
        }
         
        return "go:home";
    }
    
    public String enviarConteudo() {
        
        List<Turma> turmas = Repository.query("ConsultarTurma", turma.getCodigoTurma());        
        
         if (turmas.size() == 1) {
            Turma turmaDoAluno = turmas.get(0);
            Usuario usu = (Usuario) Context.getCurrentUser();
            Arquivo arquivoNovo = new Arquivo(arquivo, turmaDoAluno,usu);
            Repository.save(arquivoNovo);
            turmaDoAluno.setQtdConteudos(turmaDoAluno.getQtdConteudos()+1);
            Repository.save(turmaDoAluno);
        }else {
            throw new SecurityException("Turma não encontrada");
        }
         
        return "go:br.edu.Arquivo@MeusConteudos";
    }
    
    public String novoConteudo(){
        return "go:br.edu.Arquivo@EnviarConteudo";
    }
    
    public String novaAtividade(){
        return "go:br.edu.Arquivo@EnviarAtividade";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
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
    
}
