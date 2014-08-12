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
import javax.persistence.Table;
import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import util.jsf.Types;

/**
 *
 * @author Vitor Rifane
 */
@Data
@Entity
@Table(name = "TURMAS")
@NamedQueries({
    //<editor-fold defaultstate="collapsed" desc="Turmas do Professor">
    @NamedQuery(name = "TurmasDoProfessor",
            query = "From Turma t where t = :id"),
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Obter Minhas Turmas">
    @NamedQuery(name = "MinhasTurmas",
            query = "From br.edu.Turma t where t.professor = :user")
    //</editor-fold>
})
@Views({    
    //<editor-fold defaultstate="collapsed" desc="Minhas Turmas">
    @View(name = "MinhasTurmas",
            title = "Minhas Turmas",
            header = "goCadastrarTurma()",
            members = "codigo,nome,'Alunos':goAlunosDaTurma(),'Conteudos':goConteudosDaTurma(),Ação[enviarEmailParaTurma(),enviarConteudo()]",
            namedQuery = "MinhasTurmas",
            params = {
                @Param(name = "user", value = "#{context.currentUser}")},
            template = "@TABLE+@PAGE",
            roles = "Professor"),
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Cadastrar Turma">
    @View(name = "CadastrarTurma",
            hidden = true,
            title = "Cadastrar Turma",
            members = "[#nome;cadastrarTurma()]",
            namedQuery = "Select new br.edu.Turma()",
            roles = "Professor")
//</editor-fold>
})
public class Turma implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 6)
    @PropertyDescriptor(displayName = "Código")
    private String codigo;

    @Column(length = 40)
    @NotEmpty(message = "Nome da turma não informado")
    private String nome;

    @Column(precision = 2)
    @PropertyDescriptor(displayName = "Qtde. Alunos")
    private Integer qtdAlunos = 0;

    @Column(precision = 2)
    @PropertyDescriptor(displayName = "Qtde. Conteúdos")
    private Integer qtdConteudos = 0;

    @ManyToOne(optional = false)
    private Usuario professor;        

    //<editor-fold defaultstate="collapsed" desc="Construtores">
    public Turma() {
    }

    public Turma(String nome, Usuario usuario) {
        this.nome = nome;
        this.professor = usuario;
        this.codigo = RandomStringUtils.randomAlphanumeric(6);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cadastrar Turma">
    public String cadastrarTurma() {
        Usuario prof = (Usuario) Context.getCurrentUser();
        Turma turma = new Turma(nome, prof);
        turma.setCodigo(RandomStringUtils.randomAlphanumeric(6));
        Repository.save(turma);
        
        return "go:br.edu.Turma@MinhasTurmas";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Enviar Email para a turma">
    public String enviarEmailParaTurma(
            @ParameterDescriptor(displayName = "Assunto") String assunto,
            @ParameterDescriptor(displayName = "Mensagem")
            @Editor(propertyType = PropertyType.MEMO) String mensagem) {
        //TODO pegar lista de email dos alunos da turma
        GMailBuilder.getInstance().
                addToMail("vitor.rifane@gmail.com").
                setSubject(assunto).
                setMessage(mensagem).
                sendMail();
        
        List<String> emailsAlunos = Repository.query("EmailsAlunos", id);
        //a forma é essa mesmo? enviar 1 email por vez?
        int i =0;
        for (String emailAluno : emailsAlunos) {
            GMailBuilder.getInstance().
                    addToMail(emailAluno).
                    setSubject(assunto).
                    setMessage(mensagem).
                    sendMail();
            i++;
        }
        return i+" emails enviados";
    }
    //</editor-fold>
   
    //<editor-fold defaultstate="collapsed" desc="Enviar Conteúdo">
    public String enviarConteudo() {
        Context.setValue("turmaContext", this);
        return "go:br.edu.ArquivosTurma@EnviarConteudo";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Métodos de Navegação">
    @ActionDescriptor(value = "Cadastrar uma turma")
    static public String goCadastrarTurma() {
        return "go:br.edu.Turma@CadastrarTurma";
    }

    @ActionDescriptor(value = "#{dataItem.qtdAlunos}", componenteType = Types.COMMAND_LINK)
    public String goAlunosDaTurma() {
        Context.setValue("idTurma", this.id);
        return "go:br.edu.AlunosTurma@AlunosDaTurma";
    }

    @ActionDescriptor(value = "#{dataItem.qtdConteudos}", componenteType = Types.COMMAND_LINK)
    public String goConteudosDaTurma() {
        Context.setValue("idTurma", this.id);
        return "go:br.edu.ArquivosTurma@ConteudosTurma";        
    }
//</editor-fold>

}
