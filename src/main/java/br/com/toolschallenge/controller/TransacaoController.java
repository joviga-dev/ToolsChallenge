package br.com.toolschallenge.controller;

import br.com.toolschallenge.dto.output.RetornoPagamentoDto;
import br.com.toolschallenge.dto.input.SolicitacaoPagamentoInput;
import br.com.toolschallenge.entity.Transacao;
import br.com.toolschallenge.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/pagamento")
    public ResponseEntity<?> realizarPagamento(@RequestBody @Valid SolicitacaoPagamentoInput dto) {
        RetornoPagamentoDto retorno = transacaoService.realizarPagamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        Transacao transacao = transacaoService.buscarOuFalhar(id);
        return ResponseEntity.ok(transacao);
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(transacaoService.listarTransacoes(page));
    }
}