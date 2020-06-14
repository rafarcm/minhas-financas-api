package com.rmoraes.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rmoraes.minhasfinancas.exception.AutenticacaoException;
import com.rmoraes.minhasfinancas.exception.RegraNegocioException;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.model.repository.UsuarioRepository;
import com.rmoraes.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	private UsuarioServiceImpl service;
	
	@MockBean
	private UsuarioRepository repository;
	
	private static final String NOME = "usuario";
	private static final String EMAIL = "usuario@email.com";
	private static final String SENHA = "123456";

	public static Usuario criarUsuario() {
		return Usuario.builder().nome(NOME).email(EMAIL).senha(SENHA).id(1l).build();
	}
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuarioComSucesso() {
		//Cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(criarUsuario());
		
		//Ação / Execução
		final Usuario usuarioSalvo = service.salvar(new Usuario());
		
		//Verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo(NOME);
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo(EMAIL);
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo(SENHA);
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		//Cenário
		final Usuario usuario = criarUsuario();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(EMAIL);

		//Ação / Execução
		service.salvar(usuario);
		
		//Verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//Cenário
		Mockito.when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(criarUsuario()));
		
		//Ação / Execução
		final Usuario result = service.autenticar(EMAIL, SENHA);
		
		//Verificação
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//Cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//Ação / Execução
		final Throwable exception = Assertions.catchThrowable(() -> service.autenticar(EMAIL, SENHA));
		
		//Verificação
		Assertions.assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//Cenário
		String senhaInvalida = "123";
		Mockito.when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(criarUsuario()));
		
		//Ação / Execução
		final Throwable exception = Assertions.catchThrowable(() -> service.autenticar(EMAIL, senhaInvalida));
		
		//Verificação
		Assertions.assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Senha inválida.");
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		//Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//Ação / Execução
		service.validarEmail(EMAIL);
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//Ação / Execução
		final Throwable exception = Assertions.catchThrowable(() -> service.validarEmail(EMAIL));
		
		//Verificação
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Já existe um usuário cadasrado com este email.");
	}

}
