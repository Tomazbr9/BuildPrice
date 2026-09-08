package com.tomazbr9.buildprice.identidade.application.port.out;

import com.tomazbr9.buildprice.identidade.application.dto.UsuarioAutenticado;

public interface AuthenticationService {

    UsuarioAutenticado autenticar(String email, String senha);
}