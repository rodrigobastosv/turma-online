/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import entities.Context;
import entities.Repository;
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
    @NamedQuery(name = "ConsultarTurmaArquivo",
               query = "  From Turma t"
                     + " Where t.codigoTurma = :codigoTurma ")})
@Views({
/**
 * Professor enviando conteúdo para a turma
 */
@View(name = "EnviarConteudo",
     title = "Enviar conteúdo",
   members = "Arquivo[Turma[#turma.codigoTurma];Arquivo[#arquivo];enviarConteudoAtividade()]",
  namedQuery = "Select new br.edu.Arquivo()",
  roles = "Professor"),
/**
 * Professor enviando conteúdo para a turma
 */
@View(name = "EnviarAtividade",
     title = "Enviar conteúdo",
   members = "Arquivo[Turma[#turma.codigoTurma];Arquivo[#arquivo];enviarConteudoAtividade()]",
  namedQuery = "Select new br.edu.Arquivo()",
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
    
    public String enviarConteudoAtividade() {
        
        List<Turma> turmas = Repository.query("ConsultarTurma", turma.getCodigoTurma());
        
         if (turmas.size() == 1) {
            Usuario usu = (Usuario) Context.getCurrentUser();
            Arquivo arquivoNovo = new Arquivo(arquivo, turmas.get(0),usu);
            Repository.save(arquivoNovo);
            //return "go:domain.User@Users";
        }else {
            throw new SecurityException("Turma não encontrada");
        }
         
        return "go:home";
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
