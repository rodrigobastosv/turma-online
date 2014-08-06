package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.Param;
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
                     + " Where t.codigo = :codigoTurma "),
    //<editor-fold defaultstate="collapsed" desc="Obter e-mails dos Alunos">
    @NamedQuery(name = "EmailsAlunos",
            query = " Select atm.usuario.email From AlunosTurma atm where atm.turma.id = :idTurma"),
    //</editor-fold>
})
@Views({
/**
 * Turmas do Aluno
 */
@View(name = "MinhasDisciplinas",
     title = "Minhas Disciplinas",
   members = "AlunosTurma[turma.codigo;"
           + "  turma.nome;"
           + "  quantidadeAtividade, quantidadeFalta;"
           + "  enviarEmail(),enviarAtividade();"
           + "  alunosDaTurma(),conteudosDaTurma();]",
   namedQuery = "From br.edu.AlunosTurma atm where atm.usuario = :user",
   params = {@Param(name = "user", value = "#{context.currentUser}")},
  template = "@TABLE+@PAGER",
  roles = "Aluno"),
/**
 * Alunos da turma
 */
@View(name = "AlunosDaTurma",
     title = "Alunos da turma",
   members = "AlunosTurma[turma.nome,turma.codigo;"
           + "  usuario.nome;"
           + "  quantidadeAtividade, quantidadeFalta;"
           + "  enviarEmail(),enviarAtividade();]",
   namedQuery = "From br.edu.AlunosTurma atm where atm.turma.id = :idTurma",
   params = {@Param(name = "idTurma", value = "#{idTurma}")},
  template = "@TABLE+@PAGER",
  roles = "Professor,Aluno",
  hidden = true),
        
/**
 * Aluno se cadastrando na turma
 */
@View(name = "CadastrarNaTurma",
     title = "Cadastrar-me na turma",
     members = "AlunosTurma[Turma[#turma.codigo];#matricula;cadastrarNaTurma()]",
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
        
        List<Turma> turmas = Repository.query("ConsultarTurma", turma.getCodigo());
        
                
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
    
    public String enviarAtividade() {
        Context.setValue("alunoTurmaContext", this);
        return "go:br.edu.Arquivo@EnviarAtividade";
    }
    
    public String alunosDaTurma() {
        Context.setValue("turmaContext", this.getTurma());
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
