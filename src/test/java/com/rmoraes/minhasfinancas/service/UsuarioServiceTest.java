package com.rmoraes.minhasfinancas.service;

import static com.rmoraes.minhasfinancas.utils.TestUtils.EMAIL;
import static com.rmoraes.minhasfinancas.utils.TestUtils.NOME;
import static com.rmoraes.minhasfinancas.utils.TestUtils.SENHA;
import static com.rmoraes.minhasfinancas.utils.TestUtils.criarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
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
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuarioComSucesso() {
		//Cenário
		doNothing().when(service).validarEmail(anyString());
		when(repository.save(any(Usuario.class))).thenReturn(criarUsuario());
		
		//Ação / Execução
		final Usuario usuarioSalvo = service.salvar(new Usuario());
		
		//Verificação
		assertThat(usuarioSalvo).isNotNull();
		assertThat(usuarioSalvo.getNome()).isEqualTo(NOME);
		assertThat(usuarioSalvo.getEmail()).isEqualTo(EMAIL);
		assertThat(usuarioSalvo.getSenha()).isEqualTo(SENHA);
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		//Cenário
		final Usuario usuario = criarUsuario();
		doThrow(RegraNegocioException.class).when(service).validarEmail(EMAIL);

		//Ação / Execução
		service.salvar(usuario);
		
		//Verificação
		verify(repository, never()).save(usuario);
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//Cenário
		when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(criarUsuario()));
		
		//Ação / Execução
		final Usuario result = service.autenticar(EMAIL, SENHA);
		
		//Verificação
		assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//Cenário
		when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		//Ação / Execução
		final Throwable exception = catchThrowable(() -> service.autenticar(EMAIL, SENHA));
		
		//Verificação
		assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//Cenário
		String senhaInvalida = "123";
		when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(criarUsuario()));
		
		//Ação / Execução
		final Throwable exception = catchThrowable(() -> service.autenticar(EMAIL, senhaInvalida));
		
		//Verificação
		assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Senha inválida.");
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		//Cenário
		when(repository.existsByEmail(anyString())).thenReturn(false);
		
		//Ação / Execução
		service.validarEmail(EMAIL);
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//Cenário
		when(repository.existsByEmail(anyString())).thenReturn(true);
		
		//Ação / Execução
		final Throwable exception = catchThrowable(() -> service.validarEmail(EMAIL));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Já existe um usuário cadasrado com este email.");
	}

}
