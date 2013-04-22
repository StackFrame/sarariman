/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Audit;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.NamedResource;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.Week;
import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.lineitems.LineItem;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntry;
import com.stackframe.sarariman.tasks.Task;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author mcculley
 */
public interface Project extends Linkable {

    int getId();

    String getName();

    void setName(String name);

    boolean isManager(Employee employee);

    boolean isCostManager(Employee employee);

    Client getClient();

    void setClient(Client client);

    Collection<Audit> getAudits();

    PeriodOfPerformance getPoP();

    void setPoP(PeriodOfPerformance pop);

    String getContract();

    void setContract(String contract);

    String getSubcontract();

    void setSubcontract(String subcontract);

    String getPurchaseOrder();

    void setPurchaseOrder(String purchaseOrder);

    String getInvoiceText();

    void setInvoiceText(String text);

    BigDecimal getFunded();

    void setFunded(BigDecimal funded);

    BigDecimal getPreviouslyBilled();

    void setPreviouslyBilled(BigDecimal previouslyBilled);

    BigDecimal getODCFee();

    void setODCFee(BigDecimal fee);

    int getTerms();

    void setTerms(int terms);

    boolean isActive();

    void setActive(boolean active);

    BigDecimal getExpended();

    Iterable<Date> getDaysBilled();

    Collection<LineItem> getLineItems();

    Collection<Task> getTasks();

    Iterable<Employee> getManagers();

    Iterable<Employee> getCostManagers();

    Iterable<Employee> getAdministrativeAssistants();

    Set<Employee> getCurrentlyAssigned();

    Iterable<Week> getWorkedWeeks();

    Collection<NamedResource> getResources();

    Collection<LaborProjection> getLaborProjections();

    ProjectedExpenses getProjectedExpenses();

    Iterable<OutOfOfficeEntry> getUpcomingOutOfOffice();

    void delete();

}
