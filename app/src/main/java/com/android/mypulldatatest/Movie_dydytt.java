package com.android.mypulldatatest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by yansutao on 22/07/2017.
 */
public class Movie_dydytt extends Crawl<Movie_dydytt.Item> {
  private String seperator = "/";
  private String url = "http://s.dydytt.net";
  private String search = url + seperator + "plus/search.php?kwtype=0&searchtype=title&keyword=%%%%&PageNo=";
//  private static Comparator<Item> comparator = (Item o1, Item o2) ->  Float.valueOf(o2.getMark()).compareTo(Float.valueOf(o1.getMark()));
  private static Comparator<Item> comparator = new Comparator<Item>() {
    @Override
    public int compare(Item o1, Item o2) {
      return Float.valueOf(o2.getMark()).compareTo(Float.valueOf(o1.getMark()));
    }
  };

  protected class Item {
    public String text;
    public String link;
    public String mark;
    public String download;
    public String getDownload() {
      return download;
    }

    public void setDownload(String download) {
      this.download = download;
    }

    public String getMark() {
      return mark;
    }

    public void setMark(String mark) {
      this.mark = mark;
    }

    public Item(String text, String link)
    {
      this.text = text;
      this.link = link;
      this.download="";
      this.mark="1.0";
    }

    public String getText() {
      return text;
    }

    public String getLink() {
      return link;
    }

    @Override
    public String toString() {
      return "CurrentThread="+Thread.currentThread().getId()+
              "Item{" +
              "text='" + text + '\'' +
              ", link='" + link + '\'' +
              ", mark='" + mark + '\'' +
              ", download='" + download + '\'' +
              '}';
    }
  }

  public Movie_dydytt() {
    super(comparator);
  }

  public ArrayList<Item> getMovieList(int page)
  {
    Document doc = null;
    ArrayList list = new ArrayList<Item>();

    try {
      doc = Jsoup.connect(search+""+page).get();
      Elements moveList = doc.select("div.co_content8").first().select("ul").select("table");
      for(Element e : moveList) {
        Element href =  e.select("tr").first().select("a").first();
        String link = href.attr("href");

        Item item = new Item(href.text(), url + link);
        list.add(item);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }


  public ArrayList<Item> getMovieDownloadUrl(ArrayList<Item> movieList)
  {
    ArrayList<Item> downloadList = new ArrayList<>();
    for(Item item: movieList) {
      try {
        Document doc = Jsoup.connect(item.getLink()).get();
        Element href= doc.select("div#Zoom").first().select("table").first().select("tr").first().select("a").first();
        //download url
        String link = href.text();
        if (link.endsWith(".exe") || link.endsWith(".rar") || link.endsWith("html")) continue;
        item.setDownload(link);
        //get movie mark grade
        Element e = doc.select("div#read_tpc").first();
        if (null == e) e = doc.select("div#Zoom").select("p").first();
        String[] content = e.text().split("◎");
        String mark="1.0";
        int skip = "评分".length();
        for(String line : content){
          int index = line.indexOf("评分");
          int i = index+skip;
          int start=-1;
          if (index != -1) {
            for(; i < line.length(); i++) {
              if (-1 == start && (line.charAt(i) <= '9' && line.charAt(i) >= '0' )) {
                start = i;
              }else if (line.charAt(i) == '/') {
                break;
              };
            }
            mark = line.substring(start, i).trim();
            try {
              Float n = Float.valueOf(mark);
            }catch (Exception e1) {
              e1.printStackTrace();
              mark="1.0";
            }

            break;
          }
        }
        item.setMark(mark);
      } catch (Exception e) {
        e.printStackTrace();
      }finally {
        if (item.getDownload().isEmpty()) continue;;
        System.out.println(item.toString());
        downloadList.add(item);
      }
    }

    return downloadList;
  }

  @Override
  public ArrayList<Item> run(int page) {
    return getMovieDownloadUrl(getMovieList(page));
  }

  @Override
  public void toHtml(ArrayList<Item> total, OutputStreamWriter bw) throws IOException {
      for(Item item: total) {
        bw.write("      <tr>\n");
        bw.write("        <td><a href="+item.getDownload()+">"+item.getText()+"</a></td>\n");
        bw.write("        <td>"+item.getMark()+"</td>\n");
        bw.write("      </tr>\n");
      }
  }
}


