package com.pet.buscaativa.entities;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Table(name = "tb_auditoria")
public abstract class AbstractEntities{
    
    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "alterado_em")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "criado_por", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "alterado_por")
    private String updatedBy;
}
