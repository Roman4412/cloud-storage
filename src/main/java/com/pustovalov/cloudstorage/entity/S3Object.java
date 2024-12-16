package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "s3objects", uniqueConstraints = {
        @UniqueConstraint(name = "s3objects_unique_object_key_idx", columnNames = "object_key")
})
public abstract class S3Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "object_key")
    protected String objectKey;

    @Column(name = "parent")
    protected String parent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    public S3Object(String name, String objectKey, String parent, User user) {
        this.name = name;
        this.objectKey = objectKey;
        this.parent = parent;
        this.user = user;
    }
}
