package com.esther.dds.domain;

import com.esther.dds.domain.Validator.PasswordsMatch;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@PasswordsMatch(baseField = "password", matchField = "confirmPassword")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @NotEmpty
//    @Size(min = 8, max = 40)
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @NotEmpty
    @Column(length = 100)
    private String password;

    @Transient
    @NotEmpty(message = "Please enter Password Confirmation")
    private String confirmPassword;

    @Transient
    private String oldPassword;

    @Transient
    private String generatedPassword;

    private String activationCode;

    @NonNull
    @Column(nullable = false)
    private boolean enabled;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
    )       private Set<AdminRole> adminRoles = new HashSet<>();



    public void addAdminRole(AdminRole adminRole) {
        adminRoles.add(adminRole);
    }

    //voor meerdere rollen (Eventueel nodig voor in de toekomst)
    public void addadminRoles(Set<AdminRole> adminRoles) {
        adminRoles.forEach(this::addAdminRole);
    }

    //for each role in adminRoles, authorities.add SGA rile.getname
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return adminRoles.stream().map(adminRole -> new SimpleGrantedAuthority(adminRole.getName())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
