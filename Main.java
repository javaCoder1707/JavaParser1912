
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Result> results = new ArrayList<>();
        System.out.println("Parsing...");
        Document document = Jsoup.connect("https://yandex.ru/news").get();

        Elements storyTitle = document.getElementsByClass("story__title");

        storyTitle.forEach(page -> {
            Element a = page.child(0);
            String url = a.attr("href");
            String text = a.text();
            results.add(new Result(url, text));
        });

        StringBuilder stringBuilder = new StringBuilder();

//      results.forEach(System.out::println);
        results.forEach(x -> {
            stringBuilder.append("\n");
            stringBuilder.append(x.toString());
            stringBuilder.append("\n");
        });

        System.out.println(stringBuilder.toString());



        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mygmail@gmail.com", "password");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mygmail@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sentgmail@gmail.com"));
            message.setSubject("Mail Subject");


        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(stringBuilder.toString(), "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("Email sent successfully...");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
