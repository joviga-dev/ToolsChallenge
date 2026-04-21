package br.com.toolschallenge.dto.output;

import lombok.Data;

@Data
public class RetonoPagamentoTransacaoDto {
    private String id;
    private String cartao;
    private RetornoPagamentoDescricaoDto descricao;
    private RetornoPagamentoFormaPagamentoDto formaPagamento;
}
