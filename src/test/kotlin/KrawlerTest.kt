/**
 * Created by brian.a.madden@gmail.com on 12/4/16.
 *
 * Copyright (c) <2016> <H, llc>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.nhaarman.mockito_kotlin.*
import io.thelandscape.krawler.crawler.History.KrawlHistoryIf
import io.thelandscape.krawler.crawler.KrawlConfig
import io.thelandscape.krawler.crawler.KrawlQueue.QueueEntry
import io.thelandscape.krawler.crawler.Krawler
import io.thelandscape.krawler.http.KrawlDocument
import io.thelandscape.krawler.http.KrawlUrl
import io.thelandscape.krawler.http.RequestProviderIf
import org.junit.Before
import org.junit.Test
import java.util.concurrent.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KrawlerTest {

    val exampleUrl = KrawlUrl.new("http://www.example.org")
    val mockConfig = KrawlConfig(emptyQueueWaitTime = 1)
    val mockHistory = mock<KrawlHistoryIf>()
    val mockRequests = mock<RequestProviderIf>()
    val mockThreadfactory = mock<ThreadFactory>()
    val threadpool: ExecutorService = Executors.newCachedThreadPool(mockThreadfactory)
    val mockThreadpool = mock<ThreadPoolExecutor>()

    val preparedResponse = KrawlDocument(exampleUrl, prepareResponse(200, ""))

    class testCrawler(x: KrawlConfig,
                      w: KrawlHistoryIf,
                      v: RequestProviderIf,
                      z: ExecutorService): Krawler(x, w, v, z) {
        override fun shouldVisit(url: KrawlUrl): Boolean {
            return true
        }

        override fun shouldCheck(url: KrawlUrl): Boolean {
            return false
        }

        override fun visit(url: KrawlUrl, doc: KrawlDocument) {
        }

        override fun check(url: KrawlUrl, statusCode: Int) {
        }

    }

    val realThreadpoolTestKrawler = testCrawler(mockConfig, mockHistory, mockRequests, threadpool)
    val mockThreadpoolTestKrawler = testCrawler(mockConfig, mockHistory, mockRequests, mockThreadpool)

    @Before fun setUp() {
        MockitoKotlin.registerInstanceCreator { KrawlUrl.new("") }
    }

    /**
     * Test that the seed URL is added to the krawl queue and that the threadpool is started
     */
    @Test fun testStartBlocking() {
        val url: List<String> = listOf("1", "2", "3", "4")
        whenever(mockThreadpool.isTerminated).thenReturn(true)
        mockThreadpoolTestKrawler.start(url)

        // Verify submit gets called on the threadpool for each of the URLs
        verify(mockThreadpool, times(url.size)).submit(any())
    }

    /**
     * Test that when stop is called we try to shutdown
     */
    @Test fun testStop() {
        realThreadpoolTestKrawler.stop()
        assertTrue { threadpool.isShutdown }
    }

    /**
     * Test that when shutdown is called we try to shutdownNow
     */
    @Test fun testShutdown() {
        realThreadpoolTestKrawler.shutdown()
        assertTrue { threadpool.isShutdown }
    }

    /**
     * Test the doCrawl method
     */

    @Test fun testDoCrawl() {
        // Make the hasBeenSeen return true
        whenever(mockHistory.hasBeenSeen(any())).thenReturn(false)
        // Make sure we get a request response
        whenever(mockRequests.getUrl(any())).thenReturn(preparedResponse)

        // Insert some stuff into the queue
        realThreadpoolTestKrawler.doCrawl(QueueEntry("http://www.test.com"))

        // Ensure we've called to verify this is a unique URL
        verify(mockHistory).hasBeenSeen(any())
        // Now verify that we insert the URL to the history
        verify(mockHistory).insert(any())

        // The global visit count should also be 1
        assertEquals(1, realThreadpoolTestKrawler.visitCount)
    }

}