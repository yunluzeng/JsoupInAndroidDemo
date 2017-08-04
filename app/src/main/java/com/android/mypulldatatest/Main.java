package com.android.mypulldatatest;

import java.util.stream.Stream;

public class Main {

  private static String keyword;
  private static String type="";
  private static int maxPage = 100;
  private static int threadCount = 10;
  public static void main(String[]args){
    Stream.of(args).forEach(System.out::println);
    if (args.length >= 2) {
      type = args[0];
      keyword = args[1];
    }

    if (args.length >=3) {
      maxPage = Integer.valueOf(args[2]);
    }

    if (args.length >=4) {
      threadCount = Integer.valueOf(args[3]);
    }

    Crawl crawl = null;
    switch (type) {
      case "job":
        System.out.println("start crawl jobs...");
        crawl = new Jobs(keyword);
        break;
      case "movie":
        System.out.println("start crawl movies...");
        crawl = new Movie_dydytt();
        break;
      default:
        System.out.println("nothing is done.");
        System.out.println("usage:\n" +
                "java -jar myspider-1.0-SNAPSHOT.jar type keyword [max page] [thread count] \n" +
                " type: crawl job or movie list\n" +
                " keyword: it's search key word for job while file name for both job and movie\n" +
                " max page: how many result pages will be crawled, by default it's 100\n" +
                " thread count: how many threads will be started to crawl pages, by default it's 10\n");
        break;
    }

    if (null != crawl) {
      String fileName = keyword +".html";
      crawl.setThreadCount(threadCount);
      crawl.setMaxPage(maxPage);
      crawl.crawl(fileName);
    }
  }
//  public static void main(String[]args){
////    crawlMovie();
//    crawlJobs();
//  }
//
//
//  private static void crawlMovie() {
//    String fileName = "movieList.html";
//    Crawl crawl = new Movie_dydytt(300);
//    crawl.setThreadCount(50);
//    crawl.crawl(fileName);
//  }
//
//  private static void crawlJobs() {
////    String fileName = "jobList.html";
//    String keyword= "财务";
//    String fileName = keyword +".html";
//    new Jobs(keyword,300).crawl(fileName);
//  }
}
