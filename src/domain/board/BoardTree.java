package domain.board;

import domain.model.SquareState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BoardTree {
    private interface Tree {
        Optional<SquareState> getElem(int i);

        Optional<Tree> setElem(int i, SquareState x);

        Optional<Tree> swap(int i, int j);

        Tree map(java.util.function.Function<SquareState, SquareState> f);

        List<SquareState> toList();
    }

    // WE WILL NOT USE THIS AT ALL, BUT FOR SAFETY
    private static class Empty implements Tree {
        @Override
        public Optional<SquareState> getElem(int i) {
            return Optional.empty();
        }

        @Override
        public Optional<Tree> setElem(int i, SquareState x) {
            return Optional.empty();
        }

        @Override
        public Optional<Tree> swap(int i, int j) {
            return Optional.empty();
        }

        @Override
        public Tree map(java.util.function.Function<SquareState, SquareState> f) {
            return this;
        }

        @Override
        public List<SquareState> toList() {
            return new ArrayList<>();
        }
    }

    private static class Leaf implements Tree {
        private final int index;
        private final SquareState value;

        public Leaf(int index, SquareState value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public Optional<SquareState> getElem(int i) {
            if (i == index) {
                return Optional.of(value);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Tree> setElem(int i, SquareState x) {
            if (i == index) {
                return Optional.of(new Leaf(index, x));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Tree> swap(int i, int j) {
            if ((i == index || j == index) && i != j) {
                // Can't swap in a leaf - need both elements
                return Optional.empty();
            }
            return Optional.empty();
        }

        @Override
        public Tree map(Function<SquareState, SquareState> f) {
            return new Leaf(index, f.apply(value));
        }

        @Override
        public List<SquareState> toList() {
            List<SquareState> result = new ArrayList<>();
            result.add(value);
            return result;
        }
    }

    private static class Parent implements Tree {
        private final int splitIndex;
        private final Tree left;
        private final Tree right;

        public Parent(int splitIndex, Tree left, Tree right) {
            this.splitIndex = splitIndex;
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<SquareState> getElem(int i) {
            if (i < splitIndex) {
                return left.getElem(i);
            } else {
                return right.getElem(i);
            }
        }

        @Override
        public Optional<Tree> setElem(int i, SquareState x) {
            if (i < splitIndex) {
                return left.setElem(i, x).map(newLeft -> new Parent(splitIndex, newLeft, right));
            } else {
                return right.setElem(i, x).map(newRight -> new Parent(splitIndex, left, newRight));
            }
        }

        @Override
        public Optional<Tree> swap(int i, int j) {
            Optional<SquareState> xi = getElem(i);
            Optional<SquareState> xj = getElem(j);

            if (xi.isEmpty() || xj.isEmpty()) {
                return Optional.empty();
            }

            Optional<Tree> afterI = setElem(i, xj.get());
            if (afterI.isEmpty()) {
                return Optional.empty();
            }

            return afterI.get().setElem(j, xi.get());
        }

        @Override
        public Tree map(Function<SquareState, SquareState> f) {
            return new Parent(splitIndex, left.map(f), right.map(f));
        }

        @Override
        public List<SquareState> toList() {
            List<SquareState> result = new ArrayList<>();
            result.addAll(left.toList());
            result.addAll(right.toList());
            return result;
        }
    }

    private final Tree tree;

    private BoardTree(Tree tree) {
        this.tree = tree;
    }


    public static BoardTree fromList(List<SquareState> xs) {
        return new BoardTree(fromListHelper(0, xs.size(), xs));
    }

    private static Tree fromListHelper(int i, int size, List<SquareState> ys) {
        if (ys.isEmpty()) {
            return new Empty();
        }
        if (ys.size() == 1) {
            return new Leaf(i, ys.get(0));
        }

        int m = (int) Math.ceil(Math.log(size) / Math.log(2));
        int halfn = (int) Math.pow(2, m - 1);
        int j = i + halfn;

        List<SquareState> firstHalf = ys.subList(0, halfn);
        List<SquareState> lastHalf = ys.subList(halfn, ys.size());

        return new Parent(j,
                fromListHelper(i, halfn, firstHalf),
                fromListHelper(j, size - halfn, lastHalf)
        );
    }

    public Optional<SquareState> getElem(int i) {
        return tree.getElem(i);
    }

    public Optional<BoardTree> setElem(int i, SquareState x) {
        return tree.setElem(i, x).map(BoardTree::new);
    }

    public Optional<BoardTree> swap(int i, int j) {
        return tree.swap(i, j).map(BoardTree::new);
    }

    public BoardTree map(Function<SquareState, SquareState> f) {
        return new BoardTree(tree.map(f));
    }

    public List<SquareState> toList() {
        return tree.toList();
    }
}
