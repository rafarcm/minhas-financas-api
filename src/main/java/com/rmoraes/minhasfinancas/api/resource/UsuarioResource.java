package com.rmoraes.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmoraes.minhasfinancas.api.dto.UsuarioDTO;
import com.rmoraes.minhasfinancas.exception.AutenticacaoException;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

	private UsuarioService service;

	public UsuarioResource(UsuarioService service) {
		this.service = service;
	}
	
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

}
