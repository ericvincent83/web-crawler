# Web Crawler
This application is a basic web crawler written in java. It is by no mean production ready and has only been developed for the purpose of training. There still are lots of improvements that could be considered. This application will crawl the website given as a parameter to a maxDepth given as parameter (or defaulted to 2). Note a maxDepth of 0 will remove the depth limitation and all internal links will be crawled
The output of the application should be a file with a list of all links discovered on the website. For all internal links, a list of all the static resources (scripts and images) will be rendered. External links won't be crawled.

## Requirement
Maven and Java 15

## Compile
- Clone the project `git clone https://github.com/ericvincent83/web-crawler`
- Navigate to the project directory
- Compile with maven `mvn clean install`

## Run
- From the project directory and once compiled
- Run `java -cp target/web-crawler-1.0-SNAPSHOT.jar org.ericvincent.App`
This will crawl "https://wiprodigital.com" with a depth of 2 by default
 - Run `java -cp target/web-crawler-1.0-SNAPSHOT.jar org.ericvincent.App website maxDepth`
 This will crawl the website passed as a parameter with a maxDepth of maxDepth
   - website -> url of the website to crawl
   - maxDepth -> integer defining how deep you want to crawl the website
- You can adjust the log level threshold in src/main/resource/log4j2.xml

## Known limitations
- If one of the link is broken, the crawling stops and the application stops. We could define another strategy where the crawling would continue and the report will show which link was faulty
- The application does not handle relative paths very well 
- The application does not handle links to sub domains (they'll be identified as external links and won't be crawled)
- I could sanitise the user input a bit better

## Improvements
- Solve the above limitations
- We could improve the way the application is packaged (start scripts, container...)
- We could defined a more standardised output
- We could improve the way we are giving input to the application
- We could let the user select its own output file and directory

## Questions I still have no answer to
- I decided to list the scripts as static resources although it does not seem to be done in a sitemap but it was easy to do and to remove so I figured why not ;) ?
- What should be the output of the algorithm (text file, json file, xml file...)
- Should it be a standalone application, hosted on the web ?

## Methodology
- I decided to go with a recursive algorithm as it seems perfectly appropriate (same action is performed on new links discovered in each page)
- With any recursive algorithm, the risk of stack overflow is large so I have added a notion of maximum depth to crawl
- Algorithm:
    ```Parse a link if not already parsed and maxDepth not reached
    Extract all links (Search for a tags and extract their href properties)
    Extract all resources (Search for script and img tags and extract their src properties)
    Store link and resource in a map
    For all links
        if (external) { store in the map }
        else { redo all previous steps }
    Dump the map into a file (to stdout in case of IOException)

## Dependencies
- Jsoup: Has good press and very little dependencies. It made the whole algorithm much easier by facilitating the parsing of the html response 
- Log4j2: One of the most used logging framework. I wanted to include some logs in the application as there are never enough logs in an application
Those two dependencies are actively maintained and did not bring lots of external dependencies to the application

