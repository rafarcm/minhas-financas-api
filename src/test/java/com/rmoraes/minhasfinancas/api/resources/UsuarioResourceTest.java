package com.rmoraes.minhasfinancas.api.resources;

import static com.rmoraes.minhasfinancas.utils.TestUtils.EMAIL;
import static com.rmoraes.minhasfinancas.utils.TestUtils.SENHA;
import static com.rmoraes.minhasfinancas.utils.TestUtils.criarUsuario;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmoraes.minhasfinancas.api.dto.UsuarioDTO;
import com.rmoraes.minhasfinancas.api.resource.UsuarioResource;
import com.rmoraes.minhasfinancas.exception.AutenticacaoException;
import com.rmoraes.minhasfinancas.exception.RegraNegocioException;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.service.LancamentoService;
import com.rmoraes.minhasfinancas.service.UsuarioService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	private static final String API = "/api/usuarios";
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UsuarioService service;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		//Cenário
		final UsuarioDTO dto = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();
		final Usuario usuario = criarUsuario();
		when(service.autenticar(EMAIL, SENHA)).thenReturn(usuario);
		final String json = new ObjectMapper().writeValueAsString(dto);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = post(API.concat("/autenticar"))
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON)
			.content(json);
		
		//Verificação
		mvc.perform(requet)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		//Cenário
		final UsuarioDTO dto = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();
		when(service.autenticar(EMAIL, SENHA)).thenThrow(AutenticacaoException.class);
		final String json = new ObjectMapper().writeValueAsString(dto);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = post(API.concat("/autenticar"))
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON)
			.content(json);
		
		//Verificação
		mvc.perform(requet)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		//Cenário
		final UsuarioDTO dto = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();
		final Usuario usuario = criarUsuario();
		when(service.salvar(any(Usuario.class))).thenReturn(usuario);
		final String json = new ObjectMapper().writeValueAsString(dto);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = post(API)
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON)
			.content(json);
		
		//Verificação
		mvc.perform(requet).andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		//Cenário
		final UsuarioDTO dto = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();
		when(service.salvar(any(Usuario.class))).thenThrow(RegraNegocioException.class);
		final String json = new ObjectMapper().writeValueAsString(dto);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = post(API)
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON)
			.content(json);
		
		//Verificação
		mvc.perform(requet)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveObterSaldoDoUsuario() throws Exception {
		//Cenário
		final Long id = 1l;
		final BigDecimal saldo = BigDecimal.TEN;
		final Usuario usuario = criarUsuario();
		usuario.setId(id);
		when(service.obterPorId(id)).thenReturn(usuario);
		when(lancamentoService.obterSaldoPorUsuario(usuario.getId())).thenReturn(saldo);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = get(API.concat("/{id}/saldo"), id)
				.accept(APPLICATION_JSON)
				.contentType(APPLICATION_JSON);
		
		//Verificação
		mvc.perform(requet)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$").value(saldo));
	}
	
	@Test
	public void deveRetornarBadRequestAoObterSaldoDoUsuarioInvalido() throws Exception {
		//Cenário
		when(service.obterPorId(any(Long.class))).thenThrow(RegraNegocioException.class);
		
		//Ação / Execução
		final MockHttpServletRequestBuilder requet = get(API.concat("/{id}/saldo"), 1l)
				.accept(APPLICATION_JSON);
		
		//Verificação
		mvc.perform(requet)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
