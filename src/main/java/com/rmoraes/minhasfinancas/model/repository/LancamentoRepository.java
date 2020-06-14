package com.rmoraes.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rmoraes.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
