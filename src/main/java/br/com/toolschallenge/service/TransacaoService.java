package br.com.toolschallenge.service;

import br.com.toolschallenge.assembler.RetornoPagamentoDtoAssembler;
import br.com.toolschallenge.disassembler.SolicitacaoPagamentoDtoDisassembler;
import br.com.toolschallenge.dto.input.SolicitacaoPagamentoInput;
import br.com.toolschallenge.dto.output.RetornoPagamentoDto;
import br.com.toolschallenge.entity.Transacao;
import br.com.toolschallenge.enums.EnumStatusTransacao;
import br.com.toolschallenge.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    @Autowired
    private SolicitacaoPagamentoDtoDisassembler solicitacaoPagamentoDtoDisassembler;
    @Autowired
    private RetornoPagamentoDtoAssembler retornoPagamentoDtoAssembler;

    public RetornoPagamentoDto realizarPagamento(@Valid SolicitacaoPagamentoInput dto) {

        String id = dto.getTransacao().getId();

        if (transacaoRepository.existsById(id)) {
            return retornoPagamentoDtoAssembler.toDto(
                    transacaoRepository.findById(id).get()
            );
        }

        Transacao transacao = solicitacaoPagamentoDtoDisassembler.toEntity(dto);

        EnumStatusTransacao status = validarPagamento(transacao);
        transacao.getDescricao().setStatus(status);

        if (status == EnumStatusTransacao.AUTORIZADO) {
            transacao.getDescricao().setNsu(gerarNsu());
        }

        transacaoRepository.save(transacao);

        return retornoPagamentoDtoAssembler.toDto(transacao);
    }

    public Transacao buscarOuFalhar(String id) {
        return this.transacaoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transação não encontrada"));
    }

    private String gerarNsu() {
        return String.valueOf(System.currentTimeMillis());
    }

    private EnumStatusTransacao validarPagamento(Transacao transacao) {

        BigDecimal valor = new BigDecimal(transacao.getDescricao().getValor());
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return EnumStatusTransacao.NEGADO;
        }

        if (transacao.getCartao() == null || transacao.getCartao().length() < 13 || transacao.getCartao().length() > 19) {
            return EnumStatusTransacao.NEGADO;
        }

        String tipo = String.valueOf(transacao.getFormaPagamento().getTipo());
        int parcelas = Integer.parseInt(transacao.getFormaPagamento().getParcelas());

        if ("AVISTA".equalsIgnoreCase(tipo) && parcelas > 1) {
            return EnumStatusTransacao.NEGADO;
        }

        if ("PARCELADO".equalsIgnoreCase(tipo) && parcelas <= 1) {
            return EnumStatusTransacao.NEGADO;
        }

        return EnumStatusTransacao.AUTORIZADO;
    }

    public Page<RetornoPagamentoDto> listarTransacoes(int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Transacao> transacoes = transacaoRepository.findAll(pageable);

        return transacoes.map(retornoPagamentoDtoAssembler::toDto);
    }
}
