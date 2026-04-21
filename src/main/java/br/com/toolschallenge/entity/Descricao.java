package br.com.toolschallenge.entity;

import br.com.toolschallenge.enums.EnumStatusTransacao;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Embeddable
public class Descricao {
    private Float valor;
    private LocalDateTime dataHora;
    private String estabelecimento;
    private String nsu;
    private long codigoAutorizacao;
    private EnumStatusTransacao status;
}