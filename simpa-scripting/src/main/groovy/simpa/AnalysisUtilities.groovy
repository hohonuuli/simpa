package simpa

import simpa.annotation.VideoTileDateComparator
import org.mbari.movie.Timecode
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.data.xy.XYSeries
import org.jfree.chart.plot.XYPlot
import org.jfree.ui.RectangleInsets
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.entity.StandardEntityCollection
import simpa.core.MosaicAssembly
import org.jfree.chart.ChartFactory
import java.awt.Color
import org.jfree.chart.ChartUtilities

/**
 *
 *
 * @author brian
 * @since May 7, 2009 12:36:50 PM
 */

public class AnalysisUtilities {

    static void createTimeDiffPlot(MosaicAssembly mosaicAssembly, Double framerate, File imageTarget) {

        def videoTiles = mosaicAssembly.getTiles();
        Collections.sort(videoTiles, new VideoTileDateComparator())

        /* ****************************************************************************
         * Generated time diffs
         */
        def dMillisec = []                  // diff between 2 times
        def dTimecode = []                  // diff between the interpolated timecodes at those 2 times
        def n = 0..<(videoTiles.size() - 1)
        for (i in n) {
            def vd0 = videoTiles[i]
            def vd1 = videoTiles[i + 1]
            dMillisec << vd1.date.time - vd0.date.time
            def tc0 = new Timecode(vd0.videoTime.timecode, framerate)
            def tc1 = new Timecode(vd1.videoTime.timecode, framerate)
            dTimecode << tc1.diffFrames(tc0) / framerate * 1000
        }
        
        /* ****************************************************************************
         * Generated time diffs
         */
        println("dT [milliseconds]\tdTimecode [milliseconds]")
        for (j in n) {
            def dt = Math.abs(dMillisec[j] - dTimecode[j])
            println("${dMillisec[j]}\t${dTimecode[j]}\t${dt}")
        }

        // Plot the diffs
        def dataSet = new XYSeriesCollection()
        def xySeries = new XYSeries("dT")
        for (k in n) {
            xySeries.add(dMillisec[k], dTimecode[k])
        }
        dataSet.addSeries(xySeries)
        def chart = ChartFactory.createXYLineChart(
                "dT",
                "dTime",
                "dTimecode",
                dataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                false)

        XYPlot plot = chart.plot
        plot.backgroundPaint = Color.lightGray
        plot.axisOffset = new RectangleInsets(0.5, 0.5, 0.5, 0.5)
        XYLineAndShapeRenderer renderer = plot.renderer
        renderer.shapesVisible = true
        renderer.shapesFilled = true
        renderer.setSeriesLinesVisible(0, false)
        NumberAxis domainAxis = plot.domainAxis
        domainAxis.autoRange = false
        domainAxis.lowerBound = -200000
        domainAxis.upperBound = 200000
        NumberAxis rangeAxis = plot.rangeAxis
        rangeAxis.autoRange = false
        rangeAxis.lowerBound = -200000
        rangeAxis.upperBound = 200000

        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection())
        ChartUtilities.saveChartAsPNG(imageTarget, chart, 800, 800, info)
    }

}