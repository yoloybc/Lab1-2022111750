import java.io.*;
import java.util.*  ;

public class Lab1_GraphProcessor {
    static class Graph {
        Map<String, Map<String, Integer>> adj = new HashMap<>();
        Set<String> nodes = new HashSet<>();

        void addEdge(String from, String to) {
            nodes.add(from);
            nodes.add(to);
            adj.putIfAbsent(from, new HashMap<>());
            adj.get(from).put(to, adj.get(from).getOrDefault(to, 0) + 1);
        }
    }

    static Graph graph = new Graph();
    static Random random = new Random();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the input file path: ");
        String path = scanner.nextLine();
        buildGraph(path);

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Show Graph\n2. Query Bridge Words\n3. Generate New Text\n4. Shortest Path\n5. PageRank\n6. Random Walk\n0. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> showDirectedGraph(graph);
                case 2 -> {
                    System.out.print("Word1: "); String w1 = scanner.next();
                    System.out.print("Word2: "); String w2 = scanner.next();
                    System.out.println(queryBridgeWords(w1, w2));
                }
                case 3 -> {
                    System.out.print("Input text: ");
                    String line = scanner.nextLine();
                    System.out.println(generateNewText(line));
                }
                case 4 -> {
                    System.out.print("Input word1 word2 (or only one word to see all paths): "); String w1 = scanner.next();
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        String w2 = line;
                        System.out.println(calcShortestPath(w1, w2));
                    } else {
                        System.out.println(calcShortestPathsFrom(w1));
                    }
                }
                case 5 -> {
                    System.out.print("Word: ");
                    String word = scanner.next();
                    System.out.println("PageRank: " + String.format("%.4f", calPageRank(word)));
                }
                case 6 -> System.out.println(randomWalk());
                case 0 -> System.exit(0);
            }
        }
    }

    static void buildGraph(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        List<String> words = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("[^a-zA-Z]", " ").toLowerCase();
            words.addAll(Arrays.asList(line.split("\\s+")));
        }
        reader.close();
        for (int i = 0; i < words.size() - 1; i++) {
            if (!words.get(i).isEmpty() && !words.get(i+1).isEmpty()) {
                graph.addEdge(words.get(i), words.get(i + 1));
            }
        }
    }

    public static void showDirectedGraph1(Graph G) {
        for (String from : G.adj.keySet()) {
            for (Map.Entry<String, Integer> entry : G.adj.get(from).entrySet()) {
                System.out.printf("%s -> %s [weight=%d]\n", from, entry.getKey(), entry.getValue());
            }
        }



    }

    public static void showDirectedGraph(Graph G) {
        // 生成DOT图形描述语言
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    node [shape=circle];\n");

        // 添加所有边
        for (String from : G.adj.keySet()) {
            for (Map.Entry<String, Integer> entry : G.adj.get(from).entrySet()) {
                String to = entry.getKey();
                int weight = entry.getValue();
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"w=%d\"];\n",
                        escapeQuotes(from), escapeQuotes(to), weight));
            }
        }

        dot.append("}");

        // 生成图片文件
        try {
            // 写入DOT文件
            File dotFile = new File("graph.dot");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFile))) {
                writer.write(dot.toString());
            }

            // 调用Graphviz生成图片（需安装Graphviz并添加至PATH）
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "graph.dot", "-o", "graph.png");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if(exitCode == 0) {
                System.out.println("图形文件已生成: graph.png");
            } else {
                System.err.println("图形生成失败，请检查Graphviz安装");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 处理特殊字符转义
    private static String escapeQuotes(String str) {
        return str.replace("\"", "\\\"");
    }

    public static String queryBridgeWords(String word1, String word2) {
        boolean hasW1 = graph.nodes.contains(word1);
        boolean hasW2 = graph.nodes.contains(word2);
        if (!hasW1 && !hasW2)
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        if (!hasW1)
            return "No \"" + word1 + "\" in the graph!";
        if (!hasW2)
            return "No \"" + word2 + "\" in the graph!";

        Set<String> bridges = new HashSet<>();
        if (graph.adj.containsKey(word1)) {
            for (String mid : graph.adj.get(word1).keySet()) {
                if (graph.adj.containsKey(mid) && graph.adj.get(mid).containsKey(word2)) {
                    bridges.add(mid);
                }
            }
        }
        if (bridges.isEmpty()) return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: " + String.join(", ", bridges);
    }

    public static String generateNewText(String inputText) {
        String[] tokens = inputText.toLowerCase().replaceAll("[^a-zA-Z ]", " ").split("\\s+");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            result.add(tokens[i]);
            String bridge = null;
            Set<String> bridges = new HashSet<>();
            if (graph.adj.containsKey(tokens[i])) {
                for (String mid : graph.adj.get(tokens[i]).keySet()) {
                    if (graph.adj.containsKey(mid) && graph.adj.get(mid).containsKey(tokens[i+1]))
                        bridges.add(mid);
                }
            }
            if (!bridges.isEmpty()) {
                List<String> list = new ArrayList<>(bridges);
                bridge = list.get(random.nextInt(list.size()));
                result.add(bridge);
            }
        }
        result.add(tokens[tokens.length - 1]);
        return String.join(" ", result);
    }

    public static String calcShortestPath(String word1, String word2) {
        if (!graph.nodes.contains(word1) || !graph.nodes.contains(word2)) return "Word not in graph";
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        for (String node : graph.nodes) dist.put(node, Integer.MAX_VALUE);
        dist.put(word1, 0); pq.add(word1);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (!graph.adj.containsKey(u)) continue;
            for (Map.Entry<String, Integer> entry : graph.adj.get(u).entrySet()) {
                String v = entry.getKey();
                int alt = dist.get(u) + entry.getValue();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        if (!dist.containsKey(word2) || dist.get(word2) == Integer.MAX_VALUE) return "No path";
        LinkedList<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = prev.get(at)) path.addFirst(at);
        return String.join(" -> ", path) + " (Length: " + dist.get(word2) + ")";
    }

    public static String calcShortestPathsFrom(String word1) {
        if (!graph.nodes.contains(word1)) return "Word not in graph";
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        for (String node : graph.nodes) dist.put(node, Integer.MAX_VALUE);
        dist.put(word1, 0); pq.add(word1);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (!graph.adj.containsKey(u)) continue;
            for (Map.Entry<String, Integer> entry : graph.adj.get(u).entrySet()) {
                String v = entry.getKey();
                int alt = dist.get(u) + entry.getValue();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String target : graph.nodes) {
            if (target.equals(word1)) continue;
            if (dist.get(target) == Integer.MAX_VALUE) {
                sb.append("No path from ").append(word1).append(" to ").append(target).append("\n");
            } else {
                LinkedList<String> path = new LinkedList<>();
                for (String at = target; at != null; at = prev.get(at)) path.addFirst(at);
                sb.append(String.join(" -> ", path)).append(" (Length: ").append(dist.get(target)).append(")\n");
            }
        }
        return sb.toString();
    }

    public static Double calPageRank(String word) {
        if (!graph.nodes.contains(word)) return 0.0;
        double d = 0.85;
        int N = graph.nodes.size();
        Map<String, Double> pr = new HashMap<>();
        for (String node : graph.nodes) pr.put(node, 1.0 / N);

        for (int i = 0; i < 100; i++) {
            Map<String, Double> newPr = new HashMap<>();
            for (String node : graph.nodes) newPr.put(node, (1 - d) / N);
            for (String u : graph.nodes) {
                Set<String> Bu = new HashSet<>();
                for (String v : graph.adj.keySet()) {
                    if (graph.adj.get(v).containsKey(u)) {
                        Bu.add(v);
                    }
                }
                for (String v : Bu) {
                    int Lv = graph.adj.get(v).size();
                    newPr.put(u, newPr.get(u) + d * pr.get(v) / Lv);
                }
            }
            pr = newPr;
        }
        return pr.getOrDefault(word, 0.0);
    }

    public static String randomWalk() {
        List<String> visited = new ArrayList<>();
        String current = graph.nodes.stream().skip(random.nextInt(graph.nodes.size())).findFirst().orElse(null);
        if (current == null) return "Empty graph";
        Set<String> visitedEdges = new HashSet<>();
        while (current != null && graph.adj.containsKey(current)) {
            visited.add(current);
            List<String> neighbors = new ArrayList<>(graph.adj.get(current).keySet());
            if (neighbors.isEmpty()) break;
            String next = neighbors.get(random.nextInt(neighbors.size()));
            String edge = current + "->" + next;
            if (visitedEdges.contains(edge)) break;
            visitedEdges.add(edge);
            current = next;
        }
        try (PrintWriter out = new PrintWriter("random_walk.txt")) {
            out.println(String.join(" ", visited));
        } catch (IOException e) {
            return "Error writing file.";
        }
        return String.join(" ", visited);
    }
}