import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class B {
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

        public double nextDouble() {
            return scanner.nextDouble();
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

    static class Data {
        int caseNum;
        int n;
        double v, x;
        double[] r;
        double[] c;
        double ans;

        Data(int caseNum) {
            this.caseNum = caseNum;
        }

        void feed(Reader reader) {
            n = reader.nextInt();
            v = reader.nextDouble();
            x = reader.nextDouble();
            r = new double[n];
            c = new double[n];
            for (int i = 0; i < n; i++) {
                r[i] = reader.nextDouble();
                c[i] = reader.nextDouble();
            }
        }

        void print(Writer writer) {
            writer.write(String.format("Case #%d: ", caseNum));
            if (ans < 0) {
                writer.writeLine("IMPOSSIBLE");
            } else {
                writer.writeLine(String.format("%.8f", ans));
            }
        }
    }

    static class Solver implements Runnable {
        Data data;

        Solver(Data data) {
            this.data = data;
        }

        class Source implements Comparable<Source> {
            double r;
            double c;

            Source(double r, double c) {
                this.r = r;
                this.c = c;
            }

            @Override
            public int compareTo(Source o) {
                return Double.compare(c, o.c);
            }
        }

        @Override
        public void run() {
            int n = data.n;

            Source[] sources = new Source[n];
            for (int i = 0; i < n; i++) {
                sources[i] = new Source(data.r[i], data.c[i]);
            }
            Arrays.sort(sources);
            if (data.x < sources[0].c - 1e-10 || data.x > sources[n-1].c + 1e-10) {
                data.ans = -1;
            } else {
                data.ans = solve(sources);
            }
        }

        double solve(Source[] sources) {
            int n = data.n;
            double x = data.x, v = data.v;
            if (eq(sources[0].c, x) || eq(sources[n-1].c, x)) {
                double r = 0;
                for (int i = 0; i < n; i++) {
                    if (eq(sources[i].c, x)) {
                        r += sources[i].r;
                    }
                }
                return v / r;
            }
            double min = 0, max = v * 20000, mid;
            while (max - min > 1e-8) {
                mid = (max + min) / 2;
                double small = 0, vs = v;
                for (int i = 0; i < n; i++) {
                    double tt;
                    if (vs > sources[i].r * mid) {
                        tt = mid;
                        vs -= sources[i].r * tt;
                    } else {
                        tt = vs / sources[i].r;
                        vs = 0;
                    }
                    small += sources[i].r * tt * sources[i].c;
                }
                double big = 0, vg = v;
                for (int i = n - 1; i >= 0; i--) {
                    double tt;
                    if (vg > sources[i].r * mid) {
                        tt = mid;
                        vg -= sources[i].r * tt;
                    } else {
                        tt = vg / sources[i].r;
                        vg = 0;
                    }
                    big += sources[i].r * tt * sources[i].c;
                }
                if (vs <= 1e-16 && small <= v * x + 1e-16 && big >= v * x - 1e-16) {
                    max = mid;
                } else {
                    min = mid;
                }
            }
            return max;
        }

        boolean eq(double a, double b) {
            return Math.abs(a - b) < 1e-10;
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
