package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_nfc")
@Getter
@Setter
@NoArgsConstructor   
public class TagNfc {

    @Id 
    private UUID id = UUID.randomUUID();

    @Column(name = "uuid_tag", nullable = false, unique = true)
    private String uuidTag;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "vinculado_em")
    private LocalDateTime vinculadoEm;

    @Column(name = "vinculado_por")
    private UUID vinculadoPor;

}
