/*
 * Copyright (C) 2009-2010 StackFrame, LLC
 * This code is licensed under GPLv2
 */

/** Finds elements with class name of "altrows" and alternates the class of every tr element contained within. */
function altRows() {
    var tables = document.getElementsByClassName('altrows');
    for (var t = 0; t < tables.length; t++) {
        var rows = tables[t].getElementsByTagName("tr");
        for (var e = 0; e < rows.length; e += 2) {
            rows[e].className = "evenrow";
        }

        for (var o = 1; o < rows.length; o += 2) {
            rows[o].className = "oddrow";
        }
    }
}