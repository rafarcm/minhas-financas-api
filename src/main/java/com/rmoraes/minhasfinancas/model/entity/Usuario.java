package com.rmoraes.minhasfinancas.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario", schema = "financas")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome", length = 150)
	private String nome;
	
	@Column(name = "email", length = 100)
	private String email;
	
	@Column(name = "senha", length = 20)
	private String senha;
	
}