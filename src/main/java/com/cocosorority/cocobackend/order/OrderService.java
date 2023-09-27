package com.cocosorority.cocobackend.order;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_CREATE_ORDER = "INSERT INTO orders (item_id, size, customer_id, adjusted_cost) VALUES %s";

    public String createOrder(CustomerOrderRequest customerOrder) {
        StringJoiner valuesJoiner = new StringJoiner(",");
        for (ItemInOrder item: customerOrder.items) {
            StringJoiner combiner = new StringJoiner("','", "('", "')");
            combiner.add(item.itemId);
            combiner.add(item.size.toUpperCase());
            combiner.add(customerOrder.customerId);
            combiner.add(item.adjustedCost);
            valuesJoiner.add(combiner.toString());
        }
        int updatedRows = jdbcTemplate.update(
            String.format(SQL_CREATE_ORDER, valuesJoiner.toString())
        );
        return String.format("Updated %d rows", updatedRows);
    }

    public List<OrderHistoryResponse> getOrderHistory(CustomerOrderHistoryRequest orderHistoryRequest) {
        String SQL_SELECT_ORDER_HISTORY = 
            "SELECT " +
                "orders.item_id, " +
                "items.name, " +
                "items.cost_price, " +
                "items.drop_id, " +
                "items.image, " +
                "items.selling_price, " +
                "items.shipping, " +
                "orders.adjusted_cost, " +
                "orders.size, " +
                "orders.status, " +
                "orders.tracking_id, " +
                "orders.source " +
            "FROM " +
                "orders " +
                "INNER JOIN items ON orders.item_id = items.item_id " +
            "WHERE " +
                "orders.customer_id = ?";

        if (orderHistoryRequest.dropId != null) {
            SQL_SELECT_ORDER_HISTORY += " and items.drop_id = ?";
        }
        PreparedStatementSetter setter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, orderHistoryRequest.customerId);
                if (orderHistoryRequest.dropId != null) {
                    ps.setString(2, orderHistoryRequest.dropId);
                }
            }
        };
        SQL_SELECT_ORDER_HISTORY += " ORDER BY items.drop_Id, items.name";
        List<OrderHistoryResponse> data = jdbcTemplate.query(
            SQL_SELECT_ORDER_HISTORY,
            setter,
            new ResultSetExtractor<List<OrderHistoryResponse>>() {
                @Override
                public List<OrderHistoryResponse> extractData(ResultSet rs) throws SQLException {
                    List<OrderHistoryResponse> result = new ArrayList<OrderHistoryResponse>();
                    while(rs.next()) {
                        OrderHistoryResponse oHis = new OrderHistoryResponse();
                        oHis.itemId = rs.getString(1);
                        oHis.name = rs.getString(2);
                        oHis.costPrice = rs.getString(3);
                        oHis.dropId = rs.getString(4);
                        oHis.image = rs.getString(5);
                        oHis.sellingPrice = rs.getString(6);
                        oHis.shipping = rs.getString(7);
                        oHis.adjustedCost = rs.getString(8);
                        oHis.size = rs.getString(9);
                        oHis.status = rs.getString(10);
                        oHis.trackingId = rs.getString(11);
                        oHis.source = rs.getString(12);
                        result.add(oHis);
                    }
                    return result;
                }
            }
        );
        return data;
    }

    public String updateOrder(OrderUpdateRequest request) {
        String SQL_UPDATE_ORDER = 
        "UPDATE orders SET %s='%s' WHERE item_id = '%s' AND `size` = '%s' AND customer_id = '%s'";
        
        int updatedRows = jdbcTemplate.update(String.format(
            SQL_UPDATE_ORDER, 
            request.datapoint, 
            request.value, 
            request.itemId, 
            request.size, 
            request.customerId
        ));

        return String.format("Updated %d rows.", updatedRows);
    }
}