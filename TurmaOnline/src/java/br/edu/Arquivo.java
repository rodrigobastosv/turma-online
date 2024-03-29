package br.edu;

import entities.annotations.PropertyDescriptor;
import entities.annotations.Views;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Past;
import lombok.Data;

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
    private byte[] arquivo;
    
    @Past
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio; 
    
    @ManyToOne(optional = false)
    @PropertyDescriptor(displayName = "Usuário")
    private Usuario usuario;
    
    @PropertyDescriptor(displayName = "Nome do arquivo")
    @Column(length = 100)
    private String nome;
    
    @Column
    @PropertyDescriptor(displayName = "Tamanho em bytes")
    private Long tamanho;
    
    @Transient
    @PropertyDescriptor(displayName = "Arquivo")
    private File fileArquivo;
    
    public Arquivo(){
    }
    
    public Arquivo(byte[] arquivo, Usuario usuario, File fileArquivo) {
        this.arquivo = arquivo;
        this.usuario = usuario;
        this.dataEnvio = new Date();
        this.fileArquivo = fileArquivo;
        this.nome = fileArquivo.getName();
        this.tamanho = fileArquivo.length();
    }       
}
