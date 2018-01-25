# TwitterStreaming

This is interview exercise.

**Summary:**

The following exercise is a simple Twitter Streaming API ETL exercise that represents the kinds of data transformations 
we often deal with at Circle. Raw data often require munging for both machine learning feature generation as well as 
data analytics and is an important part of our process. In evaluating your solution, we will weigh both the quality of 
the source code and answers to questions equally. For answers to questions, be as specific as you can. You can include 
examples from this exercise or other work to demonstrate your point of view. Use your answers as a platform to impress 
us with your creativity and subject knowledge.  

**Deliverables:**

Source code should be written in Python, Java, Scala, or JS. You can use any libraries, packages, or frameworks 
available in these languages to assist you. You may feel free to also use the internet in any way you’d like (like in a 
normal development process). Please solve the tasks below and return your source code with any discussion documentation 
associated with the questions posed below. You can commit your code to a public git repository or return via email along 
with your other documentation. You will not be evaluated on the time taken to perform the exercise. 

**App Setup:**

1.	Build a simple application to connect to the Twitter Public Streaming API and stream tweet records over time. Filter 
out any tweets that have the high level key "delete" from the processed data. For each record Json, you should 
process/store only the keys under ‘user’ and ‘text’. An example Json is given below. We have also provided a simple 
connector in Python (https://gist.github.com/geofizx/7618736a90b6f7651d4a72c1746cc69e) that you can chose to use/augment 
or develop your own. Note: you can choose to implement batching. Twitter API keys and secrets should have been provided 
to you separately. 
2.	Set up exception handling to deal with a few of the Twitter API non-200 return codes, such as 420 (rate limiting), 
and 503 (service unavailable). Build a simple test for these cases as well.
  
**Data ETL:**

1.	Flatten - We wish to persist the twitter records as a flat data model. Using all the nested keys present under 
‘user’ and ‘text’ keys in each record, write a function to translate records from Json to an appropriate flattened data 
structure using whatever notation you prefer (e.g., dot, bracket, etc.). If you need to exclude any keys from records, 
please explain. 

2.	Stream metrics - Perform aggregations (e.g., counts, distributions) for the following keys in the stream: 
{‘user’: {‘friends_count’, ’followers_count’, ’location’, ’lang’}} and report these metrics at the end of application 
execution. An example might be generating histograms of locations over a streaming time window. How might you change the 
reporting of these metrics if they were derived from sparse data in the tweet stream? What, if any, normalization, 
weighting, or additional metrics would be required to report ‘location’ counts, if ‘location’ data were only available 
in a small percentage of streamed tweets.

3.	Normalization - Tweet text can have lots of variability including 1) newlines, 2) non-ascii chars, 3) case 
variability, and 4) superfluous whitespace. Normalize all the tweet keys that represent strings with respect to the list 
above.
      
_**Questions:**_

●	For the 3 steps above, did you chose to perform the steps on the records on-the-fly or in batch? Discuss the 
differences of the two approaches in terms of computational complexity, speed, and scalability of your application. 
Explain which would be more practical at 1000x the event volume and why.

**Persistence:**

For the exercise, you can choose to persist the data as a file or in a database. Regardless of how you choose to store 
the data, please address the following questions:

**_Questions:_**

●	Given that you might want to persist this data over some extended period of time, how frequently would you persist 
data? 

●	Would you persist in batches or as stream? 

●	How might the mechanism of persisting data affect consistency? 

●	Would you use SQL or noSQL and why?

●	Does eventual consistency in the database matter? When would it matter?

