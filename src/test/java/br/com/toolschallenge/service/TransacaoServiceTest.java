package br.com.toolschallenge.service;

import br.com.toolschallenge.assembler.RetornoPagamentoDtoAssembler;
import br.com.toolschallenge.disassembler.SolicitacaoPagamentoDtoDisassembler;
import br.com.toolschallenge.dto.input.DescricaoInput;
import br.com.toolschallenge.dto.input.FormaPagamentoInput;
import br.com.toolschallenge.dto.input.SolicitacaoPagamentoInput;
import br.com.toolschallenge.dto.input.TransacaoInput;
import br.com.toolschallenge.entity.Descricao;
import br.com.toolschallenge.entity.FormaPagamento;
import br.com.toolschallenge.entity.Transacao;
import br.com.toolschallenge.enums.EnumStatusTransacao;
import br.com.toolschallenge.enums.EnumTipoPagamento;
import br.com.toolschallenge.exception.CartaoInvalidoException;
import br.com.toolschallenge.exception.DadosInvalidosException;
import br.com.toolschallenge.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService service;

    @Mock
    private TransacaoRepository repository;

    @Mock
    private SolicitacaoPagamentoDtoDisassembler disassembler;

    @Mock
    private RetornoPagamentoDtoAssembler assembler;

    @Test
    void deveAutorizarTransacaoValida() {
        var input = inputValido();
        var transacao = transacaoValida();

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        service.realizarPagamento(input);

        assertEquals(EnumStatusTransacao.AUTORIZADO,
                transacao.getDescricao().getStatus());

        assertNotNull(transacao.getDescricao().getNsu());

        verify(repository).save(transacao);
    }

    @Test
    void deveNegarValorNegativo() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.getDescricao().setValor(Float.parseFloat("-1"));

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        assertThrows(DadosInvalidosException.class,
                () -> service.realizarPagamento(input));
    }

    @Test
    void deveLancarExcecaoParaCartaoNulo() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.setCartao(null);

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        assertThrows(CartaoInvalidoException.class,
                () -> service.realizarPagamento(input));
    }

    @Test
    void deveLancarExcecaoParaCartaoInvalido() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.setCartao("123");

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        assertThrows(CartaoInvalidoException.class,
                () -> service.realizarPagamento(input));
    }

    @Test
    void deveLancarExcecaoParaParcelasInvalidas() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.getFormaPagamento().setParcelas("abc");

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        assertThrows(DadosInvalidosException.class,
                () -> service.realizarPagamento(input));
    }

    @Test
    void deveNegarAvistaComParcelasMaiorQueUm() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.getFormaPagamento().setTipo(EnumTipoPagamento.valueOf("AVISTA"));
        transacao.getFormaPagamento().setParcelas("2");

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        service.realizarPagamento(input);

        assertEquals(EnumStatusTransacao.NEGADO,
                transacao.getDescricao().getStatus());
    }

    @Test
    void deveNegarParceladoComParcelasMenorOuIgualUm() {
        var input = inputValido();
        var transacao = transacaoValida();
        transacao.getFormaPagamento().setTipo(EnumTipoPagamento.valueOf("PARCELADO_LOJA"));
        transacao.getFormaPagamento().setParcelas("1");

        when(repository.existsById(any())).thenReturn(false);
        when(disassembler.toEntity(input)).thenReturn(transacao);

        service.realizarPagamento(input);

        assertEquals(EnumStatusTransacao.NEGADO,
                transacao.getDescricao().getStatus());
    }

    @Test
    void naoDeveSalvarDuplicado() {
        var input = inputValido();
        String id = input.getTransacao().getId();

        var existente = transacaoValida();

        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.realizarPagamento(input);

        verify(repository, never()).save(any());
    }

    @Test
    void deveEstornarTransacaoAutorizada() {
        var transacao = transacaoValida();
        transacao.getDescricao().setStatus(EnumStatusTransacao.AUTORIZADO);

        when(repository.findById(any())).thenReturn(Optional.of(transacao));

        service.realizarEstorno("1");

        assertEquals(EnumStatusTransacao.ESTORNADO,
                transacao.getDescricao().getStatus());

        verify(repository).save(transacao);
    }

    @Test
    void naoDeveEstornarSeNaoAutorizado() {
        var transacao = transacaoValida();
        transacao.getDescricao().setStatus(EnumStatusTransacao.NEGADO);

        when(repository.findById(any())).thenReturn(Optional.of(transacao));

        assertThrows(RuntimeException.class,
                () -> service.realizarEstorno("1"));
    }

    private SolicitacaoPagamentoInput inputValido() {
        var input = new SolicitacaoPagamentoInput();
        var t = new TransacaoInput();

        t.setId("1");
        t.setCartao("1234567890123456");

        var d = new DescricaoInput();
        d.setValor("100");
        t.setDescricao(d);

        var f = new FormaPagamentoInput();
        f.setTipo("AVISTA");
        f.setParcelas(Integer.parseInt("1"));
        t.setFormaPagamento(f);

        input.setTransacao(t);
        return input;
    }

    private Transacao transacaoValida() {
        var t = new Transacao();
        t.setId("1");
        t.setCartao("1234567890123456");

        var d = new Descricao();
        d.setValor(Float.parseFloat("100"));
        t.setDescricao(d);

        var f = new FormaPagamento();
        f.setTipo(EnumTipoPagamento.valueOf("AVISTA"));
        f.setParcelas("1");
        t.setFormaPagamento(f);

        return t;
    }
}