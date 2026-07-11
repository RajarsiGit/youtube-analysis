# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Build:
```
mvn compile
```

Package a runnable, dependency-shaded jar (`target/youtube-analysis.jar`, `Main-Class: youtube.analysis.Youtube`):
```
mvn package
```

There are no unit tests in this repo (`mvn test` has nothing to run).

Run against a local Hadoop install (requires `hadoop` on PATH):
```
hadoop jar target/youtube-analysis.jar youtube.analysis.Youtube <input-path> <output-path>
```
`<input-path>` should point at one or more of the sample data files in the repo root (`0.txt`, `1.txt`, `2.txt`, `3.txt`); `<output-path>` must not already exist as a non-empty directory — the job deletes it first if present.

## Architecture

This is a single-class Hadoop MapReduce job (`src/main/java/youtube/analysis/Youtube.java`) that counts videos per category from tab-separated YouTube metadata dumps.

- Input format: each line is a tab-separated record; column index 3 (0-based) is the category. Lines with fewer than 6 fields are malformed and skipped by the mapper.
- `Youtube.Map` emits `(category, 1)` for each valid line.
- `Youtube.Reduce` sums counts per category.
- `Youtube.main` wires up the `Job` (new `org.apache.hadoop.mapreduce` API, not the deprecated `mapred` API) and expects exactly two CLI args: input path, output path.

The sample data files (`0.txt`–`3.txt`) are real YouTube dataset dumps from 2008, growing from ~180 to ~48k lines — useful for testing at different scales.
