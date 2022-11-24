package de.kleiner3.lasertag.util.fastiter;


/**
 * Interface providing the fast block iteration method
 *
 * @author Étienne Muser
 */
public interface IFastWorldIter {
    /**
     * Iterates over every block in a predefined square of chunks.
     * Begins with 0, 0 and spirals outwards.
     * This method is highly optimized to iterate over every block as fast as possible.
     *
     * Uses a Threadpool with the fixed size of the number of available processors in the system.
     * @param iter The method called on every block. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     * @param progress The method called on every chunk after iteration over every block in this chunk has finished. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     */
    default void fastSearchBlock(IIter iter, IProgressReport progress) {
    }
}
