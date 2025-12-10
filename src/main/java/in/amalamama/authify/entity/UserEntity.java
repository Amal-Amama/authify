package in.amalamama.authify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //key primaire pour la base

    @Column(unique = true)
    private String userId; // identifiant public pour l'exterieur

    private String name;

    @Column(unique=true)
    private String email;

    private String password;

    private String verifyOtp;

    private Long verifyOtpExpireAt;

    private Boolean isAccountVerified;

    private String resetOtp;

    private Long resetOtpExpireAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}
