/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.outofoffice;

import com.stackframe.sarariman.Employee;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface OutOfOfficeEntry {

    int getId();

    Employee getEmployee();

    void setEmployee(Employee employee);

    Date getBegin();

    void setBegin(Date begin);

    Date getEnd();

    void setEnd(Date end);

    String getComment();

    void setComment(String comment);

}
