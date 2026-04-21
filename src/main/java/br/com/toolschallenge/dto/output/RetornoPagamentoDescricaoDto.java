package br.com.toolschallenge.dto.output;

import br.com.toolschallenge.enums.EnumStatusTransacao;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RetornoPagamentoDescricaoDto {
    private String valor;
    private String dataHora;
    private String estabelecimento;
    private String nsu;
    private String codigoAutorizacao;
    private EnumStatusTransacao status;
}
