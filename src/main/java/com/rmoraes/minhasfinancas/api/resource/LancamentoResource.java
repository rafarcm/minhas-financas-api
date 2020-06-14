package com.rmoraes.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rmoraes.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.rmoraes.minhasfinancas.api.dto.LancamentoDTO;
import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;
import com.rmoraes.minhasfinancas.service.LancamentoService;
import com.rmoraes.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			return new ResponseEntity(
					service.salvar(converter(dto)),
					HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		try {
			final Lancamento lancamento = converter(dto);
			lancamento.setId(service.obterPorId(id).getId());
			service.atualizar(lancamento);
			return ResponseEntity.ok(lancamento);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		try {
			final Lancamento lancamento = service.obterPorId(id);
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
			service.atualizar(lancamento);
			return ResponseEntity.ok(lancamento);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		try {
			service.deletar(service.obterPorId(id));
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao, 
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long usuario) {
		
		try {
			return ResponseEntity.ok(service.buscar(Lancamento.builder()
					  .descricao(descricao)
					  .mes(mes)
					  .ano(ano)
					  .usuario(usuarioService.obterPorId(usuario))
					  .build()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		return Lancamento.builder()
						 .id(dto.getId())
						 .descricao(dto.getDescricao())
						 .mes(dto.getMes())
						 .ano(dto.getAno())
						 .valor(dto.getValor())
						 .usuario(usuarioService.obterPorId(dto.getUsuario()))
						 .tipo(dto.getTipo() != null ? TipoLancamento.valueOf(dto.getTipo()) : null)
						 .status(dto.getStatus() != null ? StatusLancamento.valueOf(dto.getStatus()) : null)
						 .build();
	}

}
