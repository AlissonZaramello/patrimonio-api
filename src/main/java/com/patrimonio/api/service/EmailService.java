package com.patrimonio.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service 
public class EmailService {
    
    private final RestClient restClient;
    private final String remetente;

    public EmailService(@Value("${resend.api-key}") String apiKey, @Value("${resend.from}") String remetente){

        this.remetente = remetente;

        this.restClient = RestClient.builder().baseUrl("https://api.resend.com")
                            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    public void enviarEmailRecuperacao(String destinatario, String linkRecuperacao) {

        Map<String, Object> corpo = Map.of(
                "from", remetente,
                "to", new String[]{destinatario},
                "subject", "Recuperação de senha - SICPAT-RFID",
                "html", """
                        <h2>Recuperação de senha</h2>
                        <p>Recebemos uma solicitacao para redefinir a senha
                        da sua conta no SICPAT-RFID.</p>
                        <p>Clique no botão abaixo para cadastrar uma nova senha:</p>
                        <p>
                            <a href="%s"
                               style="display:inline-block;padding:12px 20px;
                               background-color:#2563eb;color:#ffffff;
                               text-decoration:none;border-radius:6px;">
                                Redefinir minha senha
                            </a>
                        </p>
                        <p>O link expira em 15 minutos.</p>
                        <p>Se você não solicitou essa alteração, ignore este e-mail.</p>        
                        """.formatted(linkRecuperacao)
        );

        restClient.post().uri("/emails").body(corpo).retrieve().toBodilessEntity();
    }
}
