package br.senai.sp.cfp138.restaguide.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import br.senai.sp.cfp138.restaguide.model.Restaurante;
import br.senai.sp.cfp138.restaguide.repository.RestauranteRepository;
import br.senai.sp.cfp138.restaguide.repository.TpRestRepository;
import br.senai.sp.cfp138.restaguide.util.FirebaseUtil;

@Controller
public class RestauranteController {
	
	@Autowired
	private TpRestRepository repTipo;
	
	@Autowired
	private RestauranteRepository respRest;
	
	@Autowired
	private FirebaseUtil firebaseUtil;
	
	@RequestMapping("formRestaurante")
	public String form(Model model) {
		model.addAttribute("tipos",repTipo.findAllByOrderByNomeAsc()); 
		return "restaurante/form";
	}
	@RequestMapping("salvarRestaurante")
	public String salvarRestaurante(Restaurante restaurante, @RequestParam("fileFotos") MultipartFile[] fileFotos) {
		//String para a URL das fotos
		String fotos = restaurante.getFotos();
		
		//percorrer cada arquivo que foi submetido no form
		for (MultipartFile arquivo : fileFotos) {
			//verificar se o arquivo está vazio
			if(arquivo.getOriginalFilename().isEmpty()) {
				//vai para o próximo arquivo
				continue;
			}
			// faz o upload para a nuvem e obtém a url gerada
			try {
				fotos += firebaseUtil.uploadFile(arquivo)+";";
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		//atribui a String fotos ao objeto restaurante
		restaurante.setFotos(fotos);
		respRest.save(restaurante);
		return "redirect:formRestaurante";
	}
	@RequestMapping("listarRest/{page}")
	public String listarRest(Model model, @PathVariable("page")int page) {
		PageRequest pageable = PageRequest.of(page-1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		Page<Restaurante> pagina = respRest.findAll(pageable);
		int totalPages = pagina.getTotalPages();
		List<Integer> pageNumbers = new ArrayList<Integer>();

		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i+1);
		}
		
		model.addAttribute("rest", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", pageNumbers);
	
		return "restaurante/lista";
		
	}
	
	@RequestMapping("alterarRest")
	public String alterarRest(Model model, Long id) {
		Restaurante rest  = respRest.findById(id).get();
		model.addAttribute("rest", rest);
		return "forward:formRestaurante";
	}
	
	@RequestMapping("excluirRest")
	public String excluirRest(Long id) {
		Restaurante rest = respRest.findById(id).get();
		if(rest.getFotos().length() > 0) {
			for (String foto : rest.verFotos()) {
				firebaseUtil.deletar(foto);
			}
			
		}
		respRest.delete(rest);
		return "redirect:listarRest/1";
	}
	
	@RequestMapping("excluirFotoRestaurante")
	public String excluirFoto(Long idRestaurante, int numFoto, Model model) {
		// busca o restaurante no banco de dados
		Restaurante rest = respRest.findById(idRestaurante).get();
		//pegar a String da foto a ser exclusa
		String fotoUrl = rest.verFotos()[numFoto];
		// excluir do firebase
		firebaseUtil.deletar(fotoUrl);
		// "arranca" a foto da String fotos
		rest.setFotos(rest.getFotos().replace(fotoUrl+";", ""));
		// salva no BD o objeto rest
		respRest.save(rest);
		// adiciona o rest na Model
		model.addAttribute("restaurante", rest);
		// encaminhar para o form
		return "forward:/formRestaurante";
	}
}
