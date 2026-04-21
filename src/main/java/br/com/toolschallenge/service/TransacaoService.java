package br.com.toolschallenge.service;

import br.com.toolschallenge.assembler.RetornoPagamentoDtoAssembler;
import br.com.toolschallenge.disassembler.SolicitacaoPagamentoDtoDisassembler;
import br.com.toolschallenge.dto.input.SolicitacaoPagamentoInput;
import br.com.toolschallenge.dto.output.RetornoPagamentoDto;
import br.com.toolschallenge.entity.Transacao;
import br.com.toolschallenge.enums.EnumStatusTransacao;
import br.com.toolschallenge.exception.CartaoInvalidoException;
import br.com.toolschallenge.exception.DadosInvalidosException;
import br.com.toolschallenge.exception.ExtornoIndisponivelException;
import br.com.toolschallenge.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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

        EnumStatusTransacao status = validarDados(transacao);
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

    private EnumStatusTransacao validarDados(Transacao transacao) {

        BigDecimal valor;
        try {
            valor = new BigDecimal(transacao.getDescricao().getValor());
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Valor inválido");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DadosInvalidosException("Valor deve ser maior que zero");
        }

        if (transacao.getCartao() == null) {
            throw new CartaoInvalidoException("Cartão não informado");
        }

        if (transacao.getCartao().length() < 13 || transacao.getCartao().length() > 19) {
            throw new CartaoInvalidoException("Cartão inválido");
        }

        String tipo = String.valueOf(transacao.getFormaPagamento().getTipo());

        int parcelas;
        try {
            parcelas = Integer.parseInt(transacao.getFormaPagamento().getParcelas());
        } catch (Exception e) {
            throw new DadosInvalidosException("Parcelas inválidas");
        }

        if (parcelas < 0) {
            return EnumStatusTransacao.NEGADO;
        }

        if ("AVISTA".equalsIgnoreCase(tipo) && parcelas > 1) {
            return EnumStatusTransacao.NEGADO;
        }

        if (tipo.startsWith("PARCELADO") && parcelas <= 1) {
            return EnumStatusTransacao.NEGADO;
        }

        return EnumStatusTransacao.AUTORIZADO;
    }

    public Page<RetornoPagamentoDto> listarTransacoes(int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Transacao> transacoes = transacaoRepository.findAll(pageable);

        return transacoes.map(retornoPagamentoDtoAssembler::toDto);
    }

    public RetornoPagamentoDto realizarEstorno(String id) {
        Transacao transacao = buscarOuFalhar(id);

        if (transacao.getDescricao().getStatus() != EnumStatusTransacao.AUTORIZADO) {
            throw new ExtornoIndisponivelException("Transação não pode ser estornada");
        }

        transacao.getDescricao().setStatus(EnumStatusTransacao.ESTORNADO);

        transacaoRepository.save(transacao);

        return retornoPagamentoDtoAssembler.toDto(transacao);
    }
}
