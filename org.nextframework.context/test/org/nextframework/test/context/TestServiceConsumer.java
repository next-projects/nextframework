package org.nextframework.test.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@TS1
public class TestServiceConsumer {

	@Autowired
	TestIService iService;

}
