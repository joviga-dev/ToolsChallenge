package br.com.toolschallenge.repository;

import br.com.toolschallenge.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, String> {
}
