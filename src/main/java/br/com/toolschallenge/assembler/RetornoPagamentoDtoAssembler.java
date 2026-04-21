package br.com.toolschallenge.assembler;

import br.com.toolschallenge.dto.output.RetornoPagamentoDto;
import br.com.toolschallenge.entity.Transacao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RetornoPagamentoDtoAssembler {

    @Autowired
    private ModelMapper modelMapper;

    public RetornoPagamentoDto toDto(Transacao transacao) {
        RetornoPagamentoDto dto = this.modelMapper.map(transacao, RetornoPagamentoDto.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        dto.getTransacao().getDescricao().setDataHora(transacao.getDescricao().getDataHora().format(formatter));
        return dto;
    }

}
