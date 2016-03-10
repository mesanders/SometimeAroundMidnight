package com.sandeme.spotifychallenge.utilities
import java.security.SecureRandom
import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/9/16.
  */
class ClusteringTest extends FunSuite {
  test("Test hcluster algorithm") {
    val random = new java.security.SecureRandom
    val matrix = Array.fill(10, 20) {random.nextInt(100).toDouble}
    val labels: Array[String] = Array("Hello", "Mike", "You", "Are", "Not", "A", "Failure","Duck", "Talk", "HHAHA")
    //matrix.foreach{first => first.foreach(next => print(next + "," )); print("\n"); }
    val cluster = Clustering.hCluster(matrix)
    assert(cluster.size == 1)

  }
}
