import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import spliterators.PageSpliterator;

import java.util.ArrayList;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Integer pageMaxSize = 1000;
    private static final Integer pageStart = 0;
    private static final Integer maxSize = 5000;

    public static void main(String[] args) {
        new PageSpliterator<>(
                new PageRequest(pageStart, pageMaxSize), pageable -> generate(pageable))
                .getStream(true)
                .forEach(entity -> logger.debug(entity.toString()));
    }


    private static Page<String> generate(Pageable pageable) {
        ArrayList<String> exampleList = new ArrayList<>();

        for (int i = 0; i < maxSize; i++) {
            exampleList.add(Generator.generate());
        }

        return new PageImpl<>(exampleList, pageable, maxSize);
    }

}
