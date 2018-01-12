Clang-tidy Plugin
===============

This [Jenkins CI](http://jenkins-ci.org/) plug-in generates the trend report for [Clang-tidy](http://clang.llvm.org/extra/clang-tidy/), a tool for static C/C++ code analysis and linting.

A magic command:
```bash
/usr/share/clang/run-clang-tidy.py -j8 -clang-tidy-binary="$(which clang-tidy)" -p='debug' -header-filter='debug/.*/include/.*' 2>/dev/null | grep -E ': error: |: warning: ' | sort | uniq | sed -e 's/\&/\&amp;/g' -e 's/"/\&quot;/g' -e "s/'/\&apos;/g" -e 's/</\&lt;/g' -e 's/>/\&gt;/g' | sed -nr 's;^([^:]+):([0-9]+):([0-9]+): ([^:]+): (.*?) \[(((clang-)?[^-]*)-(.*))\]$;        <error type="\7" id="\9" severity="\4" message="\5">\n            <location file="\1" line="\2" column="\3"/>\n        </error>;p' | sed -r '1i<?xml version="1.0" encoding="UTF-8"?>\n<results>\n    <clangtidy version="'"$($(which clang-tidy) -version | sed -rn 's/^  LLVM version ([0-9.]+)$/\1/p')"'"/>\n    <errors>' | sed -r '$s;$;\n    </errors>\n</results>;'  > clangtidy-result.xml
```

For more information, visit the wiki page https://wiki.jenkins-ci.org/display/JENKINS/Clang+Tidy+Plugin.
