package aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spliterators.PageSpliterator;

@Aspect
public class SpliteratorWatchAspect {

    private static final Logger logger = LoggerFactory.getLogger(SpliteratorWatchAspect.class);

    @After("@annotation(spliteratorWatch)")
    public void watch(JoinPoint joinPoint, SpliteratorWatch spliteratorWatch) throws Throwable {
        Object target = joinPoint.getTarget();
        if (target instanceof PageSpliterator) {
            logger.info("invoke");
        }
    }
}
