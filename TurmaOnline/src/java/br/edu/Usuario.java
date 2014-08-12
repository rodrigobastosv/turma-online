package br.edu;

import br.edu.util.GMailBuilder;
import entities.Context;
import entities.Repository;
import entities.annotations.ActionDescriptor;
import entities.annotations.Param;
import entities.annotations.PropertyDescriptor;
import entities.annotations.UserRoles;
import entities.annotations.Username;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import util.jsf.Types;

/**
 *
 * @author Vitor Rifane
 */
@Data
@EqualsAndHashCode(of = {"email"})
@Entity
@Table(name = "USUARIOS")
@NamedQueries({
//<editor-fold defaultstate="collapsed" desc="Autenticar Usuário">
    @NamedQuery(name = "AutenticarUsuario",
            query = "  From Usuario u"
                    + " Where u.email = :email "
                    + "   and u.senha = :senha "),
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Buscar Usuário">
    @NamedQuery(name = "BuscarUsuario",
            query = "  From Usuario u"
                    + " Where u.email = :email ")})
//</editor-fold>
@Views({
    //<editor-fold defaultstate="collapsed" desc="Entrar">
    @View(name = "Entrar",
            title = "Entrar",
            members = ""
            + "[#email;"
            + " #senha;"
            + " entrar();"
            + " [goCadastrarSe(),goEsqueciSenha()]]",
            namedQuery = "Select new br.edu.Usuario()",
            roles = "NOLOGGED"),
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="CadastrarSe">
    @View(name = "CadastrarSe",
            title = "Cadastrar-se",
            members = ""
            + "Usuario[#nome;"
            + "        #email;"
            + "        '':#perfis;"
            + "        cadastrarSe()]",
            namedQuery = "Select new br.edu.Usuario()",
            roles = "NOLOGGED",
            hidden = true),
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="RedefinirSenha">
    @View(name = "RedefinirSenha",
            title = "Redefinir Senha",
            members = "[#email;redefinirSenha()]",
            namedQuery = "Select new br.edu.Usuario()",
            roles = "NOLOGGED",
            hidden = true),
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Sair">
    @View(name = "Sair",
            title = "Sair",
            members = ""
            + "[*email;"
            + " *perfis;"
            + " sair()]",
            namedQuery = "From Usuario u Where u = :usuario",
            params = {
                @Param(name = "usuario", value = "#{context.currentUser}")},
            roles = "LOGGED")
//</editor-fold>
})
public class Usuario implements Serializable {

    public enum Perfil {

        Professor, Aluno
    }

    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(length = 100)
    @NotEmpty(message = "Nome do usuario não informado")
    @PropertyDescriptor(displayWidth = 60)
    private String nome;

    @Username
    @Column(length = 100, unique = true)
    @NotEmpty(message = "Informe o e-mail")
    @PropertyDescriptor(displayWidth = 25)
    private String email;

    @Column(length = 32)
    @NotEmpty(message = "Enter the password")
    @Type(type = "entities.security.Password")
    @PropertyDescriptor(secret = true, displayWidth = 25)
    private String senha;

    @UserRoles
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Perfil> perfis = new ArrayList<Perfil>();

    //<editor-fold defaultstate="collapsed" desc="Construtores">
    public Usuario() {
    }

    public Usuario(String nome, String email, Perfil... perfis) {
        this.nome = nome;
        this.email = email;
        this.perfis.addAll(Arrays.asList(perfis));
        this.senha = RandomStringUtils.randomAlphanumeric(8);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Entrar">
    @ActionDescriptor(preValidate = false)
    public String entrar() {
        String telaRetorno = "go:home";
        
        Usuario usuario = Repository.queryUnique("AutenticarUsuario", email, senha);
        if (usuario != null) {
            Context.setCurrentUser(usuario);
            if (usuario.getPerfis().contains(Perfil.Professor)){
                telaRetorno = "go:br.edu.Turma@MinhasTurmas";
            } else {
                telaRetorno = "go:br.edu.AlunosTurma@MinhasTurmasAluno";
            }
        } else {
            throw new SecurityException("Usuário/Senha incorreto(s)!");
        }
        
        return telaRetorno;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sair">
    public String sair() {
        Context.clear();
        return "go:br.edu.Usuario@Entrar";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cadastrar-se">
    @ActionDescriptor(preValidate = false, value = "Cadastrar-se", refreshView = true)
    public String cadastrarSe() {
        String camposNaoInformados = "";
        if(email.isEmpty()){
            camposNaoInformados += "\r\n E-mail";
        }
        
        if(nome.isEmpty()){
            camposNaoInformados += "\r\n Nome";
        }
        
        if(perfis.isEmpty()){
            camposNaoInformados += "\r\n Perfil";
        }
        
        if(!camposNaoInformados.isEmpty()){
            throw new SecurityException("Informe os campos abaixo: " +camposNaoInformados);
        }
        
        email = email.toLowerCase().trim();
        Usuario usuario = Repository.queryUnique("BuscarUsuario", email);
        if (usuario != null) {
            throw new SecurityException("Usuário já cadastrado com o e-mail informado!");
        }
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(email);
        novoUsuario.setNome(nome);
        novoUsuario.setPerfis(perfis);
        novoUsuario.setSenha(RandomStringUtils.randomAlphanumeric(8));
        Repository.save(novoUsuario);
        
        //enviar email com a sua senha
        GMailBuilder.getInstance().
                addToMail(email).
                setSubject("[TurmaOnLine]Cadastro").
                setMessage("Sua conta: " + novoUsuario + "<br> e senha: " + novoUsuario.getSenha()).
                sendMail();
        
        return "Uma senha foi enviada para seu email";
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Redefinir Senha">
    @ActionDescriptor(preValidate = false)
    public String redefinirSenha() {
        email = email.toLowerCase().trim();
        Usuario usuario = Repository.queryUnique("BuscarUsuario", email);
        if (usuario != null) {
            usuario.setSenha(RandomStringUtils.randomAlphanumeric(8));
            Repository.save(usuario);
            
            //enviar email com a nova senha
            GMailBuilder.getInstance().
                    addToMail(email).
                    setSubject("[TurmaOnLine]Nova Senha").
                    setMessage("Sua senha:" + usuario.getSenha()).
                    sendMail();
        } else {
            throw new SecurityException("Usuário/Senha incorreto(s)!");
        }
        
        return "go:home";
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Métodos de Navegação">
    @ActionDescriptor(componenteType = Types.COMMAND_LINK, value = "Cadastrar-se", immediate = true)
    public String goCadastrarSe() {
        return "go:br.edu.Usuario@CadastrarSe";
    }

    @ActionDescriptor(componenteType = Types.COMMAND_LINK, value = "Esqueci minha senha", immediate = true)
    public String goEsqueciSenha() {
        return "go:br.edu.Usuario@RedefinirSenha";
    }
    //</editor-fold>

    @Override
    public String toString() {
        return email;
    }

}
