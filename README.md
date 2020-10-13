# Nashorn Sandbox

A secure sandbox for executing JavaScript in Java apps using the [Nashorn](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/) engine.

Also see [Graal JS Sandbox](https://github.com/javadelight/delight-graaljs-sandbox) and [Rhino Sandbox](https://github.com/javadelight/delight-rhino-sandbox).

Part of the [Java Delight Suite](https://github.com/javadelight/delight-main#java-delight-suite).

[![Maven Central](https://img.shields.io/maven-central/v/org.javadelight/delight-nashorn-sandbox.svg)](https://search.maven.org/#search%7Cga%7C1%7Cdelight-nashorn-sandbox)

[![Build Status](https://travis-ci.org/javadelight/delight-nashorn-sandbox.svg?branch=master)](https://travis-ci.org/javadelight/delight-nashorn-sandbox)

Open Security Issues: [# 73](https://github.com/javadelight/delight-nashorn-sandbox/issues/73)

## Usage

The sandbox by default **blocks access to all** Java classes.

Classes, which should be used in JavaScript, must be explicitly allowed.

```java
NashornSandbox sandbox = NashornSandboxes.create();
     
sandbox.allow(File.class);
     
sandbox.eval("var File = Java.type('java.io.File'); File;")
```

Or you can inject your Java object as a JS global variable

```java
NashornSandbox sandbox = NashornSandboxes.create();

sandbox.inject("fromJava", new Object());

sandbox.eval("fromJava.getClass();");
```

The sandbox also allows limiting the CPU time and memory usage of scripts. This allows terminating scripts which contain infinite loops and other problematic code.

```java
NashornSandbox sandbox = NashornSandboxes.create();
     
sandbox.setMaxCPUTime(100);
sandbox.setMaxMemory(50*1024);
sandbox.allowNoBraces(false);
sandbox.setMaxPreparedStatements(30); // because preparing scripts for execution is expensive
sandbox.setExecutor(Executors.newSingleThreadExecutor());
     
sandbox.eval("var o={}, i=0; while (true) {o[i++]='abc';};");
```

This code will raise a ScriptCPUAbuseException.

The sandbox beautifies the JavaScript code for this and injects additional statements into the submitted code. It is thus possible that the original line numbers from
the submitted JS code are not preserved. To debug the code, which is generated by the sandbox, activate its debug mode as follows using a `log4j.properties` file (see [log4j.properties](https://github.com/javadelight/delight-nashorn-sandbox/blob/master/src/test/resources/log4j.properties)):

```
log4j.logger.delight.nashornsandbox.NashornSandbox=DEBUG
```

This will output the generated JS on the console as follows:

```
--- Running JS ---
var \__it = Java.type('delight.nashornsandbox.internal.InterruptTest');var \__if=function(){\__it.test();};
while(true) {__if();
  i = i+1;
}
--- JS END ---
```

## Maven

Just add the following dependency to your projects.

```xml
<dependency>
    <groupId>org.javadelight</groupId>
    <artifactId>delight-nashorn-sandbox</artifactId>
    <version>[insert latest version]</version>
</dependency>
```

This artifact is available on [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cdelight-nashorn-sandbox) and 
[BinTray](https://bintray.com/javadelight/javadelight/delight-nashorn-sandbox).

[![Maven Central](https://img.shields.io/maven-central/v/org.javadelight/delight-nashorn-sandbox.svg)](https://search.maven.org/#search%7Cga%7C1%7Cdelight-nashorn-sandbox)

If you are looking for a JAR with all dependencies, you can also download it from [here](https://github.com/javadelight/delight-nashorn-sandbox/releases).

## Contributors

[Eduardo Velasques](https://github.com/eduveks): API extensions to block/allow Rhino system functions; Capability to block/allow variables after Sandbox has been created. 

[Marcin Gołębski](https://github.com/mgolebsk): Major refactoring and performance improvements. Among other things improved the performance
for JS evaluation and better handling of monitoring for threads for possible CPU abuse ([#23](https://github.com/javadelight/delight-nashorn-sandbox/pull/23)).

[Marco Ellwanger](https://github.com/mellster2012): Initial support for GraalJS engine by implementing sandbox implementation backed by GraalJS.

## Version History

- 0.1.32: Defaulted `allowNoBraces` to `true` since the check easily leads to false positives ([Issue #102](https://github.com/javadelight/delight-nashorn-sandbox/issues/102) and [Issue #104](https://github.com/javadelight/delight-nashorn-sandbox/issues/104)) 
- 0.1.28: Upgraded JS Beautify version to 1.9.0 to address failing security checks ([Issue #93](https://github.com/javadelight/delight-nashorn-sandbox/issues/93)) 
- 0.1.27: Fix bug that Nashorn Sandbox does not guarantee that scripts will be stopped if they consume too much memory or CPU time ([PR #96](https://github.com/javadelight/delight-nashorn-sandbox/pull/96) by [jerome-baudoux](https://github.com/jerome-baudoux))
- 0.1.25: Graal JS sandbox capabilities have been moved to [delight-graaljs-sandbox](https://github.com/javadelight/delight-graaljs-sandbox) repository. 
- 0.1.23: Initial support for Graal JS ([PR #87](https://github.com/javadelight/delight-nashorn-sandbox/pull/87/) by [mellster2012](https://github.com/mellster2012)) 
- 0.1.22: Fixing issue with injection of if statement in certain situations ([PR #82](https://github.com/javadelight/delight-nashorn-sandbox/pull/82) by [foxep2001](https://github.com/foxep2001)); Support for JVMs that do not support ThreadMXBean ([PR #84](https://github.com/javadelight/delight-nashorn-sandbox/pull/84) by [amoravec](https://github.com/amoravec))
- 0.1.21: Fixing executor thread not set after <some value> ms - intermittent issue [# 75](https://github.com/javadelight/delight-nashorn-sandbox/issues/75) by [pradeepKaatnam](https://github.com/pradeepKaatnam) 
- 0.1.20: Implementing protection for security issue [# 73](https://github.com/javadelight/delight-nashorn-sandbox/issues/73) as suggested by [amlweems](https://github.com/amlweems)
- 0.1.19: Performance improvement for beautification [PR #71](https://github.com/javadelight/delight-nashorn-sandbox/pull/71) by [turbanoff](https://github.com/turbanoff)
- 0.1.18: Fixing [issue #66](https://github.com/javadelight/delight-nashorn-sandbox/issues/67) with [PR #69](https://github.com/javadelight/delight-nashorn-sandbox/pull/69) by [everestbt](https://github.com/everestbt)
- 0.1.17: Improved way bindings are handled (see [PR #68](https://github.com/javadelight/delight-nashorn-sandbox/pull/68) by [everestbt](https://github.com/everestbt)); Fixing [issue #66](https://github.com/javadelight/delight-nashorn-sandbox/issues/66)
- 0.1.16: Removing tools.jar dependency (see issue [# 62](https://github.com/javadelight/delight-nashorn-sandbox/issues/62))
- 0.1.15: Allowing to inject custom cache for secure JS (see [PR #59](https://github.com/javadelight/delight-nashorn-sandbox/pull/59)); Preventing the use of `--no-java` engine parameter (see [issue #57](https://github.com/javadelight/delight-nashorn-sandbox/issues/57))
- 0.1.14: Fixed bug that ThreadMonitor waits for too long sometimes (see [PR #56](https://github.com/javadelight/delight-nashorn-sandbox/pull/56) by [cmorris](https://github.com/cmorriss))
- 0.1.13: Added support for providing Bindings for evaluating scrips (see [PR #44](https://github.com/javadelight/delight-nashorn-sandbox/pull/44) by [Frontrider](https://github.com/Frontrider)); Improving way access to global functions such as `exit` is blocked; Allowing for|while|do when they are given in quoted strings in JavaScript (see [issue #47](https://github.com/javadelight/delight-nashorn-sandbox/issues/47)).
- 0.1.12: Adding capability for calling Invocable:invoke (see [PR #42](https://github.com/javadelight/delight-nashorn-sandbox/pull/42) from [escitalopram](https://github.com/escitalopram)); Fixing typos in method signatures (see [PR #41](https://github.com/javadelight/delight-nashorn-sandbox/pull/41) by [Sina](https://github.com/sinaa))
- 0.1.11: Added support for custom parameters in creating Nashorn Script engine (see [issue #40](https://github.com/javadelight/delight-nashorn-sandbox/issues/40)).
- 0.1.10: Added createBindings to the API to allow overriding global properties (see [PR #39](https://github.com/javadelight/delight-nashorn-sandbox/pull/39) by [Srinivasa Chintalapati](https://github.com/srinivasarajuch))
- 0.1.9: Fixed [bug #36](https://github.com/javadelight/delight-nashorn-sandbox/issues/36)
- 0.1.8: Fixed that `do`, `while` and `for` in comments might cause BracesExceptions (see [bug #34](https://github.com/javadelight/delight-nashorn-sandbox/issues/34))
- 0.1.7: Used webjar dependency for BeautifyJS and slf4j as logging dependency ([PR #35](https://github.com/javadelight/delight-nashorn-sandbox/issues/32) by [thjaeckle](https://github.com/thjaeckle)); Updated license (see [bug #32](https://github.com/javadelight/delight-nashorn-sandbox/issues/32))
- 0.1.6: Fixing bug that monitor checking for CPU abuses would hang when it encountered `monitor.wait(0)` (see [issue 30](https://github.com/javadelight/delight-nashorn-sandbox/issueks/30))
- 0.1.5: Fixing [bug #28](https://github.com/javadelight/delight-nashorn-sandbox/issues/28) with [PR 29](https://github.com/javadelight/delight-nashorn-sandbox/pull/29) by [srinivasarajuch](https://github.com/srinivasarajuch) - added support for evaluation JS with specific ScriptContext 
- 0.1.4: Fixing [bug #27](https://github.com/javadelight/delight-nashorn-sandbox/issues/27)
- 0.1.3: Improving regex for interrupt injections ([PR 26](https://github.com/javadelight/delight-nashorn-sandbox/pull/26)), cleaning up code for obtaining JSBeautifier instance ([PR 25](https://github.com/javadelight/delight-nashorn-sandbox/pull/25)) 
- 0.1.2: Improving way JsBeautifier instance is obtained ([PR 24](https://github.com/javadelight/delight-nashorn-sandbox/pull/24)) 
- 0.1.1: Making all fields in NashornSandboxImpl `protected` rather than `private` (see issue #19)
- 0.1.0: Major rework and performance improvements implemented by [Marcin Gołębski](https://github.com/mgolebsk) ([PR 23](https://github.com/javadelight/delight-nashorn-sandbox/pull/23))

## Further Documentation

- [JavaDocs](http://modules.appjangle.com/delight-nashorn-sandbox/latest/apidocs/index.html)
- [All Maven Reports](http://modules.appjangle.com/delight-nashorn-sandbox/latest/project-reports.html)
