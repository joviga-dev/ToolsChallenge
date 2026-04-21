package br.com.toolschallenge.dto.output;

import lombok.Data;

@Data
public class RetornoPagamentoFormaPagamentoDto {
    private String tipo;
    private String parcelas;
}
