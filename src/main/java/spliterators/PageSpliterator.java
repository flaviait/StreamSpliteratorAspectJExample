package spliterators;

import aspects.SpliteratorWatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PageSpliterator<T> implements Spliterator<T> {

    private static final int MAXSTEPSIZE = 8;
    private Page<T> page;
    private Function<Pageable, Page<T>> getPage;
    private Spliterator<T> pageSpliterator;
    private int stepSize = 1;

    public PageSpliterator(Pageable pageable, Function<Pageable, Page<T>> getPage) {
        this.getPage = getPage;
        this.page = this.getPage.apply(pageable);
        this.pageSpliterator = page.spliterator();
    }

    PageSpliterator(Pageable pageable, Function<Pageable, Page<T>> getPage, int stepSize) {
        this.getPage = getPage;
        this.page = this.getPage.apply(pageable);
        this.pageSpliterator = page.spliterator();
        if (stepSize < 0) {
            throw new IllegalArgumentException("stepSize cannot be negative");
        }
        this.stepSize = stepSize;
    }


    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (pageSpliterator.tryAdvance(action)) {
            return true;
        } else if (page.hasNext()) {
            this.page = getPage.apply(this.nextPageable());
            if (this.page.hasContent()) {
                this.pageSpliterator = this.page.spliterator();
                return this.tryAdvance(action);
            }
        }
        return false;
    }

    private Pageable nextPageable() {
        return new PageRequest(this.page.getNumber() + stepSize, this.page.getSize());
    }


    @Override
    @SpliteratorWatch
    public Spliterator<T> trySplit() {
        if (this.stepSize * 2 <= MAXSTEPSIZE) {
            PageSpliterator<T> split = new PageSpliterator<>(this.nextPageable(), this.getPage, this.stepSize * 2);
            this.stepSize *= 2;
            return split;
        } else {
            return null;
        }
    }

    public Double getProgress() {
        return Double.valueOf(this.page.getNumber() * this.page.getSize() / this.page.getTotalElements());
    }

    @Override
    public long estimateSize() {
        return this.page.getNumberOfElements();
    }

    @Override
    public int characteristics() {
        return this.pageSpliterator.characteristics();
    }

    public Stream<T> getStream(boolean parallel) {
        return StreamSupport.stream(this, parallel);
    }

}
