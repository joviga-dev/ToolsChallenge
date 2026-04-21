package br.com.toolschallenge.dto.input;

import lombok.Data;

@Data
public class FormaPagamentoInput {

    private String tipo;
    private int parcelas;
}