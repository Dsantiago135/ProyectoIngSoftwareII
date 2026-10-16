package com.dsantiago.proyectolab.Model.pkgDTO;

import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;

public record rqtUser(String attUsername, String attPassword, String attName,
    String attEmail, EnumRole attRole) {
}