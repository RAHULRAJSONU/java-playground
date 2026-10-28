# java-playground

A repository for learning and practicing core and modern Java features, targeting JDK 28 with incubator modules enabled.

## Prerequisites
- JDK 28 (early access is fine) with `jdk.incubator.json` available (`java --list-modules | grep incubator`)
- Maven 3.8+
- IntelliJ IDEA: import the project from `pom.xml`

## Build and run
```
mvn compile                                                      # compile (adds --add-modules jdk.incubator.json)
mvn test                                                         # run tests
mvn compile exec:exec -Dexec.mainClass=<fully.qualified.Class>   # run a demo
```
`exec:exec` passes `--add-modules=jdk.incubator.json --enable-native-access=ALL-UNNAMED` to the JVM.

### Running from IntelliJ
Incubator modules are not on the module graph by default, so a plain run fails with
`NoClassDefFoundError: jdk/incubator/json/Json`. Add these VM options to the run configuration:

```
--add-modules jdk.incubator.json --enable-native-access=ALL-UNNAMED
```

A shared configuration for `FetchPost` lives in `.run/`. To cover every demo, add the options under
**Run | Edit Configurations | Edit configuration templates | Application**.

## Layout
One package per topic under `src/main/java/io/github/rahulrajsonu/`; tests go in the same package under `src/test/java`.

| Package | Topic |
|---|---|
| `foreignmemory` | Foreign Function & Memory API: off-heap memory, structured layouts, column store, parallel aggregation |
| `nativejson` | `jdk.incubator.json`: fetching and parsing JSON (`FetchPost`) |
| `stream` | Reduce, collectors (grouping, teeing, custom), gatherers, IPL and weather-station analytics |
| `functionalInterface` | `Function`, `BiFunction`, `Supplier`, custom `TriFunction` |
| `utility` | Shared helpers |

## Topic roadmap
- Core: collections, generics, streams, `Optional`, exceptions, I/O/NIO.2, `java.time`
- Modern language: records, sealed types, pattern matching (switch, records), text blocks, unnamed classes/instance main, unnamed variables
- Concurrency: virtual threads, structured concurrency, scoped values, `CompletableFuture`
- Platform: FFM API, Vector API (`jdk.incubator.vector`), HTTP client, JFR, class-file API
