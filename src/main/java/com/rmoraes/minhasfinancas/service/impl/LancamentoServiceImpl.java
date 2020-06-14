package com.rmoraes.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.rmoraes.minhasfinancas.exception.RegraNegocioException;
import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;
import com.rmoraes.minhasfinancas.model.repository.LancamentoRepository;
import com.rmoraes.minhasfinancas.service.LancamentoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LancamentoServiceImpl implements LancamentoService {
	
	private final LancamentoRepository repository;
	
	@Override
	@Transactional
	public Lancamento salvar(final Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(final Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(final Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(final Lancamento lancamentoFiltro) {
		final Example<Lancamento> example = Example.of(lancamentoFiltro, 
				ExampleMatcher.matching()
							  .withIgnoreCase()
							  .withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(final Lancamento lancamento, final StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}
	
	@Override
	public Lancamento obterPorId(final Long id) {
		final Optional<Lancamento> lancamento = repository.findById(id);
		if(!lancamento.isPresent()) {
			throw new RegraNegocioException("Lançamento não encontrado para o id informado.");
		}
		return lancamento.get();
	}


	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		return receitas.subtract(despesas);
	}

	@Override
	public void validar(final Lancamento lancamento) {
		if(StringUtils.isEmpty(lancamento.getDescricao())) {
			throw new RegraNegocioException("Informe uma descrição válida.");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido.");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido.");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um usuário.");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor válido.");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
	}

}
