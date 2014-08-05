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
import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Vitor Rifane
 */
@Data
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
            members = "Turma[codigoTurma;nomeTurma;qtdAlunos,qtdConteudos;enviarEmail(),enviarConteudo(),goConteudosDaTurma();goAlunosDaTurma();]",
            namedQuery = "From br.edu.Turma t where t.usuario = :user",
            params = {
                @Param(name = "user", value = "#{context.currentUser}")},
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

    public Turma() {
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
//        GMailBuilder.enviarEmailTeste();
        return "go:home";
    }

    public String enviarConteudo() {
        Context.setValue("turmaContext", this);
        return "go:br.edu.Arquivo@EnviarConteudo";
    }

    //<editor-fold defaultstate="collapsed" desc="Métodos de Navegação">
    public String goAlunosDaTurma() {
        Context.setValue("idTurma", this.id);
        return "go:br.edu.AlunosTurma@AlunosDaTurma";
    }
    
    public String goConteudosDaTurma() {
        Context.setValue("alunoTurmaContext", this);
        return "go:br.edu.Arquivo@ConteudosTurma";
    }
//</editor-fold>
    
}
