package org.nextframework.context;

import java.util.Map;

public interface UserPersistentDataProvider {

	Map<String, String> getUserMap(String username);

}
