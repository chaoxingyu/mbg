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
package org.mybatis.generator.api.dom.kotlin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FullyQualifiedKotlinTypeTest {

    @Test
    public void testKotlinPrimitive() {
        FullyQualifiedKotlinType fqjt =
                new FullyQualifiedKotlinType("kotlin.String"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("String"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
    }

    @Test
    public void testKotlinPrimitive2() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("String"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("String"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
    }

    @Test
    public void testSimpleType() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("com.foo.Bar"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("Bar"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(1);
        assertThat(fqjt.getImportList()).contains("com.foo.Bar");
    }

    @Test
    public void testSimpleType2() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("com.foo.bar"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("bar"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(1);
        assertThat(fqjt.getImportList()).contains("com.foo.bar"); // $NON-NLS-1$
    }

    @Test
    public void testGenericType1() {
        FullyQualifiedKotlinType fqjt =
                new FullyQualifiedKotlinType(
                        "kotlin.collections.List<kotlin.String>"); //$NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("List<String>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
        ;
    }

    @Test
    public void testGenericType2() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("List<String>"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("List<String>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
        ;
    }

    @Test
    public void testGenericType3() {
        FullyQualifiedKotlinType fqjt =
                new FullyQualifiedKotlinType(
                        "kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>"); //$NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments())
                .isEqualTo("Map<String, List<String>>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
    }

    @Test
    public void testGenericType4() {
        FullyQualifiedKotlinType fqjt =
                new FullyQualifiedKotlinType("List<Map<String, String>>"); // $NON-NLS-1$
        assertThat(fqjt.getShortNameWithTypeArguments())
                .isEqualTo("List<Map<String, String>>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).isEmpty();
    }

    @Test
    public void testUppercasePackage1() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("org.foo.Bar.Inner");
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("Inner"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(1);
        assertThat(fqjt.getImportList()).contains("org.foo.Bar.Inner");
    }

    @Test
    public void testUppercasePackage2() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("org.foo.Bar.Inner.Inner");
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("Inner"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(1);
        assertThat(fqjt.getImportList()).contains("org.foo.Bar.Inner.Inner");
    }

    @Test
    public void testUppercasePackage3() {
        FullyQualifiedKotlinType fqjt =
                new FullyQualifiedKotlinType("java.util.List<org.foo.Bar.Inner>");
        assertThat(fqjt.getShortNameWithTypeArguments()).isEqualTo("List<Inner>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(2);
        assertThat(fqjt.getImportList()).contains("java.util.List", "org.foo.Bar.Inner");
    }

    @Test
    public void testLateInitialization1() {
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("List");
        fqjt.addTypeArgument(new FullyQualifiedKotlinType("java.math.BigDecimal"));
        assertThat(fqjt.getShortNameWithTypeArguments())
                .isEqualTo("List<BigDecimal>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(1);
        assertThat(fqjt.getImportList()).contains("java.math.BigDecimal");
    }

    @Test
    public void testLateInitialization2() {
        FullyQualifiedKotlinType inner = new FullyQualifiedKotlinType("some.generic.Thing");
        inner.addTypeArgument(new FullyQualifiedKotlinType("java.math.BigDecimal"));
        FullyQualifiedKotlinType fqjt = new FullyQualifiedKotlinType("kotlin.List");
        fqjt.addTypeArgument(inner);
        assertThat(fqjt.getShortNameWithTypeArguments())
                .isEqualTo("List<Thing<BigDecimal>>"); // $NON-NLS-1$
        assertThat(fqjt.getImportList()).hasSize(2);
        assertThat(fqjt.getImportList()).contains("java.math.BigDecimal", "some.generic.Thing");
    }
}
