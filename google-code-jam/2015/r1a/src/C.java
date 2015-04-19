import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class C {
    static class Reader {
        Scanner scanner = new Scanner(new BufferedInputStream(System.in));

        public String next() {
            return scanner.next();
        }

        public int nextInt() {
            return scanner.nextInt();
        }

        public long nextLong() {
            return scanner.nextLong();
        }

        public String nextLine() {
            return scanner.nextLine();
        }
    }

    static class Writer {
        PrintStream out = System.out;

        public void write(String s) {
            out.print(s);
        }

        public void write(char c) {
            out.print(c);
        }

        public void write(byte b) {
            out.print(b);
        }

        public void write(int i) {
            out.print(i);
        }

        public void write(long l) {
            out.print(l);
        }

        public void writeLine(String s) {
            out.println(s);
        }

        public void writeLine(char c) {
            out.println(c);
        }

        public void writeLine(byte b) {
            out.println(b);
        }

        public void writeLine(int i) {
            out.println(i);
        }

        public void writeLine(long l) {
            out.println(l);
        }
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Data {
        int caseNum;
        int n;
        Point[] pts;
        int[] ans;

        Data(int caseNum) {
            this.caseNum = caseNum;
        }

        void feed(Reader reader) {
            n = reader.nextInt();
            pts = new Point[n];
            for (int i = 0; i < n; i++) {
                int x = reader.nextInt();
                int y = reader.nextInt();
                pts[i] = new Point(x, y);
            }
            ans = new int[n];
        }

        void print(Writer writer) {
            writer.writeLine(String.format("Case #%d: ", caseNum));
            for (int i = 0; i < n; i++) {
                writer.writeLine(ans[i]);
            }
        }
    }

    static class Solver implements Runnable {
        Data data;

        Solver(Data data) {
            this.data = data;
        }

        boolean check(List<Point> list, Point p) {
            if (list.size() == 0) {
                return true;
            }
            double[] ang = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                ang[i] = Math.atan2(list.get(i).y - p.y, list.get(i).x - p.y);
            }
            Arrays.sort(ang);
            return ang[ang.length-1] - ang[0] <= Math.PI + 0.00000001;
        }

        @Override
        public void run() {
            int n = data.n;
            Point[] pts = data.pts;
            double eps = 1e-9;

            for (int i = 0; i < n; i++) {
                int min = n - 1;
                int m = 0;
                double[] ang = new double[n-1];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        ang[m++] = Math.atan2(pts[j].y - pts[i].y, pts[j].x - pts[i].x);
                    }
                }
                Arrays.sort(ang);
                for (int j = 0; j < m; j++) {
                    double next = ang[j] + Math.PI;
                    if (next >= Math.PI - eps) {
                        next = ang[j] - Math.PI;
                    }
                    int pos = -(Arrays.binarySearch(ang, next + eps) + 1);
                    int cnt = pos - j;
                    if (pos <= j) {
                        cnt = pos + m - j;
                    }
                    min = Math.min(min, m - cnt);
                }

                data.ans[i] = min;
            }

        }
    }

    static class SingleThread {
        void execute() {
            Reader reader = new Reader();
            Writer writer = new Writer();

            int cases = reader.nextInt();
            for (int i = 1; i <= cases; i++) {
                Data data = new Data(i);
                data.feed(reader);
                new Solver(data).run();
                data.print(writer);
            }
        }
    }

    static class MultipleThread {
        int threads;

        MultipleThread(int threads) {
            this.threads = threads;
        }

        void execute() {
            Reader reader = new Reader();
            int cases = reader.nextInt();
            Data[] data = new Data[cases];
            Solver[] solvers = new Solver[cases];
            for (int i = 0; i < cases; i++) {
                data[i] = new Data(i + 1);
                data[i].feed(reader);
                solvers[i] = new Solver(data[i]);
            }

            Future[] futures = new Future[cases];
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < cases; i++) {
                futures[i] = executor.submit(solvers[i]);
            }
            for (int i = 0; i < cases; i++) {
                if (!futures[i].isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            executor.shutdown();

            Writer writer = new Writer();
            for (int i = 0; i < cases; i++) {
                data[i].print(writer);
            }
        }
    }

    public static void main(String[] args) {
        int threads = 1;
        if (args.length > 0) {
            try {
                threads = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                threads = 1;
            }
        }

        if (threads > 1) {
            new MultipleThread(threads).execute();
        } else {
            new SingleThread().execute();
        }
    }
}
