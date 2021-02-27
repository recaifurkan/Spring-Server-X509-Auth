import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Main {

  private static SSLSocketFactory getFactory(File pKeyFile, String pKeyPassword) throws NoSuchAlgorithmException,
      KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    KeyStore keyStore = KeyStore.getInstance("PKCS12");

    InputStream keyInput = new FileInputStream(pKeyFile);
    keyStore.load(keyInput, pKeyPassword.toCharArray());
    keyInput.close();

    keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());

    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }
    } };

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());

    return context.getSocketFactory();
  }

  public static void main(String[] args) throws IOException, UnrecoverableKeyException, KeyManagementException,
      NoSuchAlgorithmException, KeyStoreException, CertificateException {
    URL url = new URL("https://localhost:8443/user");
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setSSLSocketFactory(getFactory(new File("../server/store/clientBob.p12"), "changeit"));

    StringBuilder sb = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
      sb.append("\\n");
    }

    System.out.println(sb.toString());

  }
}