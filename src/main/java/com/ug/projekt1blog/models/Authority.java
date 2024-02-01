package com.ug.projekt1blog.models;

import lombok.*;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Authority {

    @Id
    @Column(length = 16)
    String authority_name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authority otherAuthority = (Authority) o;
        return Objects.equals(authority_name, otherAuthority.authority_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority_name);
    }

}
