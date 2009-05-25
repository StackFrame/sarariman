package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class Project {

    private final int id;
    private final String name;
    private final Customer customer;

    Project(int id, String name, Customer customer) {
        this.id = id;
        this.name = name;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Customer getCustomer() {
        return customer;
    }

}
