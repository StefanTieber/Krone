import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static ScheduledExecutorService scheduler;
    public static Clipboard clipboard;
    public static Session session;

    public static String currentSong;

    public static void main(String[] args) {
        scheduler = Executors.newScheduledThreadPool(1);
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("software.stefan.tieber@gmail.com", "esel7353");
            }
        });

        scheduler.scheduleAtFixedRate(Main::krone, 0, 5, TimeUnit.SECONDS);
    }

    private static void krone() {
        List<String> lines = readClipboard();
        if (lines.size() < 2) {
            System.out.println("could not resolve");
            return;
        }

        //Hang tight - Expanding search

        String song = lines.get(0);
        String artist = lines.get(1);
        String combined = artist + " - " + song;

        if (!combined.equals(currentSong)) {
            currentSong = combined;

            System.out.println(currentSong);

            if (isRelevantSong(artist, song)) {
                sendEmail(currentSong);
            }
        }
    }

    private static List<String> readClipboard() {
        try {
            String text = (String) clipboard.getData(DataFlavor.stringFlavor);

            return Arrays.stream(text.split("\\r?\\n"))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }

    private static boolean isRelevantSong(String artist, String song) {
        return artist.matches("(?i).*Ed\\s+Sheeran.*") && song.matches("(?i).*Shivers.*")
                || artist.matches("(?i).*Robin\\s+Schulz.*") && song.matches("(?i).*Young\\s+Right\\s+Now.*")
                || artist.matches("(?i).*Shakira.*") && song.matches("(?i).*Whenever\\W+Wherever.*");
    }

    private static void sendEmail(String song) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("software.stefan.tieber@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("stefantieber23@gmail.com"));
            message.setSubject(song);

            String msg = "Current song: " + song + "<br>" +
                    "<a href=\"tel:0771127711\">Call Kronehit</a>";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Successfully sent Email");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
