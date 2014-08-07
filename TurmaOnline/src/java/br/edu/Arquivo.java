package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.Param;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.Serializable;
import java.util.Date;
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
import lombok.Data;

/**
 *
 * @author br0600106533
 */
@Data
@Entity
@NamedQueries({
    @NamedQuery(name = "ConsultarAlunoTurma",
               query = "  From AlunosTurma at"
                     + " Where at.turma.codigo = :codigoTurma ")})
@Views({
/**
 * Professor enviando conteúdo para a turma TESTE
 */
@View(name = "EnviarConteudo",
     title = "Enviar conteúdo",
    members = "Arquivo[#arquivo;enviarConteudo()]",
    namedQuery = "Select new br.edu.Arquivo()",
    roles = "Professor",
    hidden = true),
/**
 * Aluno enviando conteúdo para a turma
 */
//apagar essa view
@View(name = "EnviarAtividade",
     title = "Enviar atividade",
    members = "Arquivo[#arquivo;enviarAtividade()]",
    namedQuery = "Select new br.edu.Arquivo()",
    roles = "Aluno",
    hidden = true),
/**
* Minhas Turmas
*/
@View(name = "MeusConteudos",
     title = "Meus conteúdos",
    members = "Arquivo[turma.nome;arquivo;dataEnvio]",
    namedQuery = "From br.edu.Arquivo a where a.usuario = :user",//TODO criar namedQuery
    params = {@Param(name = "user", value = "#{context.currentUser}")},
    template = "@TABLE+@PAGE",
    roles = "Professor"),
/**
* Minhas Turmas
*/
//apagar essa view
@View(name = "MinhasAtividades",
     title = "Minhas atividades",
    members = "Arquivo[turma.nome;arquivo;dataEnvio;]",
    namedQuery = "From br.edu.Arquivo a where a.usuario = :user",//TODO criar namedQuery
    params = {@Param(name = "user", value = "#{context.currentUser}")},
    template = "@TABLE+@PAGE",
    roles = "Aluno",
    hidden = true),
/**
* Conteúdos da turma
*/
@View(name = "ConteudosTurma",
     title = "Conteúdos da turma",
    members = "turma.nome;arquivo;dataEnvio",
    namedQuery = "From br.edu.Arquivo a where a.turma.id = :idTurma",//TODO criar namedQuery
    params = {@Param(name = "idTurma", value = "#{idTurma}")},
    template = "@TABLE+@PAGE",
    roles = "Professor,Aluno",
    hidden = true)
})
public class Arquivo implements Serializable {
    
    @Id
    @GeneratedValue    
    private Integer id;
    
    @ManyToOne(optional = false)
    private Turma turma = new Turma();
        
    @Lob
    private byte[] arquivo;
    
    @Past
    @Temporal(TemporalType.DATE)
    private Date dataEnvio; 
    
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
        
        
        AlunosTurma alunoTurmaContext = (AlunosTurma) Context.getValue("alunoTurmaContext");
        Usuario usu = (Usuario) Context.getCurrentUser();
        Arquivo arquivoNovo = new Arquivo(arquivo, alunoTurmaContext.getTurma(),usu);
        Repository.save(arquivoNovo);
        alunoTurmaContext.setQuantidadeAtividade(alunoTurmaContext.getQuantidadeAtividade()+1);
        Repository.save(alunoTurmaContext);              
         
        return "go:br.edu.Arquivo@MinhasAtividades";
    }
    
    public String enviarConteudo() {
                
        Turma turmaContext = (Turma) Context.getValue("turmaContext");
                
        Usuario usu = (Usuario) Context.getCurrentUser();
        Arquivo arquivoNovo = new Arquivo(arquivo, turmaContext,usu);
        Repository.save(arquivoNovo);
        turmaContext.setQtdConteudos(turmaContext.getQtdConteudos()+1);
        Repository.save(turmaContext);       
         
        return "go:br.edu.Arquivo@MeusConteudos";
    }    
    
}
