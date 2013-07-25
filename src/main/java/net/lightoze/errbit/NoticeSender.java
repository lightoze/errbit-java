package net.lightoze.errbit;

import net.lightoze.errbit.api.Notice;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Vladimir Kulev
 */
public class NoticeSender {
    private static Charset UTF = Charset.forName("UTF-8");
    private String url;
    private Marshaller marshaller;

    public NoticeSender(String url) {
        this.url = url;
        try {
            marshaller = JAXBContext.newInstance(Notice.class).createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-type", "text/xml");
        connection.setRequestProperty("Accept", "text/xml, application/xml");
        connection.setRequestMethod("POST");
        return connection;
    }

    public void send(Notice notice) throws IOException {
        HttpURLConnection connection = createConnection();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF);
            marshaller.marshal(notice, writer);
            writer.close();
            if (connection.getResponseCode() != 200) {
                throw new IOException("Notifier response code " + connection.getResponseCode());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
