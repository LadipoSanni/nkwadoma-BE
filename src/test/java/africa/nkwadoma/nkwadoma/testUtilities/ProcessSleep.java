package africa.nkwadoma.nkwadoma.testUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessSleep {
    /**
     * Delays execution for the specified number of seconds,
     * printing each second as it passes.
     *
     * @param seconds The number of seconds to delay.
     */
    public static void delayWithCountdown(int seconds) {
        log.info("Delay count down method called for {} seconds", seconds);
        for (int i = 1; i <= seconds; i++) {
            try {
                Thread.sleep(1000);
                log.info("Sleeping ... {} second{} elapsed...", i, i > 1 ? "s" : "");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
               log.error("Countdown interrupted!");
                break;
            }
        }
    }

}
