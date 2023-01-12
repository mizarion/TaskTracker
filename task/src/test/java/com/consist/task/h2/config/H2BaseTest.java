package com.consist.task.h2.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = H2Config.class, loader =  AnnotationConfigContextLoader.class)
@TestExecutionListeners(inheritListeners = false, listeners =
        {DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public abstract class H2BaseTest {
}
