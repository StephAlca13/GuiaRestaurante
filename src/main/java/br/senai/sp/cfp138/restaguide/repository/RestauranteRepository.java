package br.senai.sp.cfp138.restaguide.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.restaguide.model.Restaurante;

public interface RestauranteRepository extends PagingAndSortingRepository<Restaurante, Long> {

	public List<Restaurante> findByTipoId(Long idTipo);
	
	public List<Restaurante> findByEstacionamento(Boolean estacionamento);
	
	public List<Restaurante> findByEstado(String uf);
	
}
