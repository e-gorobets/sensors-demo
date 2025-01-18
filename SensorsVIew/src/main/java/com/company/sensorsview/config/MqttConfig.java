package com.company.sensorsview.config;
import com.company.sensorsview.app.SensorDataListener;
import io.github.cdimascio.dotenv.Dotenv;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
public class MqttConfig {
    @Autowired
    private DataManager dataManager;

    @Autowired
    SystemAuthenticator authenticator;

    private static final Dotenv dotenv = Dotenv.load();

    private static final String TRUSTED_ROOT;

    static {
        try {
            TRUSTED_ROOT = Files.readString(Path.of(dotenv.get("CERT_FILE")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public MqttClient mqttClient() throws Exception {
        String broker = dotenv.get("MQTT_BROKER");
        String clientId = dotenv.get("CLIENT_ID_LISTENER");
        int keepAliveInterval = 30;
        /*
        От keepAliveInterval значения зависит частота отправки команд PINGREQ.
        Чем меньше значение keepAliveInterval, тем быстрее клиент понимает,
        что соединение было разорвано нештатным путем.
        Но для этого чаще отправляются тарифицируемые команды PINGREQ.
         */

        MqttClient mqttClient = new MqttClient(broker, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(dotenv.get("USER_NAME"));
        options.setPassword(dotenv.get("PASSWORD").toCharArray());
        options.setKeepAliveInterval(keepAliveInterval);

        SSLSocketFactory sslSocketFactory = getSocketFactory();
        options.setSocketFactory(sslSocketFactory);

        options.setCleanSession(true);

        // Установка обработчика асинхронных событий
        mqttClient.setCallback(new SensorDataListener(dataManager, authenticator));

        mqttClient.connect(options);
        return mqttClient;
    }

    private SSLSocketFactory getSocketFactory() throws Exception {
        InputStream is = new ByteArrayInputStream(TRUSTED_ROOT.getBytes(StandardCharsets.UTF_8));
        CertificateFactory cFactory = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate) cFactory.generateCertificate(is);

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
        tks.load(null); // Вам не нужно загружать из файла экземпляр класса `KeyStore`.
        tks.setCertificateEntry("caCert", caCert);
        tmf.init(tks);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        return ctx.getSocketFactory();
    }
}
