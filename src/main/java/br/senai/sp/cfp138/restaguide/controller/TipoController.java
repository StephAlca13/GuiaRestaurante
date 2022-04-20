package br.senai.sp.cfp138.restaguide.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.senai.sp.cfp138.restaguide.model.TipoRestaurante;
import br.senai.sp.cfp138.restaguide.repository.TpRestRepository;

@Controller
public class TipoController {
	@Autowired
	private TpRestRepository repository;

	@RequestMapping("formTipo")
	public String formTipo() {
		return "tipos/form";
	}

	@RequestMapping("alterarTipo")
	public String alterarTipo(Model model, Long id) {
		TipoRestaurante tipo = repository.findById(id).get();
		model.addAttribute("tipo", tipo);
		return "forward:formTipo";
	}

	@RequestMapping("excluirTipo")
	public String excluirTipo(Long id) {
		repository.deleteById(id);
		return "redirect:listarTipo/1";
	}

	@RequestMapping("listarTipo/{page}")
	public String listar(Model model, @PathVariable("page") int page) {
		// cria um pageable com 6 elementos por página, ordenando os objetos pelo nome,
		// de forma ascendente
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		// cria a página atual através do repository
		// faz a busca no banco de dados
		Page<TipoRestaurante> pagina = repository.findAll(pageable);
		// descobrir o total de páginas
		int totalPages = pagina.getTotalPages();
		// cria uma lista de inteiros para representar as páginas
		List<Integer> pageNumbers = new ArrayList<Integer>();
		// preencher a lista com as páginas
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i + 1);
		}
		// adiciona as variáveis na Model
		model.addAttribute("tipos", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", pageNumbers);
		// retorna para o HTML da lista
		return "tipos/lista";
	}

	@RequestMapping(value = "salvarTipo", method = RequestMethod.POST)
	public String salvarTipo(@Valid TipoRestaurante tipo, BindingResult result, RedirectAttributes attr) {
		// verifica se houve erro na validação do objeto
		if (result.hasErrors()) {
			// envia mensagem de erro via requisição
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			return "redirect:formTipo";
		}

		try {
			// salva o Tipo
			repository.save(tipo);
			attr.addFlashAttribute("mensagemSucesso", "Tipo salvo com sucesso. ID:" + tipo.getId());
			return "redirect:formTipo";

		} catch (Exception e) {
			// caso ocorra ima Exception informar ao usuário
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar o Tipo: " + e.getMessage());
		}
		return "redirect:formTipo";

	}
	@RequestMapping("buscarTodos")
	public String procurarPorTodos(String todostp, Model model) {
		model.addAttribute("tipos", repository.procurarPorTodos("%"+todostp+"%"));
		return "/tipos/lista";
}
}
	
	
