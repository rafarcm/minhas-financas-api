package com.rmoraes.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmoraes.minhasfinancas.api.dto.LancamentoDTO;
import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;
import com.rmoraes.minhasfinancas.service.LancamentoService;
import com.rmoraes.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {
	
	private LancamentoService service;
	private UsuarioService usuarioService;

	public LancamentoResource(LancamentoService service) {
		super();
		this.service = service;
	}
	
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
	
	private Lancamento converter(LancamentoDTO dto) {
		return Lancamento.builder()
						 .id(dto.getId())
						 .descricao(dto.getDescricao())
						 .mes(dto.getMes())
						 .ano(dto.getAno())
						 .valor(dto.getValor())
						 .usuario(usuarioService.obterPorId(dto.getId()))
						 .tipo(TipoLancamento.valueOf(dto.getTipo()))
						 .status(StatusLancamento.valueOf(dto.getStatus()))
						 .build();
	}

}
