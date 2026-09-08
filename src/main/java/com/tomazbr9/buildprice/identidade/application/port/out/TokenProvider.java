package com.tomazbr9.buildprice.identidade.application.port.out;

import com.tomazbr9.buildprice.identidade.domain.enums.PapelUsuario;

public interface TokenProvider {
    String gerarAccessToken(String email, String papel);
    String gerarRefreshToken(String email);
}
