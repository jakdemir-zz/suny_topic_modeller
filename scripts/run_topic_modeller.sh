#!/bin/bash

echo "mallet import-dir --input /tmp/results/raw_content/* --output /tmp/results/topics-input-everything --keep-sequence --remove-stopwords"
rm -Rfv /tmp/results
mkdir /tmp/results
mallet import-dir --input /tmp/raw_content_sp/* --output /tmp/results/topics-input-everything --keep-sequence --remove-stopwords

echo "mallet train-topics --input /tmp/results/topics-input-everything --num-topics 5 --output-state /tmp/results/topic_results_everything.gz --output-doc-topics /tmp/results/output_doc_topics.txt --output-topic-keys /tmp/results/output_topic_keys.txt"

mallet train-topics --input /tmp/results/topics-input-everything --num-topics 5 --output-state /tmp/results/topic_results_everything.gz --output-doc-topics /tmp/results/output_doc_topics.txt --output-topic-keys /tmp/results/output_topic_keys.txt

gunzip /tmp/results/topic_results_everything.gz
tail -n+4 /tmp/results/topic_results_everything> /tmp/results/topic_results_everything.txt
rm /tmp/results/topic_results_everything
sort  -k6n -k2 /tmp/results/topic_results_everything.txt|awk '{print $2" "$6}'|uniq >/tmp/results/topic_results_everything_sorted.txt

