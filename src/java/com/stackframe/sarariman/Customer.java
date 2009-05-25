package com.stackframe.sarariman;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class Customer {

    private final int id;
    private final String name;

    public static Map<Integer, Customer> getCustomers() throws Exception {
        Context context = new InitialContext();
        DataSource dataSource = (DataSource)context.lookup("jdbc/sarariman");
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM projects");
        ResultSet resultSet = ps.executeQuery();
        Map<Integer, Customer> map = new HashMap<Integer, Customer>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            map.put(id, new Customer(id, name));
        }

        resultSet.close();
        return map;
    }

    Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
