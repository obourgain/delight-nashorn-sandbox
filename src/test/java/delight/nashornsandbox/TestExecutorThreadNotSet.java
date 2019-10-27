package delight.nashornsandbox;

import junit.framework.Assert;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.concurrent.*;

public class TestExecutorThreadNotSet {

    @Test
    public void executor_not_set_should_prevent_invocation_from_running_indefinitely() throws Exception {
        // We create an executor with a queue
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()
        );

        // We then use NashornSandbox to prevent the script to run indefinitely
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.setMaxCPUTime(1000); // in millis
        sandbox.setMaxMemory(1000 * 1000 * 10); // 10 MB
        sandbox.allowNoBraces(false);
        sandbox.allowPrintFunctions(true);
        sandbox.setMaxPreparedStatements(30);
        sandbox.setExecutor(executor);

        // This is our infinite script
        final String script = "function x(){while(true){}}\n";
        sandbox.eval(script);

        // We simulate a slow down that will prevent the JsEvaluator to register itself in the thread monitor in time
        // The invoke task will be but in the queue for at least 2000ms which is greater than the time the ThreadMonitor
        // waits for the JsEvaluator to register itself
        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // The invocation will return an error as the JsEvaluator was not set in time
        Invocable invocable = sandbox.getSandboxedInvocable();
        Throwable t = null;
        try {
            invocable.invokeFunction("x");
        } catch (ScriptException se) {
            t = se;
        }
        Assert.assertNotNull(t);
        Assert.assertTrue(t.getMessage().contains("Executor thread not set after"));

        // Since the thread monitor has stopped monitoring the JsEvaluator, we need to make sure the invoke was not performed
        // Before this PR, the script could run indefinitely since it was not monitored anymore
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(0, executor.getActiveCount());
    }
}
