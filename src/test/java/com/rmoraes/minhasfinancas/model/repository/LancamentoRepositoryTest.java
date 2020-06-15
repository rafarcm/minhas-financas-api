package com.rmoraes.minhasfinancas.model.repository;

import static com.rmoraes.minhasfinancas.utils.TestUtils.criarEPersistirUmLancamento;
import static com.rmoraes.minhasfinancas.utils.TestUtils.criarLancamento;
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

import com.rmoraes.minhasfinancas.model.entity.Lancamento;
import com.rmoraes.minhasfinancas.model.enums.StatusLancamento;
import com.rmoraes.minhasfinancas.model.enums.TipoLancamento;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	private LancamentoRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		//Cenário
		//Ação / Execução
		final Lancamento lancamentoSalvo = repository.save(criarLancamento());
		
		//Verificação
		assertThat(lancamentoSalvo.getId()).isNotNull(); 
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//Cenário
		final Lancamento lancamento = criarEPersistirUmLancamento(entityManager);
		
		//Ação / Execução
		repository.delete(lancamento);
		
		//Verificação
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull(); 
	}

	@Test
	public void deveAtualizarUmLancamento() {
		//Cenário
		final Lancamento lancamento = criarEPersistirUmLancamento(entityManager);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setAno(2018);
		lancamento.setMes(2);
		lancamento.setTipo(TipoLancamento.DESPESA);
		lancamento.setStatus(StatusLancamento.EFETIVADO);
		
		//Ação / Execução
		repository.save(lancamento);
		
		//Verificação
		final Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoAtualizado).isNotNull();  
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo(lancamento.getDescricao());
		assertThat(lancamentoAtualizado.getMes()).isEqualTo(lancamento.getMes());
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(lancamento.getAno());
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(lancamento.getStatus());
		assertThat(lancamentoAtualizado.getTipo()).isEqualTo(lancamento.getTipo());
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		//Cenário
		final Lancamento lancamento = criarEPersistirUmLancamento(entityManager);
		
		//Ação / Execução
		final Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		//Verificação
		assertThat(lancamentoEncontrado.isPresent()).isTrue();  
	}
}
