package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.ActionDescriptor;
import entities.annotations.Param;
import entities.annotations.PropertyDescriptor;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Vitor Rifane
 */
@Data
@Entity
@NamedQueries({
//<editor-fold defaultstate="collapsed" desc="Obter e-mail do Aluno da Turma">
@NamedQuery(name = "ObterMeusConteudos",
        query = "From ArquivosTurma atm where atm.arquivo.usuario = :user"), 
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Obter Conteúdos da Turma">
    @NamedQuery(name = "ObterConteudosTurma",
            query = "From ArquivosTurma atm where atm.turma.id = :idTurma")
//</editor-fold>
})

@Views({
//<editor-fold defaultstate="collapsed" desc="Meus Conteúdos">
    @View(name = "MeusConteudos",
            title = "Meus conteúdos",
            members = "ArquivosTurma[turma.nome;"
                    + "              arquivo.nome,arquivo.tamanho,descricao;"
                    + "              arquivo.arquivo;"
                    + "              arquivo.dataEnvio];",
            namedQuery = "ObterMeusConteudos",
            params = {@Param(name = "user", value = "#{context.currentUser}")},
            template = "@TABLE+@PAGE",
            roles = "Professor",
            hidden = true),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Conteúdos da turma">
    @View(name = "ConteudosTurma",
            title = "Conteúdos da turma",
            members = "'Turma':turma.nome;arquivo.nome,arquivo.tamanho;descricao;arquivo.dataEnvio;Ctrl.DAO.deleteRow()",
            namedQuery = "ObterConteudosTurma",
            params = {@Param(name = "idTurma", value = "#{idTurma}")},
            template = "@TABLE+@PAGER",
            roles = "Professor,Aluno",
            hidden = true),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Enviar Conteúdo">
    @View(name = "EnviarConteudo",
            title = "Enviar conteúdo",
            members = "Arquivo[#arquivo.fileArquivo;#descricao;enviarConteudo()]",
            namedQuery = "Select new br.edu.ArquivosTurma()",
            roles = "Professor",
            hidden = true),
//</editor-fold>
})
public class ArquivosTurma implements Serializable {
    
    @Id
    @GeneratedValue    
    @PropertyDescriptor(index = 1, hidden = true)
    private Integer id;
    
    @ManyToOne(optional = false)
    private Arquivo arquivo = new Arquivo();
    
    @ManyToOne(optional = false)
    private Turma turma = new Turma();
    
    @Column(length = 100)
    @NotEmpty(message = "Descrição do arquivo não informada")
    @PropertyDescriptor(displayWidth = 60, displayName = "Descrição")
    private String descricao;
    
    public ArquivosTurma(){        
    }
    
    public ArquivosTurma(Arquivo arquivo, Turma turma, String descricao){
        this.arquivo = arquivo;
        this.turma = turma;
        this.descricao = descricao;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Enviar Conteúdo">
    @ActionDescriptor(displayName = "Enviar Conteúdo")
    public String enviarConteudo() throws FileNotFoundException, IOException {
        
        Turma turmaContext = (Turma) Context.getValue("turmaContext");
        
        Usuario usu = (Usuario) Context.getCurrentUser();

        FileInputStream fileInputStream = new FileInputStream(arquivo.getFileArquivo());
        try {
            arquivo.setArquivo(new byte[ (int) arquivo.getFileArquivo().length()]);
            fileInputStream.read(arquivo.getArquivo());
            fileInputStream.close();
        } catch (IOException e){            
            throw new SecurityException("Não foi possível carregar o arquivo!");
        }
        Arquivo arquivoNovo = new Arquivo(arquivo.getArquivo(), usu, arquivo.getFileArquivo());
        Repository.save(arquivoNovo);
        turmaContext.setQtdConteudos(turmaContext.getQtdConteudos()+1);
        Repository.save(turmaContext);
        ArquivosTurma arquivoTurmaNovo = new ArquivosTurma(arquivoNovo, turmaContext, descricao);
        Repository.save(arquivoTurmaNovo);
        
        return "go:br.edu.ArquivosTurma@MeusConteudos";
    }        
    //</editor-fold>   
}
