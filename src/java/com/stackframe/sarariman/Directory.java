package com.stackframe.sarariman;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Directory {

    Map<Object, Employee> getEmployeeMap();

    Collection<Employee> getEmployees();

}
