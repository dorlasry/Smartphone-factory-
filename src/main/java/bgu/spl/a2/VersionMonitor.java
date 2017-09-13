package bgu.spl.a2;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {

    int version=0;
	/**
	 * getter
	 * @return version- the version
	 */
    public int getVersion() {
        return version;
    }

    /**
     * this method changes the version in order to announce that an important thing has changed
     */
    public synchronized void inc() {	//it's sync-analog to reason written in {@link #await(int)} method
        version++;
        notifyAll();
    }

    /**
     * this method awaits while the thread 'thinks' there was no important change, i.e. the current version wasn't changed.
     * @param version-the version to wait on
     * @throws InterruptedException - if the thread was interrupted while waiting
     */
    public synchronized void await(int version) throws InterruptedException {         //sync-so that the thread won't start the loop, and before it
        	while (this.version==version) wait();       								//starts waiting, another thread might notify in the {@link #inc()} method
    }
}
