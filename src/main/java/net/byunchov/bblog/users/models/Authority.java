package net.byunchov.bblog.users.models;

import java.io.Serializable;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "authorities")
public class Authority implements Serializable {
    @Id
    @Column(length = 16, unique = true)
    private String name;
}
