
/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2
 */

function altRows(id){
    if (document.getElementsByTagName) {

        var table = document.getElementById(id);
        var rows = table.getElementsByTagName("tr");

        for (i = 0; i < rows.length; i++) {
            if (i % 2 == 0) {
                rows[i].className = "evenrow";
            } else {
                rows[i].className = "oddrow";
            }
        }
    }
}