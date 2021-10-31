#!/bin/bash

script_dir=$(dirname "$0")
output="$script_dir/benchmark_report.txt"

java -jar "$script_dir/target/benchmarks.jar" EmbeddedDatabaseBenchmark -rf text -rff "$output"