package br.com.toolschallenge.dto.input;

import lombok.Data;

@Data
public class DescricaoInput {

    private String valor;
    private String dataHora;
    private String estabelecimento;
}