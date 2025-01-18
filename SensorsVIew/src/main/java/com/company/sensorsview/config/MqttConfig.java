package com.company.sensorsview.config;
import com.company.sensorsview.app.SensorDataListener;
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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

@Configuration
public class MqttConfig {
    @Autowired
    private DataManager dataManager;

    @Autowired
    SystemAuthenticator authenticator;

    private static final String TRUSTED_ROOT = "-----------------"; // TODO: Должно браться из файла

    @Bean
    public MqttClient mqttClient() throws Exception {
        String broker = "-----------------"; // TODO: MQTT брокер Яндекса
        String clientId = "-----------------"; // TODO: ClientId
        int keepAliveInterval = 30;
        /*
        От keepAliveInterval значения зависит частота отправки команд PINGREQ.
        Чем меньше значение keepAliveInterval, тем быстрее клиент понимает,
        что соединение было разорвано нештатным путем.
        Но для этого чаще отправляются тарифицируемые команды PINGREQ.
         */

        MqttClient mqttClient = new MqttClient(broker, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("-----------------"); // TODO: Замените на ваш client ID
        options.setPassword("-----------------".toCharArray()); // TODO: Замените на ваш client secret
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
