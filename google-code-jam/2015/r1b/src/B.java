import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.*;
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
        int n, r, c;
        int ans;

        Data(int caseNum) {
            this.caseNum = caseNum;
        }

        void feed(Reader reader) {
            r = reader.nextInt();
            c = reader.nextInt();
            n = reader.nextInt();
        }

        void print(Writer writer) {
            writer.write(String.format("Case #%d: ", caseNum));
            writer.writeLine(String.format("%d", ans));
        }
    }

    static class Solver implements Runnable {
        Data data;

        Solver(Data data) {
            this.data = data;
        }

        @Override
        public void run() {
            int r = data.r, c = data.c, n = data.n;
            data.ans = Integer.MAX_VALUE;
            for (int k = 0; k <= 1; k++) {
                boolean[][] grid = new boolean[r][c];
                int left = n;
                for (int i = 0; i < r && left > 0; i++) {
                    for (int j = 0; j < c && left > 0; j++) {
                        if (i + j == k) {
                            continue;
                        }
                        if ((i == 0 || !grid[i - 1][j]) && (j == 0 || !grid[i][j - 1])) {
                            grid[i][j] = true;
                            left--;
                        }
                    }
                }
                data.ans = Math.min(data.ans, getUnhapiness(grid, r, c, left));
            }
        }

        int getUnhapiness(boolean[][] grid, int r, int c, int left) {
            int res = 0;
            List<Integer> unhappiness = new ArrayList<Integer>();
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    if (grid[i][j]) {
                        continue;
                    }
                    int up = 0;
                    if (i > 0 && grid[i-1][j]) {
                        up++;
                    }
                    if (i + 1 < r && grid[i+1][j]) {
                        up++;
                    }
                    if (j > 0 && grid[i][j-1]) {
                        up++;
                    }
                    if (j + 1 < c && grid[i][j+1]) {
                        up++;
                    }
                    unhappiness.add(up);
                }
            }
            Collections.sort(unhappiness);

            for (int i = 0; i < left; i++) {
                res += unhappiness.get(i);
            }
            return res;
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
