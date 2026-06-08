package com.example.ap2.repository;

import com.example.ap2.model.Cliente;
import com.example.ap2.model.Pedido;
import com.example.ap2.model.Produto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PedidoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PedidoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //CREATE
    public void create(Pedido pedido) {
        String query = "INSERT INTO pedido(status_pedido, data_pedido, cliente_id) VALUES(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //insert into pedido
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getStatusPedido());
            ps.setTimestamp(2, Timestamp.valueOf(pedido.getDataPedido()));
            ps.setLong(3, pedido.getCliente().getId());

            return ps;
        }, keyHolder);

        Long pedidoId = keyHolder.getKey().longValue();

        //salvar produtos na entidade associativa 'item_pedido'
        String sqlItem = "INSERT INTO item_pedido(pedido_id, produto_id, quantidade) VALUES (?,?,?)";
        for (Produto produto : pedido.getProdutos()) {
            jdbcTemplate.update(sqlItem, pedidoId, produto.getId(), 1);
        }
    }

    //read all
    public List<Pedido> readAll() {

        //juntar pedido com info do cliente
        String query = "SELECT p.*, c.nome AS cliente_nome, c.email AS cliente_email " +
                "FROM pedido p JOIN cliente c ON p.cliente_id = c.id";

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Pedido pedido = new Pedido();
            pedido.setId(rs.getLong("id"));

            Timestamp timestamp = rs.getTimestamp("data_pedido");
            if (timestamp != null){
                pedido.setDataPedido(timestamp.toLocalDateTime());
            }
            pedido.setStatusPedido(rs.getString("status_pedido"));

            //montar cliente dentro de pedido
            Cliente cliente = new Cliente();
            cliente.setId(rs.getLong("cliente_id"));
            cliente.setNome(rs.getString("cliente_nome"));
            cliente.setEmail(rs.getString("cliente_email"));
            pedido.setCliente(cliente);

            //buscar produtos pertencenets ao pedido especifico
            String sqlProdutos = "SELECT pr.* FROM produto pr" +
                    " JOIN item_pedido ip ON pr.id = ip.produto_id" +
                    " WHERE ip.pedido_id = ?";

            List<Produto> produtos = jdbcTemplate.query(sqlProdutos, (rsProd, rowProd) -> {
                Produto prod = new Produto(
                        rsProd.getLong("id"),
                        rsProd.getString("nome"),
                        rsProd.getDouble("preco")
                );
                return prod;
            }, pedido.getId());

            pedido.setProdutos(produtos);
            return pedido;
        });
    }

    // UPDATE
    public int updateStatus(Long id, Pedido pedido) {
        String query = "UPDATE pedido SET status_pedido = ? WHERE id = ?";
        return jdbcTemplate.update(
                query,
                pedido.getStatusPedido(),
                id
        );
    }

    //delete
    public void deleteById(Long id) {
        //apagar da associativa
        String queryItens = "DELETE FROM item_pedido WHERE pedido_id = ?";
        jdbcTemplate.update(queryItens, id);

        //apagar de pedido
        String queryPedido = "DELETE FROM pedido WHERE id = ?";
        jdbcTemplate.update(queryPedido, id);
    }
}
