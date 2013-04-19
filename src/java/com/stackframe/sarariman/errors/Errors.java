/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Errors {

    Error get(int id);

    Collection<Error> getAll();

    Map<? extends Number, Error> getMap();

}
