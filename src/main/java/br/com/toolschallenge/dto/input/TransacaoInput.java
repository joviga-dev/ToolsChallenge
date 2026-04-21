package br.com.toolschallenge.dto.input;

import lombok.Data;

@Data
public class TransacaoInput {
    private String id;
    private String cartao;
    private DescricaoInput descricao;
    private FormaPagamentoInput formaPagamento;
}
