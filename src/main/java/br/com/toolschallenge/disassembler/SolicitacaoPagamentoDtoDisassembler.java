package br.com.toolschallenge.disassembler;

import br.com.toolschallenge.dto.input.SolicitacaoPagamentoInput;
import br.com.toolschallenge.entity.Transacao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SolicitacaoPagamentoDtoDisassembler {

    @Autowired
    private ModelMapper modelMapper;

    public Transacao toEntity(SolicitacaoPagamentoInput dto) {
        Transacao transacao = this.modelMapper.map(dto.getTransacao(), Transacao.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        transacao.getDescricao().setDataHora(LocalDateTime.parse(dto.getTransacao().getDescricao().getDataHora(), formatter));
        return transacao;
    }
}
