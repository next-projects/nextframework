package org.nextframework.test.context;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("ts2")
public class TestService2 implements TestIService {

}
