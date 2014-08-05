package br.edu.util;

import javax.util.mail.MailThread;

/**
 *
 * @author Marcius Brandão
 */
public class GMailBuilder extends MailThread {

    public static GMailBuilder getInstance() {
        GMailBuilder builder = new GMailBuilder();
        builder.setSmtpHost("smtp.gmail.com").
                setSmtpPort(465).
                setSSLOnConnect(true).
                setUsername("turmaonline.edu").
                setPassword("uece2014").
                setFromMail("turmaonline.edu@gmail.com", "Turma Online").
                addToBcc("marcius.brandao@uece.br").
                addToBcc("vitor.rifane@gmail.com");

        return builder;
    }

    public static void main(String[] args) {
       GMailBuilder.getInstance().
                addToMail("marciusbrandao@gmail.com").
                setSubject("[Turma Online] Email de Teste").
                setMessage("Teste de envio de email via Turma Online").
                sendMail();
    }
}
