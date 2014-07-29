/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import entities.Context;
import entities.Repository;
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
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Vitor Rifane
 */


@Entity
@Table(name = "USUARIOS")
@NamedQueries({
    @NamedQuery(name = "AutenticarUsuario",
               query = "  From Usuario u"
                     + " Where u.email = :email "
                     + "   and u.senha = :senha "),
    @NamedQuery(name = "BuscarUsuario",
               query = "  From Usuario u"
                     + " Where u.email = :email ")})
@Views({
    /**
     * View de Entrar
     */
    @View(name = "Entrar",
         title = "Entrar",
       members = "[#email;#senha;entrar();]",
    namedQuery = "Select new br.edu.Usuario()",
         roles = "NOLOGGED"),
/**
     * Cadastro de Usuarios
     */
    @View(name = "CadastrarSe",
         title = "Cadastrar-se",
       members = "Usuario[#nome;"
               + "     #email;"
               + "     #perfis:2;"
               + "  salvar()]",
      namedQuery = "Select new br.edu.Usuario()",
      roles = "NOLOGGED"),
    /**
     * View de redefinir senha
     */
    @View(name = "RedefinirSenha",
         title = "Redefinir Senha",
       members = "[#email;redefinirSenha()]",
    namedQuery = "Select new br.edu.Usuario()",
         roles = "NOLOGGED"),
    /**
     * View de Sair
     */
    @View(name = "Sair",
         title = "Sair",
       members = "[[*email;"
               + "            *perfis;"
               + "            [sair()]]]",
    namedQuery = "From Usuario u Where u = :usuario",
        params = {@Param(name = "usuario", value = "#{context.currentUser}")},
         roles = "LOGGED")
    
})
public class Usuario implements Serializable {
    
    public enum Perfil { Professor, Aluno }
 
    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(length = 100)
    //@NotEmpty(message = "Nome do usuario não informado")
    private String nome;
    
    @Username
    @Column(length = 100, unique = true)    
    //@NotEmpty(message = "Informe o e-mail")
    private String email;
    
    /*@PropertyDescriptor(displayName = "Sou Professor")
    private boolean perfilProfessor;
    
    @PropertyDescriptor(displayName = "Sou Aluno")
    private boolean perfilAluno;*/
    
    @Column(length = 8)
    //@NotEmpty(message = "Enter the password")
    //@Type(type = "entities.security.Password")
    @PropertyDescriptor(secret = true, displayWidth = 25)    
    private String senha;
    
    @UserRoles
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch= FetchType.EAGER)
    private List<Perfil> perfis = new ArrayList<Perfil>();
    
    public Usuario(){
        //this.senha = RandomStringUtils.randomAlphanumeric(8);
    }
    
    public Usuario(String nome, String email, Perfil... perfis) {
        this.nome = nome;
        this.email = email;
        this.perfis.addAll(Arrays.asList(perfis));
        this.senha = RandomStringUtils.randomAlphanumeric(8);
    }
    
     // <editor-fold defaultstate="collapsed" desc="Autorização & Autenticação">    
    public String entrar() {        
        
        List<Usuario> usuarios = Repository.query("AutenticarUsuario", email, senha);
        if (usuarios.size() == 1) {
            Context.setCurrentUser(usuarios.get(0));
        }else {
            throw new SecurityException("Usuário/Senha incorreto(s)!");
        }
                    
        return "go:home";
    }
    
    static public String sair() {
        Context.clear();
        return "go:br.edu.Usuario@Entrar";
    }// </editor-fold>
    
    public String cadastrar(){
        List<Usuario> usuarios = Repository.query("BuscarUsuario", email);        
        if (usuarios.size() <= 0) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setPerfis(perfis);
            novoUsuario.setSenha(RandomStringUtils.randomAlphanumeric(8));
            Repository.save(novoUsuario);
            //enviar email com a sua senha
        }else {
            throw new SecurityException("Usuário já cadastrado com o e-mail informado!");
        }
        return "go:br.edu.Usuario@Entrar";
    }
    
    public String salvar(){
        
        List<Usuario> usuarios = Repository.query("BuscarUsuario", email);        
        if (usuarios.size() <= 0) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setEmail(email);
            novoUsuario.setNome(nome);
            novoUsuario.setPerfis(perfis);
            novoUsuario.setSenha(RandomStringUtils.randomAlphanumeric(8));
            Repository.save(novoUsuario);
            //enviar email com a sua senha
        }else {
            throw new SecurityException("Usuário já cadastrado com o e-mail informado!");
        }
        return "go:br.edu.Usuario@Entrar";
    }
    public String EsqueciSenha(){
        return "go:br.edu.Usuario@RedefinirSenha";
    }
    
    public String redefinirSenha(){
        List<Usuario> usuarios = Repository.query("BuscarUsuario", email);
        if (usuarios.size() == 1) {
            Usuario usu = usuarios.get(0);
            usu.setSenha(RandomStringUtils.randomAlphanumeric(8));
            Repository.save(usu);
            //enviar email com a nova senha
        }else {
            throw new SecurityException("Usuário/Senha incorreto(s)!");
        }
                    
        return "go:home";
    }
    
     @Override
    public String toString() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public boolean isPerfilProfessor() {
        return perfilProfessor;
    }

    public void setPerfilProfessor(boolean perfilProfessor) {
        this.perfilProfessor = perfilProfessor;
    }

    public boolean isPerfilAluno() {
        return perfilAluno;
    }

    public void setPerfilAluno(boolean perfilAluno) {
        this.perfilAluno = perfilAluno;
    }*/
    

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getSenha() {
        return senha;
    }

    public List<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<Perfil> perfis) {
        this.perfis = perfis;
    }
    
    
}
