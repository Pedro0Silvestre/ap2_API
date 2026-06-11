package com.example.ap2.repository;

import com.example.ap2.model.Produto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProdutoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProdutoRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(Produto produto) {
        String query = "INSERT INTO produto(nome,preco) VALUES(?,?)";
        return jdbcTemplate.update(query, produto.getNome(), produto.getPreco());
    }

    //read all
    public List<Produto> readAll(){
        String query = "SELECT * FROM produto";
        return jdbcTemplate.query(query, new ProdutoRowMapper());
    }

    // read by id
    public Produto readById(Long id) {
        String query = "SELECT * FROM produto WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, new ProdutoRowMapper(), id);
        } catch (Exception e) {
            System.out.println("ERRO AO BUSCAR ID");
            return null;
        }
    }

    //deletar
    public int deleteById(Long id){
        String query = "DELETE FROM produto WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    //update
    public int updateById(Long id, Produto produto) {
        String query = "UPDATE produto SET nome = ?, preco = ? WHERE id = ?";
        return jdbcTemplate.update(query, produto.getNome(), produto.getPreco(), id);
    }

    private static class ProdutoRowMapper implements RowMapper<Produto> {
        @Override
        public Produto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Produto produto = new Produto();

            produto.setId(rs.getLong("id"));
            produto.setNome(rs.getString("nome"));
            produto.setPreco(rs.getBigDecimal("preco"));

            return produto;
        }
    }
}
