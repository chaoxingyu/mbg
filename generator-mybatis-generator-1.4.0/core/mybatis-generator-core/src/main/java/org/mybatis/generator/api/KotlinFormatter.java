/**
 * Copyright 2006-2020 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.api;

import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.config.Context;

/**
 * Objects implementing this interface are used to convert the internal representation of the Kotlin
 * DOM classes into a string suitable for saving to the file system. Note that the string generated
 * by this class will be saved directly to the file system with no additional modifications.
 *
 * <p>Only one instance of the class will be created in each context. Configuration can be passed
 * into the class through the use of properties in the Context.
 *
 * @author Jeff Butler
 */
public interface KotlinFormatter {
    void setContext(Context context);

    String getFormattedContent(KotlinFile kotlinFile);
}
