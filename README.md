Clang-tidy Plugin
===============

This [Jenkins CI](http://jenkins-ci.org/) plug-in generates the trend report for [Clang-tidy](http://clang.llvm.org/extra/clang-tidy/), a tool for static C/C++ code analysis and linting.

A magic command:
```bash
grep '^/' clangtidy-result.xml | sort | uniq | sed -r
's;^(/[^:]+):([0-9]+):([0-9]+): ([^:]+): (.*?) \[(.*)\];\;        <error
id="\6" severity="\4" msg="\5" verbose="\5">\n            <location
file="\1" line="\2"/>;' | grep -v '^/'
```


