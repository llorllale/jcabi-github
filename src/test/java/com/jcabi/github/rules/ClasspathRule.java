/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Utility class which provides convenient methods for annotations check etc.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class ClasspathRule implements TestRule {

    /**
     * Provides all classes in package 'com.jcabi.github'.
     * @return Classes
     */
    public Collection<Class<?>> allTypes() {
        final List<ClassLoader> classloaders = new LinkedList<ClassLoader>();
        classloaders.add(ClasspathHelper.contextClassLoader());
        classloaders.add(ClasspathHelper.staticClassLoader());
        final Set<Class<?>> all = new Reflections(
            new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                .setUrls(
                    ClasspathHelper.forClassLoader(
                        classloaders.toArray(
                            new ClassLoader[classloaders.size()]
                        )
                    )
                ).filterInputsBy(
                    new FilterBuilder().include(
                        FilterBuilder.prefix("com.jcabi.github")
                    )
                )
        ).getSubTypesOf(Object.class);
        final Collection<Class<?>> result = new ArrayList<Class<?>>(0);
        result.addAll(all);
        for (final Class<?> clazz : all) {
            final String name = clazz.getName();
            if (name.endsWith("Test")
                || name.endsWith("ITCase")
                || name.contains("$")
                || name.endsWith("ClasspathRule")) {
                result.remove(clazz);
            }
        }
        return result;
    }

    @Override
    public Statement apply(final Statement statement,
        final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
            }
        };
    }
}
