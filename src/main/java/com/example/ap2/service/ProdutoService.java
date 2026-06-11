package com.example.ap2.service;

import com.example.ap2.model.Produto;
import com.example.ap2.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void cadastrar(Produto produto) {
        //1- campos obrigatorios
        if(produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("nome e um campo obrigatorio");
        }

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("precos e obrigatorio e deve ser maior que zero");
        }

        produtoRepository.create(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.readAll();
    }

    public Produto buscarPorId(Long id) {
        //validar se id esta salvo
        Produto produto = produtoRepository.readById(id);

        if (produto == null) {
            throw new RuntimeException("ID nao salvo no banco");
        }
        return produto;
    }

    public void atualizar(Long id, Produto produtoAtualizado) {
        //failfast
        Produto produtoSalvoNoBanco = this.buscarPorId(id);
        Produto produtoFinal = new Produto();
        produtoFinal.setId(id);

        if(produtoAtualizado.getNome() != null && !produtoAtualizado.getNome().trim().isEmpty()){
            produtoFinal.setNome(produtoAtualizado.getNome());
        } else{
            produtoFinal.setNome(produtoSalvoNoBanco.getNome());
        }

        if(produtoAtualizado.getPreco() != null){
            produtoFinal.setPreco(produtoAtualizado.getPreco());
        } else{
            produtoFinal.setPreco(produtoSalvoNoBanco.getPreco());
        }


        int linhasAfetadas = produtoRepository.updateById(id,produtoFinal);
        if(linhasAfetadas == 0) {
            throw new RuntimeException("Nao foi possivel atualizar o produto");
        }
    }

    public void excluir(Long id) {
        this.buscarPorId(id);

        int linhasAfetadas = produtoRepository.deleteById(id);
        if (linhasAfetadas == 0) {
            throw new RuntimeException("Nao foi possivel excluir o produto");
        }
    }
}
