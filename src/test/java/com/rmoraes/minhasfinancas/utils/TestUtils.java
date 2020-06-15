package com.rmoraes.minhasfinancas.utils;

import java.math.BigDecimal;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;

public final class TestUtils {
		
	public static final String NOME = "usuario";
	public static final String EMAIL = "usuario@email.com";
	public static final String SENHA = "123456";
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome(NOME).email(EMAIL).senha(SENHA).build();
	}
	
	public static Usuario criarEPersistirUmUsuario(final TestEntityManager entityManager) {
		final Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		return entityManager.find(Usuario.class, usuario.getId());
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				 .descricao("Lançamento Qualquer")
				 .mes(1)
				 .ano(2019)
				 .valor(BigDecimal.valueOf(10))
				 .tipo(TipoLancamento.RECEITA)
				 .status(StatusLancamento.PENDENTE)
				 .build();
	}
	
	public static Lancamento criarEPersistirUmLancamento(final TestEntityManager entityManager) {
		final Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return entityManager.find(Lancamento.class, lancamento.getId());
	}
	
}
