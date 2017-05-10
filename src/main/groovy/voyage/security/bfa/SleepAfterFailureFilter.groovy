package voyage.security.bfa

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(-10001)
class SleepAfterFailureFilter extends OncePerRequestFilter {
    protected static Logger LOG = LoggerFactory.getLogger(SleepAfterFailureFilter)

    @Value('${security.brute-force-attack.sleep-after-failure.enabled}')
    private boolean isEnabled

    @Value('${security.brute-force-attack.sleep-after-failure.http-status-failure-list}')
    private int[] httpStatusList = [401, 403, 404]

    @Value('${security.brute-force-attack.sleep-after-failure.min-sleep-seconds}')
    private int minSleepSeconds = 3

    @Value('${security.brute-force-attack.sleep-after-failure.max-sleep-seconds}')
    private int maxSleepSeconds = 8
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isEnabled) {
            LOG.debug('SleepAfterFailureFilter is DISABLED. Skipping.')
        }

        filterChain.doFilter(request, response)

        if (isEnabled) {
            sleepAfterFailureHttpStatus(response)
        }
    }

    private void sleepAfterFailureHttpStatus(HttpServletResponse response) {
        LOG.debug('Examining the response for failure status codes...')
        if (LOG.debugEnabled) {
            LOG.debug("Found HTTP Status: ${response.status}")
        }

        if (httpStatusList.contains(response.status)) {
            if (LOG.debugEnabled) {
                LOG.debug("${response.status} is a match in the list ${httpStatusList}")
            }

            Random random = new Random()
            int sleepSeconds = random.nextInt(maxSleepSeconds)
            if (sleepSeconds < minSleepSeconds) {
                sleepSeconds = minSleepSeconds
            }

            if (LOG.debugEnabled) {
                LOG.debug("Sleeping the thread for ${sleepSeconds} seconds")
            }

            sleep(sleepSeconds * 1000)

            LOG.debug('Resuming the HTTP response filter chain')

        } else {
            LOG.debug('HTTP Status code does not match. Skipping request.')
        }
    }
}
