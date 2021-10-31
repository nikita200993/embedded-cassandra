#!/bin/bash

output="$(dirname "$0")/benchmark_report.txt"

java -jar ./target/benchmarks.jar EmbeddedDatabaseBenchmark -rf text -rff "$output"