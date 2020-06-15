package com.rmoraes.minhasfinancas.api.resource;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmoraes.minhasfinancas.api.dto.UsuarioDTO;
import com.rmoraes.minhasfinancas.exception.AutenticacaoException;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.service.LancamentoService;
import com.rmoraes.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {		
		try {
			return ResponseEntity.ok(service.autenticar(dto.getEmail(), dto.getSenha()));
		} catch (AutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		try {
			return new ResponseEntity(
					service.salvar(
							Usuario.builder()
								   .nome(dto.getNome())
								   .email(dto.getEmail())
								   .senha(dto.getSenha())
								   .build()),
					HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable Long id) {
		try {
			final BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(service.obterPorId(id).getId());
			return ResponseEntity.ok(saldo);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
