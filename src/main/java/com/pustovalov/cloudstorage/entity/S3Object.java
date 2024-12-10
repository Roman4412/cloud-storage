package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class S3Object {

    //TODO Добавить индексы, при написании миграций
    //TODO рефакторинг: путь в виде кастомного типа Path, а не строки
    //TODO objKey не должен изменяться т к по нему будет происходить поиск в minio , нужен констрэинт на это

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "object_key", unique = true)
    private String objectKey;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public S3Object(String name, String path, String objectKey, User user) {
        this.name = name;
        this.objectKey = objectKey;
        this.path = path;
        this.user = user;
    }
}
