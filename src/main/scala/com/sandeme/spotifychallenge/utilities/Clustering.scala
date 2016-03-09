package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.DblVector

import scala.collection.mutable.ArrayBuffer

/**
  * Created by sandeme on 3/9/16.
  * Found an example from Collective intelligence. THere were tons of examples using kmeans, but wanted to try this
  */
object Clustering {
  case class Bicluster(vector: DblVector, left: Option[Bicluster] = None, right: Option[Bicluster ]= None, distance:Double = 0.0, id: Option[Int] =None)

  /**
    * HCluster is a scala implementation based on the hcluster algorithm found in Collective Intelligence
    * @param rows
    * @return
    */
  def hCluster(rows: Array[Array[Double]]) = {
    val cache: scala.collection.mutable.Map[(Int, Int), Double] = scala.collection.mutable.Map()

    var idr = 0
    val clusters: scala.collection.mutable.ArrayBuffer[Bicluster] = scala.collection.mutable.ArrayBuffer()
    clusters ++ rows.map(row => new Bicluster(vector =  row, id = Option(idr)))

    var currentClusterId = -1
    while (clusters.size - 1 > 1) {
      println("Cluster Size: " + clusters.size)
      var lowestPair = (0, 1)
      var closest = Utility.correlation(clusters(0).vector, clusters(1).vector)


      // loop through each pair looking for smallest distance
      for (i <- 0 to clusters.size - 1) {
        for (j <- 1 to clusters.size - 1) {
          // cache is the cache of distance calculations
          if(cache.contains(clusters(i).id.get,clusters(j).id.get)) {
            cache((clusters(i).id.get, clusters(j).id.get)) = Utility.correlation(clusters(i).vector, clusters(j).vector)
          }

          val dist: Option[Double] = cache.get((clusters(i).id.get, clusters(j).id.get))

          if (dist.get < closest) {
            closest = dist.get
            lowestPair = (i, j)
          }
        }
      }

      // Calculate the average of the two clusters
      val arrayBuffer: ArrayBuffer[Double] = ArrayBuffer()
      for (i <- 0 to clusters(lowestPair._1).vector.size - 1) {
        arrayBuffer.append((clusters(lowestPair._1).vector(i) + clusters(lowestPair._2).vector(i)).toDouble / 2.0)
      }
      val mergedVec: DblVector = arrayBuffer.toArray

      // create new cluster
      var newCluster = Bicluster(mergedVec, left = Option(clusters(lowestPair._1)), right = Option(clusters(lowestPair._2)), distance = closest, id =Option(currentClusterId))

      currentClusterId -= 1
      clusters.remove(lowestPair._1)
      clusters.remove(lowestPair._2)
    }

    clusters(0)
  }



}
