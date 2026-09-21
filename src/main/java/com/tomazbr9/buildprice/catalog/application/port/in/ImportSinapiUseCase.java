package com.tomazbr9.buildprice.catalog.application.port.in;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;

import java.util.UUID;

public interface ImportSinapiUseCase {

    UUID execute(ImportSinapiCommand command);
}