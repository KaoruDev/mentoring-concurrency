package com.kaoruk.mentoring.concurrency.deadlock

import java.util.concurrent.ThreadLocalRandom

/**
 * This program simulates two "islands" exchanging goods in a concurrent fashion.
 *
 * The expected output is that Island One has 300 items and island two has 700.
 *
 * Unfortunately due to lack of a proper locking mechanism, the results vary.
 *
 * Solve the problem by modifying the Island class.
 *
 */
object Runner extends App {
  case object Lock
  val islandOne = Island(500, Lock)
  val islandTwo = Island(500, Lock)

  // Sleep creates some indeterminism
  val islandOneShipments = (1 to 40).map(_ => {
    new Thread(() => {
      (1 to 10).foreach(_ => {
        Thread.sleep(ThreadLocalRandom.current().nextInt(10))
        islandOne.sendItemTo(islandTwo)
      })
    })
  })

  val islandTwoShipments = (1 to 20).map(_ => {
    new Thread(() => {
      (1 to 10).foreach(_ => {
        Thread.sleep(ThreadLocalRandom.current().nextInt(10))
        islandTwo.sendItemTo(islandOne)
      })
    })
  })

  // Starts threads -- similar to creating futures
  islandOneShipments.foreach(_.start)
  islandTwoShipments.foreach(_.start)

  // Waits for threads to finish -- similar to calling Await on a future
  islandOneShipments.foreach(_.join)
  islandTwoShipments.foreach(_.join)

  println(s"islandOne has ${islandOne.itemCount}")
  println(s"islandTwo has ${islandTwo.itemCount}")
}
