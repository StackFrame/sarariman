#!/bin/sh
sudo mysqldump sarariman --no-data=true --add-drop-table=false > sarariman.sql
