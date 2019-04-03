package com.blockchain.larisa.service;


import com.blockchain.larisa.util.LarisaThreadPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

@Service
public class MailService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    @Value("${mail.auth.code}")
    private String authCode;

    @Value("${mail.port}")
    private String mailPort;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.send.address}")
    private String sendAddress;

    @Value("${mail.receive.address}")
    private String receiveAddress;

    private Properties props;

    private Authenticator authenticator;

    private ExecutorService executor = LarisaThreadPool.executor();

    @Override
    public void afterPropertiesSet() {
        props = new Properties();
        props.setProperty("mail.host", smtpHost);
        props.setProperty("mail.smtp.port", mailPort);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        authenticator = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sendAddress, authCode);
            }
        };
    }

    public void send(String address, String title, String msg) {
        LOGGER.info("send mail. address:{}, title:{}, msg:{}", address, title, msg);
        try {
            Session session = Session.getDefaultInstance(props, authenticator);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendAddress));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(StringUtils.isEmpty(address) ? receiveAddress : address));
            message.setSubject(title);
            message.setContent(msg, "text/plain;charset=UTF-8");
            executor.execute(() -> {
                try {
                    Transport.send(message);
                    LOGGER.info("send mail success, address:{}, title:{}, msg:{}", address, title, msg);
                } catch (Exception e) {
                    LOGGER.error("send mail exception. title:{}, msg:{}, exception", title, msg, e);
                }
            });

        } catch (Exception e) {
            LOGGER.error("send mail exception. title:{}, msg:{}, exception", title, msg, e);
        }
    }
}
