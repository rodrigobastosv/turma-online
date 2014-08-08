package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.ActionDescriptor;
import entities.annotations.Param;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.File;
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
import util.jsf.Types;

/**
 *
 * @author Vitor Rifane
 */
@Data
@Entity
@NamedQueries({
    @NamedQuery(name = "ConsultarAlunoTurma",
               query = "  From AlunosTurma at"
                     + " Where at.turma.codigo = :codigoTurma ")})
@Views({
})
public class Arquivo implements Serializable {
    
    @Id
    @GeneratedValue    
    private Integer id;       
        
    @Lob
    private File arquivo;
    
    @Past
    @Temporal(TemporalType.DATE)
    private Date dataEnvio; 
    
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    public Arquivo(){
    }
    
    public Arquivo(File arquivo, Usuario usuario) {
        this.arquivo = arquivo;
        this.usuario = usuario;
        this.dataEnvio = new Date();
    }       
    
//    @ActionDescriptor(componenteType = Types.LABEL, refreshView = true )
    public String dadosDoArquivo(){
        String retorno = "";
        retorno = retorno + "Nome do arquivo = "+ arquivo.getName();
        retorno = retorno + " \r\n Tamanho (bytes) = "+ arquivo.length();
        return retorno;
    }
    
}
