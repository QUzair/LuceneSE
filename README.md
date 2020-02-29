# Cranfield Collection Lucene SE

A search engine built upon the cranfield collection for  **"CS7IS3 INFORMATION RETRIEVAL AND WEB SEARCH**. 

Ran similarities
- tfidf
- boolean
- bm25 

And a CustomAnalyzer

# Running Project
Grant Permission to bash script to automatically unzip trec_eval.zip, build java lucene project, and trecEval 
```
git clone https://github.com/QUzair/LuceneSE.git

chmod u+x trecEval.sh

./trecEval.sh
```


# Files In Project

Main Classes:
- CranFileParser.java
	> Parses Cran Docs File and Index it with specified Analyzer
- CranfieldQueries
	> Parses Cran Queries File and creates DockRank for queries
- CranfieldModel
	> Basic model for field in cranfield doc (id,title,author,biblio,content)
- PersonalQueries
	> Class to create custom queries for created Index
- Main
	> Main class which indexes and searches with different analyzers and similarity classes
	
Within cran folder:
- cran.all.1400
	> Contains 1400 documents from the Cranfield Collection.
- cran.qry
	> Queries that will be used to test our Implementation of the Search Engine with trec_eval
- QRelsCorrectedforTRECeval
	> RelDocs used for evaluation of our own search results 
	
 Output/Other files:
 - similarityFiles
	> Creating 'DocRanks' results from our scoring functionality with bm25, boolean and tfidf
- trecEval.sh
	> Bash Script to unzip and make trec_eval.zip, build java lucene project, and run trecEval on the outputted similarityFiles (contains 'DocRanks') and QRelsCorrectedforTRECeval
- stopWords.txt
	> List of stopwords taken from [https://www.ranks.nl/stopwords](https://www.ranks.nl/stopwords) 


## Custom Analyzer

Basic Custom analyzer with  stopwords taken from [https://www.ranks.nl/stopwords](https://www.ranks.nl/stopwords) 
```java
//Creating New Token Stream  
TokenStream tokenStream = new LowerCaseFilter(source);  
  
//Adding Filters  
tokenStream = new EnglishPossessiveFilter(tokenStream);  
tokenStream = new PorterStemFilter(tokenStream);  
tokenStream = new EnglishMinimalStemFilter(tokenStream);  
tokenStream = new KStemFilter(tokenStream);  
  
  
CharArraySet newStopSet = null;  
try {  
    newStopSet = StopFilter.makeStopSet(getStopWords()); //Set of Words from ranks.nl/stopwords
} catch (IOException e) {  
    e.printStackTrace();  
}  
tokenStream = new StopFilter(tokenStream, newStopSet);  
return new TokenStreamComponents(source, tokenStream);
```
# Results

|                |StandardAnalyzer                          |CustomAnalyzer                         |
|----------------|-------------------------------|-----------------------------|
|tfidf|0.1557            | 0.2796           |
|boolean          | 0.1782            | 0.2781            |
|bm25          |0.2864|0.3375|


As can be seen bm25 provides the best results along with the CustomAnalyzer.

![alt text][eq]

[eq]: https://wikimedia.org/api/rest_v1/media/math/render/svg/43e5c609557364f7836b6b2f4cd8ea41deb86a96

![alt text][graph]

[graph]: https://github.com/QUzair/LuceneSE/blob/master/rec_precision.png?raw=true


