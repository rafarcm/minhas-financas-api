package com.rmoraes.minhasfinancas.model.repository;

import static com.rmoraes.minhasfinancas.utils.TestUtils.EMAIL;
import static com.rmoraes.minhasfinancas.utils.TestUtils.criarEPersistirUmUsuario;
import static com.rmoraes.minhasfinancas.utils.TestUtils.criarUsuario;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//Cenário
		criarEPersistirUmUsuario(entityManager);
		
		//Ação / Execução
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadatradoComOEmail() {	
		//Ação / Execução
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirumUsuarioNaBaseDeDados() {
		//Ação / Execução
		final Usuario usuarioSalvo = repository.save(criarUsuario());
		
		//Verificação
		assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//Cenário
		criarEPersistirUmUsuario(entityManager);
		
		//Ação / Execução
		final Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		assertThat(result.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {	
		//Ação / Execução
		final Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		assertThat(result.isPresent()).isFalse();
	}

}
