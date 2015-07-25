package org.nextframework.test.context;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("ts1")
public class TestService1 implements TestIService {

}
