package solver;

import java.util.Set;

public class PruneResult {
    public Set<int[]> changes;
    public boolean hasZero;

    public PruneResult(Set<int[]> changes, boolean hasZero) {
        this.hasZero = hasZero;
        this.changes = changes;
    }
}