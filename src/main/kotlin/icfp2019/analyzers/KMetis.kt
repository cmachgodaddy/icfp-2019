import galois.objects.MethodFlag
import galois.objects.graph.MorphGraph
import gmetis.main.*
import icfp2019.model.BoardCell
import icfp2019.model.Point
import org.jgrapht.Graph
import java.util.*
import kotlin.math.ln
import kotlin.math.max

fun <E> kMetis(
    graph: Graph<BoardCell, E>,
    boosters: Set<Point>,
    n: Int
): List<Set<BoardCell>> {
    val metisGraph = MetisGraph()
    metisGraph.nparts = n
    metisGraph.graph = MorphGraph.IntGraphBuilder().create()
    metisGraph.initPartWeight()

    val nodes = graph.vertexSet().mapIndexed() { idx, cell ->
        val weight = if (cell.point in boosters) 2 else 1
        val graphNode = metisGraph.graph.createNode(MetisNode(idx, weight))
        metisGraph.graph.add(graphNode, MethodFlag.NONE)
        cell to graphNode
    }.toMap()

    graph.edgeSet().forEach { edge ->
        val src = nodes.getValue(graph.getEdgeSource(edge))
        val dst = nodes.getValue(graph.getEdgeTarget(edge))
        metisGraph.graph.addEdge(src, dst, 1, MethodFlag.NONE)
    }

    kMetis(metisGraph, n)

    return nodes
        .map { entry ->
            entry.value.data.partition to entry
        }
        .groupBy { it.first }
        .values
        .map { value -> value.map { it.second.key }.toSet() }
}

private fun kMetis(metisGraph: MetisGraph, nparts: Int): MetisGraph {
    val graph = metisGraph.graph

    val coarsenTo = max(graph.size() / (40 * ln(nparts.toDouble())), (20 * nparts).toDouble()).toInt()
    var maxVertexWeight = (1.5 * (graph.size() / coarsenTo.toDouble())).toInt()
    val coarsener = Coarsener(false, coarsenTo, maxVertexWeight)
    var time = System.nanoTime()
    val mcg = coarsener.coarsen(metisGraph)!!
    time = (System.nanoTime() - time) / 1000000
    System.err.println("coarsening time: $time ms")

    metisGraph.nparts = 2
    val totalPartitionWeights = FloatArray(nparts)
    Arrays.fill(totalPartitionWeights, 1 / nparts.toFloat())
    time = System.nanoTime()
    maxVertexWeight = (1.5 * ((mcg.getGraph().size()) / Coarsener.COARSEN_FRACTION)).toInt()
    val pmetis = PMetis(20, maxVertexWeight)
    pmetis.mlevelRecursiveBisection(mcg, nparts, totalPartitionWeights, 0, 0)
    time = (System.nanoTime() - time) / 1000000
    System.err.println("initial partition time: $time ms")
    mcg.nparts = nparts
    mcg.partWeights = null
    mcg.initPartWeight()
    time = System.nanoTime()
    Arrays.fill(totalPartitionWeights, 1 / nparts.toFloat())
    val refiner = KWayRefiner()
    refiner.refineKWay(mcg, metisGraph, totalPartitionWeights, 1.03.toFloat(), nparts)
    time = (System.nanoTime() - time) / 1000000
    System.err.println("refine time: $time ms")
    return mcg
}