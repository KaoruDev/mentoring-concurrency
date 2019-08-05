package com.kaoruk.mentoring.concurrency.deadlock

/**
 * An external lock allows threads to coordinate with each other. Previously we were locking each object independently,
 * but transactionally modifying both of their state. Because we had two separate locks, there could be two
 * separate operations modifying the shared states. An external lock allows us to coordinate modifications
 * multiple states.
 */
case class Island(var itemCount: Int, lock: AnyRef) {
  // synchronized will give lock the calling thread if not held by another thread.
  // If the object's lock is currently held by a different thread, the calling thread will wait.
  def sendItemTo(island: Island): Unit = lock.synchronized {
    island.receiveItems(shipItem)
  }

  // Adding a synchronized to this method creates a potential dead lock.
  // Because more than one thread could be calling `sendItemTo` at the same time, thus preventing
  // the calling thread to hold the lock here
  def receiveItems(items: Int): Int = {
    itemCount += items
    itemCount
  }

  def shipItem: Int = lock.synchronized {
    val result = if (itemCount > 0) {
      itemCount -= 1
      1
    } else {
      throw new IllegalStateException("island has no items to ship!")
    }
    result
  }
}
