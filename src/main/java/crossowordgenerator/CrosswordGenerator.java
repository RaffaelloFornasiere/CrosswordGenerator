package crossowordgenerator;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class CrosswordGenerator {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        ArrayList<KeyWord> keyWords = new ArrayList<>(Arrays.stream(gson.fromJson(new FileReader("src/main/resources/words.json"), KeyWord[].class)).toList());
        keyWords.stream().map(KeyWord::getWord).forEach(System.out::println);
        var confs = setMatches(keyWords);


    }


    static ArrayList<WordConf> setMatches(ArrayList<KeyWord> keyWords) {
        ArrayList<WordConf> res = new ArrayList<>() {{
            keyWords.forEach(keyWord ->
                    add(WordConf.builder().word(keyWord.getWord()).build()));
        }};
        res.forEach(word -> res.forEach(word2 ->
        {
            var match = findMatch(word, word2);
            if (match != null)
                word.getMatches().add(new Pair<>(word2, match.getFirst()));
        }));
        return res;
    }

    static Pair<Integer, Integer> findMatch(WordConf wc1, WordConf wc2) {
        for (int i = 0; i < wc1.getWord().chars().toArray().length; i++) {
            for (int i1 = 0; i1 < wc2.getWord().chars().toArray().length; i1++) {
                if (wc1.getWord().chars().toArray()[i] == wc2.getWord().chars().toArray()[i1])
                    return new Pair<>(i, i1);
            }
        }
        return null;
    }


    static Pair<Integer, Integer> computeMatrixSize(ArrayList<WordConf> words) {
        WordConf word = words.get(0);
        word.setLine(new Line(0,
                0,
                word.isVertical() ? 1 : word.getWord().length(),
                word.isHorizontal() ? word.getWord().length() : 1));


        while (word != null) {
            Line current = word.getLine();
            for (int i = 0; i < word.getMatches().size(); i++) {
                Pair<WordConf, Integer> match = word.getMatches().get(i);
                if (word.isVertical()) {
                    match.getFirst().setLine(new Line(
                            current.x1 - match.getFirst().getParent().getSecond(),
                            current.y1 + match.getSecond(),
                            current.x1 - match.getFirst().getParent().getSecond()
                                    + match.getFirst().getWord().length(),
                            current.y1 + match.getSecond()
                    ));
                }
            }
            word = words.stream().filter(i -> i.getLine() != null && !i.isVisited()).findAny().orElse(null);
        }
        var lines = words.stream().map(WordConf::getLine).toList();
        Integer xSize = Collections.max(lines.stream().map(Line::getX1).collect(Collectors.toList())) -
                Collections.min(lines.stream().map(Line::getX1).collect(Collectors.toList()));
        Integer ySize = Collections.max(lines.stream().map(Line::getY2).collect(Collectors.toList())) -
                Collections.min(lines.stream().map(Line::getY2).collect(Collectors.toList()));
        Integer minX = Collections.min(lines.stream().map(Line::getX1).collect(Collectors.toList()));
        Integer minY = Collections.min(lines.stream().map(Line::getY1).collect(Collectors.toList()));
        words.forEach(w -> moveLine(w.getLine(), minX < 0 ? -minX : minX, minY < 0 ? -minY : minY));
        return new Pair<>(xSize, ySize);
    }


    static void moveLine(Line line, int qtyx, int qtyy) {
        line.x1 += qtyx;
        line.x2 += qtyx;
        line.y1 += qtyy;
        line.y2 += qtyy;
    }


}
