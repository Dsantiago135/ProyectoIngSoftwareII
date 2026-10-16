package com.dsantiago.proyectolab.Model.pkgModels;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity 
@Table (name = "tblUser")
public class clsUser {
    @Id
    @Column (name = "userName", nullable = false, unique = true)
    private String attUsername;
    @Column (name = "password", nullable = false)
    private String attPassword;
    @Column (name = "name", nullable = false)
    private String attName;
    @Column (name = "email", nullable = false, unique = true)
    private String attEmail;
    @Enumerated(EnumType.STRING)
    @Column (name = "role", nullable = false)
    private EnumRole attRole;
}