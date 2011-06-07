package edu.kaist.uilab.asc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import edu.kaist.uilab.asc.crawler.blogposts.PostCollector;
import edu.kaist.uilab.asc.data.Document;
import edu.kaist.uilab.asc.data.Sentence;
import edu.kaist.uilab.asc.data.SentiWord;
import edu.kaist.uilab.asc.prior.GraphInputProducer;
import edu.kaist.uilab.asc.stemmer.EnglishStemmer;
import edu.kaist.uilab.asc.stemmer.FrenchStemmer;

/**
 * Main application.
 * 
 * @author trung nguyen (trung.ngvan@gmail.com)
 */
public class Application {
  private static final String UTF8 = "utf-8";
  private static final String EN_PATTERN = ".*[^a-zA-Z_].*";
  private static final String FR_PATTERN = ".*[0-9()<>,&;\"].*";
  String inputDir = "C:/datasets/asc/reviews/ElectronicsReviews1";
  // String inputDir = "C:/datasets/asc/reviews/MovieReviews";
  // String inputDir = "C:/datasets/asc/blogs/obama";
  static final String wordcountFile = "WordCount.csv";
  static final String wordlistFile = "WordList.txt";
  static final String enDocuments = "BagOfSentences_en.txt";
  static final String otherDocuments = "BagOfSentences_other.txt";
  // vacuum, coffee, camera
  String datasetName = "Stemmed-InitY0";
  final String documentListFile = "DocumentList.txt";
  final String englishCorpus = inputDir + "/docs_en.txt";
  final String frCorpus = inputDir + "/docs_other.txt";
  // final String stopWordFile = inputDir + "/StopWords.txt";
  final String enStopWordFile = inputDir + "/StopStems_en.txt";
  final String frStopWordFile = inputDir + "/StopStems_fr.txt";
  // final String sentiFilePrefix = "SentiWords-";
  final String sentiFilePrefix = "SentiStems-";
  final String dictionaryFile = "C:/datasets/asc/dict/en-fr.txt";
  final String similarityGraphFile = "graph.txt";

  int minWordLength = 3;
  int maxWordLength = 30;
  int minSentenceLength = 4;
  int maxSentenceLength = 50;
  int minWordOccur = 3;
  int minDocLength = 15; // used 20

  int numTopics = 5;
  int numSenti = 2;
  double alpha = 0.1;
  double[] gammas = new double[] { 1, 0.5 };
  int numIterations = 1500;
  int savingInterval = 200;
  int optimizationInterval = 100;
  int burnIn = 500;
  int numThreads = 1;
  int numEnglishDocuments;

  public static void main(String[] args) throws Exception {
    Application app = new Application();
    app.parseDocuments();
    app.runModel(4);
  }

  /**
   * @param numEnglishDocs
   * @param numFrenchDocs
   * @throws IOException
   */
  static void getRandomDocuments(int numEnglishDocs, int numFrenchDocs)
      throws IOException {
    PrintWriter out = new PrintWriter(new OutputStreamWriter(
        new FileOutputStream("C:/datasets/asc/blogs/obama/docs_en.txt"),
        "utf-8"));
    File[] docs = new File("C:/datasets/asc/blogs/obama_en").listFiles();
    for (int i = 0; i < numEnglishDocs; i++) {
      File doc = docs[(int) (Math.random() * docs.length)];
      BufferedReader in = new BufferedReader(new InputStreamReader(
          new FileInputStream(doc), "utf-8"));
      out.println(doc);
      out.println();
      String line;
      in.readLine(); // ignore url
      while ((line = in.readLine()) != null) {
        out.print(line.replaceAll("&.*;", " ").replaceAll("<!--.*-->", "")
            .replaceAll("You may use these HTML.*", "")
            + " ");
      }
      out.println();
      in.close();
    }
    out.close();
    out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
        "C:/datasets/asc/blogs/obama/docs_other.txt"), "utf-8"));
    docs = new File("C:/datasets/asc/blogs/obama_fr").listFiles();
    for (int i = 0; i < numFrenchDocs; i++) {
      File doc = docs[(int) (Math.random() * docs.length)];
      BufferedReader in = new BufferedReader(new InputStreamReader(
          new FileInputStream(doc), "utf-8"));
      out.println(doc);
      out.println();
      String line;
      in.readLine(); // ignore url
      while ((line = in.readLine()) != null) {
        out.print(PostCollector.toFrench(line).replaceAll("&.*;", " ")
            .replaceAll("�", "'").replaceAll("<!--.*-->", " ")
            .replaceAll("Vous pouvez utiliser ces balises et attributs.*", "")
            + " ");
      }
      out.println();
      in.close();
    }
    out.close();

  }

  /**
   * Parses all documents from the dataset.
   */
  public void parseDocuments() throws IOException {
    System.out.println("Parsing documents...");
    DocumentParser parser = new DocumentParser(minWordLength, maxWordLength,
        minSentenceLength, maxSentenceLength, minWordOccur, minDocLength);
    parser.addReplacePattern("[http|ftp]://[\\S]*", " ");
    parser.addReplacePattern("(not|n't|without|never)[\\s]+(very|so|too|much|"
        + "quite|even|that|as|as much|a|the|to|really)[\\s]+", " not_");
    parser.addReplacePattern("(not|n't|without|never|no)[\\s]+", " not_");
    parser.addReplacePattern("(pas|non|sans|n'est|pas de)[\\s]+", " pas_");
    parser.addReplacePattern(
        "(pas|ne|non|jamais|sans|n'est)[\\s]+(tr�s|si|trop|beaucoup|toujours"
            + "assez|si|que|ausi|vraiment)[\\s]+", " pas_");
    parser.addReplacePattern("[()<>\\[\\],~&;:\"\\-/=*#@^+'`]", " ");

    // English corpus
    parser.setLocale(Locale.ENGLISH);
    parser.setWordReplacePattern(new String[] { EN_PATTERN, null });
    parser.setStopWords(enStopWordFile);
    parser.setStemmer(new EnglishStemmer());
    parseCorpus(englishCorpus, parser);
    parser.writeAndClearsStemMap(inputDir + "/Stem_en.txt");

    // French corpus
    parser.setLocale(Locale.FRENCH);
    parser.setWordReplacePattern(new String[] { FR_PATTERN, null });
    parser.setStopWords(frStopWordFile);
    parser.setStemmer(new FrenchStemmer());
    parseCorpus(frCorpus, parser);
    parser.writeAndClearsStemMap(inputDir + "/Stem_fr.txt");

    parser.filterWords();
    parser.writeOutFiles(inputDir);
    System.out.println(parser);
  }

  void parseCorpus(String corpus, DocumentParser parser) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(corpus), UTF8));
    double rating;
    while (in.readLine() != null) {
      try {
        rating = Double.parseDouble(in.readLine());
      } catch (NumberFormatException e) {
        rating = -1.0;
      }
      parser.build(in.readLine(), rating);
    }
    in.close();
  }

  TreeSet<LocaleWord> readWords(String file, String charset, Locale locale)
      throws IOException {
    TreeSet<LocaleWord> words = new TreeSet<LocaleWord>();
    String line;
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(file), charset));
    while ((line = reader.readLine()) != null) {
      words.add(new LocaleWord(line, locale));
    }
    return words;
  }

  public void runModel(int version) throws IOException {
    Vector<LocaleWord> wordList = readWordList(inputDir + "/" + wordlistFile);
    Vector<Document> documents = new Vector<Document>();
    readDocuments(documents, inputDir + "/" + enDocuments);
    numEnglishDocuments = documents.size();
    readDocuments(documents, inputDir + "/" + otherDocuments);
    ArrayList<TreeSet<LocaleWord>> list = new ArrayList<TreeSet<LocaleWord>>(
        numSenti);
    for (int s = 0; s < numSenti; s++) {
      list.add(readWords(inputDir + "/" + sentiFilePrefix + s + "_en.txt",
          UTF8, Locale.ENGLISH));
      list.get(s).addAll(
          readWords(inputDir + "/" + sentiFilePrefix + s + "_fr.txt", UTF8,
              Locale.FRENCH));
    }
    ArrayList<TreeSet<Integer>> sentiClasses = new ArrayList<TreeSet<Integer>>(
        list.size());
    for (Set<LocaleWord> sentiWordsStr : list) {
      TreeSet<Integer> sentiClass = new TreeSet<Integer>();
      for (LocaleWord word : sentiWordsStr) {
        sentiClass.add(wordList.indexOf(word));
      }
      sentiClasses.add(sentiClass);
    }
    printConfiguration(documents.size(), wordList.size());
    GraphInputProducer graphProducer = new GraphInputProducer(inputDir + "/"
        + wordlistFile, dictionaryFile);
    graphProducer.write(inputDir + "/" + similarityGraphFile);
    AbstractAsc model = null;
    switch (version) {
    case 1:
      model = new Asc1(numTopics, numSenti, wordList, documents,
          numEnglishDocuments, sentiClasses, alpha, gammas, inputDir + "/"
              + similarityGraphFile);
      break;
    case 2:
      model = new Asc2(numTopics, numSenti, wordList, documents,
          numEnglishDocuments, sentiClasses, alpha, gammas, inputDir + "/"
              + similarityGraphFile);
      break;
    case 3:
      model = new Asc3(numTopics, numSenti, wordList, documents,
          numEnglishDocuments, sentiClasses, alpha, gammas, inputDir + "/"
              + similarityGraphFile);
      break;
    case 4:
      model = new Asc4(numTopics, numSenti, wordList, documents,
          numEnglishDocuments, sentiClasses, alpha, gammas, inputDir + "/"
              + similarityGraphFile);
      // model = new
      // Asc4("C:/datasets/asc/reviews/MovieReviews/Stemmed-InitY0(V4)-T20-S2-A0.10-I2000(rf-0.10)/2000/model.gz",
      // 2000);
    default:
      break;
    }
    model.setOutputDir(String.format("%s/%s(V%d)-T%d-G%.2f,%.2f-I%d", inputDir,
        datasetName, version, numTopics, gammas[0], gammas[1], numIterations));
    model.gibbsSampling(numIterations, savingInterval, burnIn,
        optimizationInterval, numThreads);
  }

  void printConfiguration(int numDocuments, int vocabSize) {
    System.out.println("Dataset name: " + datasetName);
    System.out.println("Input Dir: " + inputDir);
    System.out.printf("Documents: %d (en = %d)\n", numDocuments,
        numEnglishDocuments);
    System.out.println("Unique Words: " + vocabSize);
    System.out.println("Topics: " + numTopics);
    System.out.println("Alpha: " + alpha);
    System.out.println();
    System.out.print("Gamma: ");
    for (double gamma : gammas) {
      System.out.printf("%.4f ", gamma);
    }
    System.out.println();
    System.out.println("Iterations: " + numIterations);
    System.out.println("Threads: " + numThreads);
  }

  /**
   * Reads all words from the specified file.
   * 
   * @param file
   * @return
   * @throws IOException
   */
  public static Vector<LocaleWord> readWordList(String file) throws IOException {
    Vector<LocaleWord> wordList = new Vector<LocaleWord>();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(file), UTF8));
    String line;
    while ((line = in.readLine()) != null) {
      if (line != "") {
        wordList.add(new LocaleWord(line));
      }
    }
    in.close();
    return wordList;
  }

  /**
   * Reads documents from the specified corpus (file) into the given vector of
   * documents <code>documents</code>.
   * 
   * @param documents
   * @param corpus
   * @return
   * @throws IOException
   */
  public static void readDocuments(Vector<Document> documents, String corpus)
      throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(corpus));
    String line;
    while ((line = in.readLine()) != null) {
      Document document = new Document(documents.size());
      StringTokenizer st = new StringTokenizer(line);
      document.setRating(Double.valueOf(st.nextToken()));
      int numSentences = Integer.valueOf(st.nextToken());
      for (int s = 0; s < numSentences; s++) {
        Sentence sentence = new Sentence();
        line = in.readLine();
        st = new StringTokenizer(line);
        while (st.hasMoreElements()) {
          int wordNo = Integer.valueOf(st.nextToken());
          sentence.addWord(new SentiWord(wordNo));
        }
        document.addSentence(sentence);
      }
      documents.add(document);
    }
    in.close();
  }
}
