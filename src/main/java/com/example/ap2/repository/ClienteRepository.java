package com.example.ap2.repository;

import com.example.ap2.model.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClienteRepository {

    private final JdbcTemplate jdbcTemplate;

    //criando conexao banco com as configs no apllication propeties
    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //create
    public int create(Cliente cliente) {
        String query = "INSERT INTO cliente(nome,email) VALUES(?,?)";
        return jdbcTemplate.update(query, cliente.getNome(),cliente.getEmail());
    }

    //read ALL
    public List<Cliente> readAll() {
        String query = "SELECT * FROM cliente";
        return jdbcTemplate.query(query, new ClienteRowMapper());
    }

    //read ID
    public Cliente readById(Long id){
        String query = "SELECT * FROM cliente WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(query, new ClienteRowMapper(), id);
        } catch (Exception e) {
            System.out.println("Erro ao buscar ID");
            return null;
        }
    }

    //delete
    public int deleteById(Long id){
        String query = "DELETE FROM cliente WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    //update
    public int updateById(Long id, Cliente cliente) {
        String query = "UPDATE cliente SET nome = ?, email = ? WHERE id = ?";
        return jdbcTemplate.update(query, cliente.getNome(), cliente.getEmail(), id);
    }

    //ROW MAPPER: Funciona como o serializer do cliente mapeando linhas do db em objetos cliente
    private static class ClienteRowMapper implements RowMapper<Cliente> {
        @Override
        public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getLong("id"));
            cliente.setNome(rs.getString("nome"));
            cliente.setEmail(rs.getString("email"));

            return cliente;
        }
    }
}
