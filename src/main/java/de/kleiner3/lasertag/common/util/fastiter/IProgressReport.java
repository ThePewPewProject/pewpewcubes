package de.kleiner3.lasertag.common.util.fastiter;

/**
 * Interface to provide callback method when a new chunk is being iterated in the fast block iteration
 *
 * @author Étienne Muser
 */
public interface IProgressReport {
    /**
     * Called after the iteration for a chunk has finished.
     * @param curr The chunk that has just finished
     * @param max The maximum number of chunks being iterated in this run
     */
    void onProgress(int curr, int max);
}
