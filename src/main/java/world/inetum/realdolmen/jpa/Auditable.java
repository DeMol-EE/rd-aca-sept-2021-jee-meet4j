package world.inetum.realdolmen.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Auditable {

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @PrePersist
    public void prePersist() {
        setCreatedAt(LocalDateTime.now());
        preUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        setLastModifiedAt(LocalDateTime.now());
    }
}
