#!/bin/sh
# Embed a git version description into the code.
version=$(git describe)
sed s/VERSION/$version/g < src/java/com/stackframe/sarariman/Version.java.template > src/java/com/stackframe/sarariman/Version.java
