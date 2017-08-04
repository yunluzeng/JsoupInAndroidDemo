package com.android.mypulldatatest;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by yansutao on 23/07/2017.
 */
public class Jobs extends Crawl<Jobs.Item> {
    private String keyword = "";
    private String search = "http://search.51job.com/list/020000,000000,0000,00,9,99,_keyword_,2,_pageNum_.html?" +
            "lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=03%2C04%2C05%2C06%2C07" +
            "&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=";

    private static Comparator<Item> comparator = (Item o1, Item o2) -> Float.valueOf(o2.getSalary()).compareTo(Float.valueOf(o1.getSalary()));

    protected class Item {

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Item(String title, String href, String company, String location, String salary, String publishDate) {
            this.title = title;
            this.href = href;
            this.company = company;
            this.location = location;
            this.salaryRange = salary;
            this.unit = "万";
            if (null != salary && !salary.isEmpty()) {
                //part0 as salary, part1 as per month or year
                String[] parts = salary.split("/");
                if (parts.length == 2) {
                    float nSalary = 0;
                    String[] salaryWithUnitParts = parts[0].split("-");
                    String salaryWithUnit = salaryWithUnitParts.length > 1 ? salaryWithUnitParts[1] : salaryWithUnitParts[0];
                    int index = 0;
                    for (Character c : salaryWithUnit.toCharArray()) {
                        if ((c >= Character.valueOf('0') && c <= Character.valueOf('9')) || c == '.') {
                            index++;
                        } else {
                            break;
                        }
                    }
                    salary = salaryWithUnit.substring(0, index);
                    unit = salaryWithUnit.substring(index);
                    if (unit.equals("千")) {
                        salary = "" + Float.valueOf(salary) / 10;
                        unit = "万";
                    } else if (unit.equals("元")) {
                        salary = "" + Float.valueOf(salary) / 10000;
                        unit = "万";
                    }

                    if (parts[1].equals("年")) {
                        nSalary = Float.valueOf(salary) / 12;
                        salary = "" + nSalary;
                    }
                }
            } else {
                salary = "0";
            }
            this.salary = salary;
            this.publishDate = publishDate;
        }

        private String title;
        private String href;
        private String company;
        private String location;
        private String unit;
        private String salary;
        private String salaryRange;
        private String publishDate;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getSalaryRange() {
            return salaryRange;
        }

        public void setSalaryRange(String salaryRange) {
            this.salaryRange = salaryRange;
        }

        @Override
        public String toString() {
            return "CurrentThread=" + Thread.currentThread().getId() +
                    "-Item{" +
                    "title='" + title + '\'' +
                    ", href='" + href + '\'' +
                    ", company='" + company + '\'' +
                    ", location='" + location + '\'' +
                    ", salary='" + salary + '\'' +
                    ", publishDate='" + publishDate + '\'' +
                    '}';
        }

    }

    public Jobs(String keyword) {
        super(comparator);
        try {
            this.keyword = URLEncoder.encode(URLEncoder.encode(keyword, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        search = search.replaceAll("_keyword_", this.keyword);
    }

    private ArrayList<Item> getJobList(int page) {
        ArrayList<Item> list = new ArrayList<>();
        try {
            String url = search.replaceAll("_pageNum_", "" + page);

            Document doc = Jsoup.connect(url).get();
            Elements jobList = doc.select("div#resultList").first().select("div.el");
            for (Element e : jobList) {
                if (null == e.select("p") || e.select("p").size() == 0) continue;
                String title = e.select("p.t1").first().select("a").attr("title");
                String href = e.select("p.t1").first().select("a").attr("href");
                String company = e.select("span.t2").first().text();
                String location = e.select("span.t3").first().text();
                String salary = e.select("span.t4").first().text();
                String publishDate = e.select("span.t5").first().text();
                Item item = new Item(title, href, company, location, salary, publishDate);
                System.out.println(item.toString());
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ArrayList<Item> run(int page) {
        return getJobList(page);
    }

    @Override
    public void toHtml(ArrayList<Item> total, OutputStreamWriter bw) throws IOException {
        for (Item item : total) {
            bw.write("      <tr>\n");
            bw.write("        <td><a href=" + item.getHref() + ">" + item.getTitle() + "</a></td>\n");
            bw.write("        <td>" + item.getSalary() + item.getUnit() + "</td>\n");
            bw.write("        <td>" + item.getCompany() + "</td>\n");
            bw.write("        <td>" + item.getLocation() + "</td>\n");
            bw.write("        <td>" + item.getPublishDate() + "</td>\n");
            bw.write("      </tr>\n");
        }
    }
}
