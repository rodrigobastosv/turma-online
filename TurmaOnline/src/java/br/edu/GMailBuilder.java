/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu;

import javax.util.mail.MailThread;

/**
 *
 * @author Vitor Rifane
 */
public class GMailBuilder extends MailThread {

    public static GMailBuilder getInstance() {
        GMailBuilder builder = new GMailBuilder();
        builder.setSmtpHost("smtp.gmail.com").
                setSmtpPort(465).
                setSSLOnConnect(true).
                setUsername("turmaonline.edu").
                setPassword("uece2014");

        return builder;
    }

    //public static void main(String[] args) {
    public static void enviarEmailTeste() {
        GMailBuilder.getInstance().
                setFromMail("turmaonline.edu@gmail.com", "Turma Online").
                addToBcc("vitor.rifane@gmail.com").
                addToMail("vitor_rifane@hotmail.com").
                addToMail("rifane.listas@gmail.com").
                setSubject("[GMailBuilder] Envio de email pelo Gmail").
                setMessage("Teste de envio de email via GMail para outro email").
                sendMail();
    }
}
