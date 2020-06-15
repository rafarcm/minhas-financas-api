package com.rmoraes.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rmoraes.minhasfinancas.exception.RegraNegocioException;
import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;
import com.rmoraes.minhasfinancas.model.repository.LancamentoRepository;
import com.rmoraes.minhasfinancas.service.impl.LancamentoServiceImpl;
import com.rmoraes.minhasfinancas.utils.TestUtils;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	private LancamentoServiceImpl service;
	
	@MockBean
	private LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamentoComSucesso() {
		//Cenário
		final Lancamento lancamentoASalvar = TestUtils.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);

		final Lancamento lancamentoSalvo = TestUtils.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Ação / Execução
		final Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//Verificação
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarLancamentoQaundoHouverErro() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		doThrow(RegraNegocioException.class).when(service).validar(lancamento);
		
		//Ação / Execução
		catchThrowableOfType(() -> service.salvar(lancamento), RegraNegocioException.class);
		
		//Verificação
		verify(repository, never()).save(lancamento);
	}
	
	@Test
	public void deveAtualizarUmLancamentoComSucesso() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		lancamento.setId(1l);
		doNothing().when(service).validar(lancamento);
		when(repository.save(lancamento)).thenReturn(lancamento);
		
		//Ação / Execução
		service.atualizar(lancamento);
		
		//Verificação
		verify(repository, times(1)).save(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		
		//Ação / Execução
		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		
		//Verificação
		verify(repository, never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamentoComSucesso() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		lancamento.setId(1l);
		
		//Ação / Execução
		service.deletar(lancamento);
		
		//Verificação
		verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		
		//Ação / Execução
		catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		
		//Verificação
		verify(repository, never()).delete(lancamento);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarLancamentos() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		lancamento.setId(1l);
		
		final List<Lancamento> lancamentos = Arrays.asList(lancamento);
		when(repository.findAll(any(Example.class))).thenReturn(lancamentos);
		
		//Ação / Execução
		final List<Lancamento> result = service.buscar(lancamento);
		
		//Verificação
		assertThat(result).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOSatusdeUmLancamentoComSucesso() {
		//Cenário
		final Lancamento lancamento = TestUtils.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		final StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doReturn(lancamento).when(service).atualizar(lancamento);
		
		//Ação / Execução
		service.atualizarStatus(lancamento, novoStatus);
		
		//Verificação
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterumLancamentoPorId() {
		//Cenário
		final Long id = 1l;
		final Lancamento lancamento = TestUtils.criarLancamento();
		lancamento.setId(id);
		when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//Ação / Execução
		final Lancamento result = service.obterPorId(id);
		
		//Verificação
		assertThat(result).isNotNull();
	}
	
	@Test
	public void deveRetonarErroQuandoLancamentoNaoExiste() {
		//Cenário
		//Ação / Execução
		final Throwable exception = catchThrowable(() -> service.obterPorId(1l));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Lançamento não encontrado para o id informado.");
	}
	
	@Test
	public void deveLancarErrosAoValidarUmlancamento() {
		//Cenário
		final Lancamento lancamento = new Lancamento();
		
		//Ação / Execução
		Throwable exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		//Cenário
		lancamento.setDescricao("Teste");
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		//Cenário
		lancamento.setMes(1);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		//Cenário
		lancamento.setAno(0);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		//Cenário
		lancamento.setAno(13);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		//Cenário
		lancamento.setAno(2019);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
			
		//Cenário
		lancamento.setUsuario(new Usuario());
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		//Cenário
		lancamento.setUsuario(Usuario.builder().id(1l).build());
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		//Cenário
		lancamento.setValor(BigDecimal.ZERO);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		//Cenário
		lancamento.setValor(BigDecimal.TEN);
		
		//Ação / Execução
		exception = catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
	}
	
	@Test
	public void deveObterUmSaldoPorUsuario() {
		//Cenário
		final Long id = 1l;
		final BigDecimal receitas = BigDecimal.ZERO;
		final BigDecimal despesas = BigDecimal.ZERO;
		when(repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA)).thenReturn(receitas);
		when(repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA)).thenReturn(despesas);
		
		//Ação / Execução
		final BigDecimal saldo = service.obterSaldoPorUsuario(id);
		
		//Verificação
		assertThat(saldo).isEqualTo(BigDecimal.ZERO);
	}
}
