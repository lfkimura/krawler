/**
 * Created by brian.a.madden@gmail.com on 1/18/17.
 *
 * Copyright (c) <2017> <H, llc>
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

package io.thelandscape.krawler.crawler

import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor

// TODO: If the queue is full, push URLs into HSQL?
class NoopTaskRejector : RejectedExecutionHandler {
    /**
     * Method that may be invoked by a [ThreadPoolExecutor] when
     * [execute][ThreadPoolExecutor.execute] cannot accept a
     * task.  This will occur for Krawler when no more queue slots are
     * available.
     *
     * This method will simply return, making it a no-op
     */
    override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor?) {
        return
    }

}