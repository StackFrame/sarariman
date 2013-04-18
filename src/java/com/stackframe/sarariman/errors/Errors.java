/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Errors {

    Error get(int id);

    Iterable<Error> getAll();

    Map<? extends Number, Error> getMap();

}
