package br.edu;

import br.edu.util.GMailBuilder;
import entities.Context;
import entities.Repository;
import entities.annotations.ActionDescriptor;
import entities.annotations.Editor;
import entities.annotations.Param;
import entities.annotations.ParameterDescriptor;
import entities.annotations.PropertyDescriptor;
import entities.annotations.View;
import entities.annotations.Views;
import entities.descriptor.PropertyType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.Data;
import util.jsf.Types;

/**
 *
 * @author Vitor Rifane
 */
@Data
@Entity
@NamedQueries({
//<editor-fold defaultstate="collapsed" desc="Consultar Turma">
@NamedQuery(name = "ConsultarTurma",
        query = "  From Turma t"
                + " Where t.codigo = :codigoTurma "),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter e-mails dos Alunos">
@NamedQuery(name = "EmailsAlunos",
        query = " Select atm.usuario.email From AlunosTurma atm where atm.turma.id = :idTurma"),   
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter Minhas Disciplinas">
@NamedQuery(name = "MinhasDisciplinas",
        query = "From br.edu.AlunosTurma atm where atm.usuario = :user"),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter Alunos da Turma">
@NamedQuery(name = "ObterAlunosDaTurma",
        query = "From br.edu.AlunosTurma atm where atm.turma.id = :idTurma"),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter e-mail do Professor da Turma">
@NamedQuery(name = "ObterEmailProfessorTurma",
        //query = " Select atm.turma.usuario.email From br.edu.AlunosTurma atm where atm.turma.id = :idTurma"),
        query = " Select t.professor.email From Turma t where t.id = :idTurma"),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter e-mail do Aluno da Turma">
@NamedQuery(name = "EmailAluno",
        query = "Select atm.usuario.email From br.edu.AlunosTurma atm where atm.usuario.id = :idUsuario")
//</editor-fold>
})
@Views({
//<editor-fold defaultstate="collapsed" desc="Minhas Disciplinas">
    @View(name = "MinhasDisciplinas",
            title = "Minhas Disciplinas",
            header = "goCadastrarSeNumaTurma()",
            members = "turma.codigo,turma.nome, quantidadeFalta, 'Alunos':goAlunosDaTurma(), 'Conteudos':goConteudosDaTurma(),Ação[enviarEmailParaProfessor()]",
            namedQuery = "MinhasDisciplinas",
            params = {
                @Param(name = "user", value = "#{context.currentUser}")},
            template = "@TABLE+@PAGE",
            roles = "Aluno"),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Alunos da turma">
    @View(name = "AlunosDaTurma",
            title = "Alunos da Turma",
            //header = "turma.nome",
            members = "turma.nome,matricula,usuario.nome, quantidadeFalta, Ação[enviarEmailParaAluno()]",
            namedQuery = "ObterAlunosDaTurma",
            params = {@Param(name = "idTurma", value = "#{idTurma}")},
            template = "@TABLE+@PAGE",
            roles = "Professor",
            hidden = true),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Alunos da disciplina">
    @View(name = "AlunosDaDisciplina",
            title = "Alunos da Disciplina",
            //header = "turma.nome",
            members = "turma.nome,matricula,usuario.nome, quantidadeFalta",
            namedQuery = "ObterAlunosDaTurma",
            params = {@Param(name = "idTurma", value = "#{idTurma}")},
            template = "@TABLE+@PAGE",
            roles = "Aluno",
            hidden = true),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Aluno se cadastrando na turma">
@View(name = "CadastrarNaTurma",
        title = "Cadastrar-me na turma",
        members = "#turma.codigo;"
                + "#matricula;"
                + "cadastrarNaTurma()",
        namedQuery = "Select new br.edu.AlunosTurma()",
        roles = "Aluno",
        hidden = true)
//</editor-fold>
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
    
    public AlunosTurma(){
        this.usuario = (Usuario) Context.getCurrentUser();
        this.quantidadeFalta = 0;
        this.quantidadeAtividade = 0;
    }
    
    public AlunosTurma(Turma turma, Usuario usuario, String matricula) {
        this.turma = turma;
        this.usuario = usuario;
        this.matricula = matricula;
        this.quantidadeFalta = 0;
        this.quantidadeAtividade = 0;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Cadastro do aluno na turma">
    public String cadastrarNaTurma() {
        
        Turma turmaDoAluno = Repository.queryUnique("ConsultarTurma", turma.getCodigo());
        
        
        if (turmaDoAluno != null) {
            Usuario usu = (Usuario) Context.getCurrentUser();
            AlunosTurma alunoTurma = new AlunosTurma(turmaDoAluno,usu, matricula);
            Repository.save(alunoTurma);
            turmaDoAluno.setQtdAlunos(turmaDoAluno.getQtdAlunos()+1);
            Repository.save(turmaDoAluno);
        }else {
            throw new SecurityException("Turma não encontrada");
        }
        
        return "go:br.edu.AlunosTurma@MinhasDisciplinas";
    }
//</editor-fold>
    
    
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
        return "go:br.edu.ArquivosTurma@ConteudosTurma";
    }
    
    //<editor-fold defaultstate="collapsed" desc="Enviar email para o Professor">
    @ActionDescriptor(componenteType = Types.COMMAND_BUTTON )
    public String enviarEmailParaProfessor(
            @ParameterDescriptor(displayName = "Assunto") String assunto,
            @ParameterDescriptor(displayName = "Mensagem")
            @Editor(propertyType = PropertyType.MEMO) String mensagem) {
        
        String emailProfessor = Repository.queryUnique("ObterEmailProfessorTurma", turma.getId());
        this.enviarEmail(emailProfessor, assunto, mensagem);
        
        return "E-mails enviado para o professor";
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Enviar email para Aluno">
    @ActionDescriptor(componenteType = Types.COMMAND_BUTTON )
    public String enviarEmailParaAluno(
            @ParameterDescriptor(displayName = "Assunto") String assunto,
            @ParameterDescriptor(displayName = "Mensagem")
            @Editor(propertyType = PropertyType.MEMO) String mensagem) {
        
        String emailAluno = Repository.queryUnique("EmailAluno", usuario.getId());;
        this.enviarEmail(emailAluno, assunto, mensagem);
        
        return "E-mails enviado para o aluno";
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Enviar email genérico">
    private void enviarEmail(String email, String assunto, String mensagem){
        GMailBuilder.getInstance().
                addToMail("vitor.rifane@gmail.com").
                addToMail(email).
                setSubject(assunto).
                setMessage(mensagem).
                sendMail();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="enviar email pra turma">
    public String enviarEmailParaTurma(
            @ParameterDescriptor(displayName = "Assunto") String assunto,
            @ParameterDescriptor(displayName = "Mensagem")
            @Editor(propertyType = PropertyType.MEMO) String mensagem) {
        //TODO pegar lista de email dos alunos da turma
        this.enviarEmail("vitor.rifane@gmail.com", assunto, mensagem);
        
        List<String> emailsAlunos = Repository.query("EmailsAlunos", id);
        //a forma é essa mesmo? enviar 1 email por vez?
        int i =0;
        for (String emailAluno : emailsAlunos) {
            this.enviarEmail(emailAluno, assunto, mensagem);
            i++;
        }
        return i+" emails enviados";
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Métodos de Navegação">
    @ActionDescriptor(value = "Cadastrar-se numa turma")
    static public String goCadastrarSeNumaTurma() {
        return "go:br.edu.AlunosTurma@CadastrarNaTurma";
    }

    @ActionDescriptor(value = "#{dataItem.turma.qtdAlunos}", componenteType = Types.COMMAND_LINK)
    public String goAlunosDaTurma() {
        Context.setValue("idTurma", this.turma.getId());
        return "go:br.edu.AlunosTurma@AlunosDaDisciplina";
    }

    @ActionDescriptor(value = "#{dataItem.turma.qtdConteudos}", componenteType = Types.COMMAND_LINK)
    public String goConteudosDaTurma() {
        Context.setValue("idTurma", this.turma.getId());
        return "go:br.edu.ArquivosTurma@ConteudosTurma";
    }
//</editor-fold>    
}
