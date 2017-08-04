package com.android.mypulldatatest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Crawl<E> {
    protected int maxPage = 50;
    private Comparator<E> comparator;

    public void setListener(OnCallbackListener listener) {
        this.listener = listener;
    }

    private OnCallbackListener listener;

    public int threadCount = 10;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }


    public abstract ArrayList<E> run(int page);

    public abstract void toHtml(ArrayList<E> total, OutputStreamWriter ow) throws IOException;

    public Crawl(Comparator<E> comparator, int maxPage) {
        this.comparator = comparator;
        this.maxPage = maxPage;
    }

    public Crawl(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public ArrayList<E> crawl() {
        ArrayList<E> total = new ArrayList<>();
        ExecutorService es = Executors.newWorkStealingPool(threadCount);
//        ExecutorService es = Executors.newSingleThreadExecutor(); //单线程池
        //Stream.iterate(1, item -> item + 1).limit(10)
        //先获取一个无限长度的正整数集合的Stream，然后取出前10个
        List<Future<ArrayList<E>>> results = Stream.iterate(1, page -> page + 1)
                .limit(maxPage)
                .map(page -> es.submit(() -> run(page)))
                .collect(Collectors.toList());
        results.forEach(item -> {
            try {
                total.addAll(item.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        total.sort(comparator);
        if(listener != null){
            listener.onCallback(total);
        }
        return total;
    }

    // 生成Html文件
    public void crawl(String fileName) {
        ArrayList<E> total = new ArrayList<>();
        ExecutorService es = Executors.newWorkStealingPool(threadCount);

        //Stream.iterate(1, item -> item + 1).limit(10)
        //先获取一个无限长度的正整数集合的Stream，然后取出前10个
        List<Future<ArrayList<E>>> results = Stream.iterate(1, page -> page + 1)
                .limit(maxPage)
                .map(page -> es.submit(() -> run(page)))
                .collect(Collectors.toList());
        results.forEach(item -> {
            try {
                total.addAll(item.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        total.sort(comparator);
        toHtmlAbstract(total, fileName);
    }

    public void toHtmlAbstract(ArrayList<E> total, String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        try (OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");) {
            ow.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head lang=\"en\">\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title></title>\n" +
                    "</head>\n" +
                    "<body align=\"center\">\n" +
                    "    <table  border=\"1\" align=\"center\" >");
            toHtml(total, ow);
            ow.write("    </table>" +
                    "  </body>\n" +
                    "</html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调接口 获取数据之后，通过该接口设置数据传递
     */
    interface OnCallbackListener<E> {
        void onCallback(ArrayList<E> results);
    }
}
