package com.example.ecommerce.shared.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * An abstract base class that provides auditing fields for entities.
 * This class automatically tracks the creation and modification details such as timestamps and user information.
 * <p>
 *
 * Entities extending this class will inherit the following fields:
 * <ul>
 *     <li>{@code createdAt} - the timestamp of when the entity was created</li>
 *     <li>{@code updatedAt} - the timestamp of the last modification</li>
 *     <li>{@code createdBy} - the user who created the entity</li>
 *     <li>{@code updatedBy} - the user who last modified the entity</li>
 * </ul>
 *
 * Uses Spring Data JPA's auditing features to automatically populate these fields.</p>
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;

}
