package br.senai.sp.cfp138.restaguide.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.Restaurante;
import br.senai.sp.cfp138.restaguide.repository.RestauranteRepository;

@RequestMapping("/api/restaurante")
@RestController
public class RestauranteRestController {
	@Autowired
	private RestauranteRepository repository;
	@Publico
	@RequestMapping(value="", method = RequestMethod.GET)
	public Iterable<Restaurante> getRestaurantes(){
		return repository.findAll();
	}
	
	@Publico
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Restaurante> findRestaurante(@PathVariable("id") Long idRestaurante){
		// busca o restaurante
		Optional<Restaurante> restaurante = repository.findById(idRestaurante);
		if(restaurante.isPresent()) {
			return ResponseEntity.ok(restaurante.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Publico
	@RequestMapping(value="/tipo/{idTipo}", method = RequestMethod.GET)
	public Iterable<Restaurante> getTipoRestaurante(@PathVariable("idTipo") Long idTipo){
		return repository.findByTipoId(idTipo);
	}
	
	@Publico
	@RequestMapping(value="/estacionamento/{estacionamento}", method = RequestMethod.GET)
	public Iterable<Restaurante>getEstacionamentoRest(@PathVariable("estacionamento") Boolean estacionamento){
		return repository.findByEstacionamento(estacionamento);
	}
	
	@Publico
	@RequestMapping(value="/estado/{uf}", method = RequestMethod.GET)
	public Iterable<Restaurante>getEstado(@PathVariable("uf") String uf){
		return repository.findByEstado(uf);
	}
	
	
}
