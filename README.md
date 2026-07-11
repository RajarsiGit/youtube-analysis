# Youtube Analysis

A Hadoop MapReduce job that counts YouTube videos per category from tab-separated dataset dumps.

## Data format

Each line in the input files (`0.txt`, `1.txt`, `2.txt`, `3.txt`) is a tab-separated record with fields including video ID, uploader, age, category, length, views, and related video IDs. The job counts records by category (column index 3).

## Build

Requires Java 8+ and Maven.

```
mvn package
```

This produces `target/youtube-analysis.jar`, a shaded jar with `Main-Class: youtube.analysis.Youtube`.

## Run

Requires a Hadoop installation (`hadoop` on PATH):

```
hadoop jar target/youtube-analysis.jar youtube.analysis.Youtube <input-path> <output-path>
```

`<input-path>` can point at one of the sample files in this repo (e.g. `0.txt`). `<output-path>` is created by the job; if it already exists it is deleted first.
