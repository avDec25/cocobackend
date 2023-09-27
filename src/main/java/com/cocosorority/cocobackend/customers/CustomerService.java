package com.cocosorority.cocobackend.customers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.cocosorority.cocobackend.utils.GoogleSheetService;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.JsonObject;

import lombok.SneakyThrows;

@Service
public class CustomerService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_INSERT_CUSTOMER = "INSERT INTO customers (name, phone, address, email, social_id, pincode) VALUES (?, ?, ?, ?, ?, ?)";
    private final String SQL_UPDATE_CUSTOMER = "UPDATE customers SET name = ?, phone = ?, address = ?, social_id = ?, pincode = ? WHERE email = ? ";

    public String saveCustomer(CustomerSaveRequest request) {
        int updatedRows = jdbcTemplate.update(SQL_INSERT_CUSTOMER,
                request.name,
                request.phone,
                request.address,
                request.email,
                request.socialId,
                request.pincode);
        return String.format("Updated %d rows", updatedRows);
    }

    @Autowired
    GoogleSheetService sheetService;

    @SneakyThrows
    public String importCustomersFromGoogleSheet(String sheetId, String range) {
        ValueRange values = sheetService.getRangeValue(sheetId, range);
        int i = 0;
        int insertedRecordCount = 0;
        for(List<Object> row : values.getValues()) {
            try {
                jdbcTemplate.update(SQL_INSERT_CUSTOMER, 
                    WordUtils.capitalize((String)row.get(GSheetEnums.NAME.ordinal())).strip().replaceAll("\\p{C}", ""),
                    extractPhone(((String)row.get(GSheetEnums.PHONE.ordinal())).strip().replaceAll("\\p{C}", "")),
                    ((String)row.get(GSheetEnums.ADDRESS.ordinal())).strip().replaceAll("\\p{C}", ""),
                    ((String)row.get(GSheetEnums.EMAIL.ordinal())).strip().replaceAll("\\p{C}", "").toLowerCase(),
                    ((String)row.get(GSheetEnums.SOCIAL.ordinal())).strip().replaceAll("\\p{C}", ""),
                    extractPincode(((String)row.get(GSheetEnums.ADDRESS.ordinal())).strip().replaceAll("\\p{C}", ""))
                );
                insertedRecordCount += 1;
            }
            catch (DuplicateKeyException e) {                
                jdbcTemplate.update(
                    SQL_UPDATE_CUSTOMER, 
                    WordUtils.capitalize((String)row.get(GSheetEnums.NAME.ordinal())).strip().replaceAll("\\p{C}", ""),
                    extractPhone(((String)row.get(GSheetEnums.PHONE.ordinal())).strip().replaceAll("\\p{C}", "")),
                    ((String)row.get(GSheetEnums.ADDRESS.ordinal())).strip().replaceAll("\\p{C}", ""),                    
                    ((String)row.get(GSheetEnums.SOCIAL.ordinal())).strip().replaceAll("\\p{C}", ""),
                    extractPincode(((String)row.get(GSheetEnums.ADDRESS.ordinal())).strip().replaceAll("\\p{C}", "")),
                    ((String)row.get(GSheetEnums.EMAIL.ordinal())).strip().replaceAll("\\p{C}", "").toLowerCase()
                );
                // System.out.printf("Updated %s\n", (String)row.get(GSheetEnums.EMAIL.ordinal()));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("Error to import Row: %d \t %s\n", i, (String)row.get(GSheetEnums.EMAIL.ordinal()));
            }
            i += 1;
        }
        return String.format("Inserted %d Records from Sheet[R(%d) x C(%d)]", insertedRecordCount, values.getValues().size(), values.getValues().get(0).size());
    }

    private String extractPincode(String address) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(address);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    private String extractPhone(String phone) {
        final Pattern p = Pattern.compile("(\\d{10})$");
        final Matcher m = p.matcher(phone);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    private final String SQL_SELECT_CUSTOMERS = "SELECT customer_id, name, social_id FROM customers ORDER BY name";
    public List<CustomerQuickView> listCustomers() {
        List<CustomerQuickView> data = jdbcTemplate.query(
            SQL_SELECT_CUSTOMERS,
            new ResultSetExtractor<List<CustomerQuickView>>() {
                @Override
                public List<CustomerQuickView> extractData(ResultSet rs) throws SQLException {
                    List<CustomerQuickView> result = new ArrayList<CustomerQuickView>();
                    while(rs.next()) {
                        CustomerQuickView qView = new CustomerQuickView();
                        qView.customerId = rs.getString(1);
                        qView.name = rs.getString(2);
                        qView.socialId = rs.getString(3);
                        result.add(qView);
                    }
                    return result;
                }
            }
        );
        return data;
    }

    public JsonObject getCustomerDetails(String customerId) {
        String SELECT_CUSTOMER_SQL = "SELECT customer_id, name, phone, address, email, social_id, pincode FROM customers where customer_id=?";
        PreparedStatementSetter setter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, customerId);
            }
        };
        JsonObject customer = jdbcTemplate.query(
            SELECT_CUSTOMER_SQL,
            setter,
            new ResultSetExtractor<JsonObject>() {
                @Override
                public JsonObject extractData(ResultSet rs) throws SQLException {
                    JsonObject result = new JsonObject();
                    while(rs.next()) {
                        result.addProperty("customerId", rs.getString(1));
                        result.addProperty("name", rs.getString(2));
                        result.addProperty("phone", rs.getString(3));
                        result.addProperty("address", rs.getString(4));
                        result.addProperty("email", rs.getString(5));
                        result.addProperty("socialId", rs.getString(6));
                        result.addProperty("pincode", rs.getString(7));
                    }
                    return result;
                }
            }
        );
        return customer;
    }
}
