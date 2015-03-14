package com.codepoetics.protonpack;

import java.util.*;
import java.util.function.Consumer;

public class GroupRunsSpliterator<T> implements Spliterator<List<T>> {
    private Spliterator<T> source;
    private Comparator<T> comparator;

    class Box<Y>{
        public Y item;
    }

    Optional<T> last = Optional.empty();

    public GroupRunsSpliterator(Spliterator<T> source, Comparator<T> comparator) {
        this.source = source;
        this.comparator = comparator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<T>> action) {
        Box<T> current = new Box<>();

        List<T> neighbors = new LinkedList<>();

        Boolean runBroken = false;

        if(last.isPresent()){
            neighbors.add(last.get());
        }

        for(;;){
            if(source.tryAdvance(i -> current.item = i)) {
                if( !last.isPresent() || comparator.compare(current.item, last.get()) == 0) {
                    neighbors.add(current.item);
                }
                else{
                    runBroken = true;
                }

                last = Optional.of(current.item);

                if(runBroken){
                    action.accept(neighbors);

                    return true;
                }
            }
            // read to the end and its the last run
            else if(!neighbors.isEmpty()){
                action.accept(neighbors);

                last = Optional.empty();

                return true;
            }
            // source is empty
            else {
                return false;
            }
        }
    }

    @Override
    public Spliterator<List<T>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public int characteristics() {
        return source.characteristics() &
                ~(Spliterator.SIZED | Spliterator.ORDERED);
    }
}
