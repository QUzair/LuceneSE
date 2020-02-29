#!/bin/bash

tar xvf trec_eval.zip
cd trec_eval.9.0
make
cd ..
mvn compile
mvn package
java -jar target/UziLuceneSE-1.0-SNAPSHOT.jar


printf "\n\nStandard tfidf\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/tfidf_systemFile.txt
printf "\n\nStandard boolean\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/boolean_systemFile.txt
printf "\Standard bm25\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/bm25_systemFile.txt

printf "\n\Custom tfidf\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/custom/tfidf_systemFile.txt
printf "\n\Custom boolean\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/custom/boolean_systemFile.txt
printf "\Custom bm25\n"
./trec_eval.9.0/trec_eval cran/QRelsCorrectedforTRECeval simiarlityFiles/custom/bm25_systemFile.txt
