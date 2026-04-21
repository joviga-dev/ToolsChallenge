package br.com.toolschallenge.entity;

import br.com.toolschallenge.enums.EnumTipoPagamento;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FormaPagamento {
    private EnumTipoPagamento tipo;
    private String parcelas;
}