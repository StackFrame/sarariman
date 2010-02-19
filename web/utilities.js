/*
 * Copyright (C) 2009-2010 StackFrame, LLC
 * This code is licensed under GPLv2
 */

/** Finds elements with class name of "altrows" and alternates the class of every tr element contained within. */
function altRows() {
    var tables = document.getElementsByClassName('altrows');
    for (t = 0; t < tables.length; t++) {
        var rows = tables[t].getElementsByTagName("tr");
        for (i = 0; i < rows.length; i++) {
            if (i % 2 == 0) {
                rows[i].className = "evenrow";
            } else {
                rows[i].className = "oddrow";
            }
        }
    }
}