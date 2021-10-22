package crossowordgenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class WordConf {
    private String word;
    private Pair<WordConf, Integer> parent;
    private ArrayList<Pair<WordConf, Integer>> matches;
    private Orientation orientation;
    private Line line;
    private Boolean visited;

    Integer getMatchI(WordConf wordConf){
        return matches.stream().filter(i -> i.getFirst().equals(wordConf)).map(Pair::getSecond).reduce((a, b) -> {
                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                }).orElseThrow(NoSuchElementException::new);
    }

    boolean isVisited(){return visited;}
    boolean isVertical(){return orientation == Orientation.VERTICAL;}
    boolean isHorizontal(){return orientation == Orientation.HORIZONTAL;}

}
