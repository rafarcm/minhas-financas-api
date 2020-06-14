package com.rmoraes.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rmoraes.minhasfinancas.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static final String EMAIL = "usuario@email.com";
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email(EMAIL).senha("123456").build();
	}
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//Cenário
		entityManager.persist(criarUsuario());
		
		//Ação / Execução
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadatradoComOEmail() {	
		//Ação / Execução
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirumUsuarioNaBaseDeDados() {
		//Ação / Execução
		final Usuario usuarioSalvo = repository.save(criarUsuario());
		
		//Verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//Cenário
		entityManager.persist(criarUsuario());
		
		//Ação / Execução
		final Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {	
		//Ação / Execução
		final Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result.isPresent()).isFalse();
	}

}
