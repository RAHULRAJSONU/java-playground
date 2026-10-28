# java-playground

A repository for learning and implementing java features.

Practice repo for core and modern Java (JDK 28, incubator modules enabled).

## Commands
- `mvn test` - run all tests (one test class per topic)
- `mvn compile exec:exec -Dexec.mainClass=<fqcn>` - run a demo

## Layout
One package per topic under `src/main/java/io/github/rahulrajsonu/`, a matching test under `src/test/java`.

| Package | Topic |
|---|---|
| `foreignmemory` | Foreign Function & Memory API, off-heap data |
| `nativejson` | `jdk.incubator.json` |
| `stream` | Collectors, gatherers, IPL and weather analytics |
| `functionalInterface` | Function, BiFunction, Supplier, custom TriFunction |

## Topic roadmap
- Core: collections, generics, streams, `Optional`, exceptions, I/O/NIO.2, `java.time`
- Modern language: records, sealed types, pattern matching (switch, records), text blocks, unnamed classes/instance main, unnamed variables
- Concurrency: virtual threads, structured concurrency, scoped values, `CompletableFuture`
- Platform: FFM API, Vector API (`jdk.incubator.vector`), HTTP client, JFR, class-file API
