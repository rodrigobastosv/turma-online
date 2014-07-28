/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import entities.Context;
import entities.Repository;
import entities.annotations.Param;
import entities.annotations.ParameterDescriptor;
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
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Vitor Rifane
 */


@Entity
@Table(name = "USUARIOS")
@NamedQueries({
    @NamedQuery(name = "Authentication",
               query = "  From Usuario u"
                     + " Where u.email = :email "
                     + "   and u.senha = :senha ")})
@Views({
    /**
     * View de Login
     */
    @View(name = "Login",
         title = "Login",
       members = "[#email;#senha;login();novoUsuario()]",
    namedQuery = "Select new br.edu.Usuario()",
         roles = "NOLOGGED"),
    /**
     * View de Logout
     */
    @View(name = "Logout",
         title = "Logout",
       members = "[[*email;"
               + "            *perfis;"
               + "            [newPassword(),logout()]]]",
    namedQuery = "From Usuario u Where u = :usuario",
        params = {@Param(name = "usuario", value = "#{context.currentUser}")},
         roles = "LOGGED"),
    /**
     * Cadastro de Usuarios
     */
    @View(name = "Usuarios",
         title = "Usuarios",
       members = "Usuario[nome;"
               + "     email;"
               + "     perfis:2]",
      template = "@CRUD+@PAGER")
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
    @NotEmpty(message = "Informe o e-mail")
    private String email;
    
    /*@PropertyDescriptor(displayName = "Sou Professor")
    private boolean perfilProfessor;
    
    @PropertyDescriptor(displayName = "Sou Aluno")
    private boolean perfilAluno;*/
    
    @Column(length = 8)
    @NotEmpty(message = "Enter the password")
    //@Type(type = "entities.security.Password")
    @PropertyDescriptor(secret = true, displayWidth = 25)    
    private String senha;
    
    @UserRoles
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch= FetchType.EAGER)
    private List<Perfil> perfis = new ArrayList<Perfil>();
    
    public Usuario(){
        this.senha = RandomStringUtils.randomAlphanumeric(8);
    }
    
    public Usuario(String nome, String email, Perfil... perfis) {
        this.nome = nome;
        this.email = email;
        this.perfis.addAll(Arrays.asList(perfis));
        this.senha = RandomStringUtils.randomAlphanumeric(8);
    }
    
     // <editor-fold defaultstate="collapsed" desc="Autorização & Autenticação">    
    public String login() {        
        
        List<Usuario> usuarios = Repository.query("Authentication", email, senha);
        if (usuarios.size() == 1) {
            Context.setCurrentUser(usuarios.get(0));
        }else {
            throw new SecurityException("Username/Password invalid!");
        }
                    
        return "go:home";
    }
    
    static public String logout() {
        Context.clear();
        return "go:br.edu.Usuario@Login";
    }// </editor-fold>
    
    public String novoUsuario(){
        return "go:br.edu.Usuario@Usuarios";
    }
    public String newPassword(){
//     public String newPassword(
//            @ParameterDescriptor(displayName = "New Password", 
//                                      secret = true, 
//                                    required = true) 
//            String newPassword,
//            @ParameterDescriptor(displayName = "Confirm password", 
//                                      secret = true, 
//                                    required = true) 
//            String rePassword) {
//        if (newPassword.equals(rePassword)) {
//            this.setPassword(newPassword);            
//            Repository.save(this);
//            return "Password changed successfully!";
//        } else {
//            throw new SecurityException("The passwords are different!");
//        }
        return "";
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
