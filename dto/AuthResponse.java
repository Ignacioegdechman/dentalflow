package com.dentalflow.dentalflow.dto;

import com.dentalflow.dentalflow.enums.RolUsuario;

public record AuthResponse(String token, String username, RolUsuario rol) {
}

